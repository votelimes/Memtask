package com.votelimes.memtask.fragments;

import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Pair;
import androidx.databinding.DataBindingUtil;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.votelimes.memtask.R;
import com.votelimes.memtask.app.App;
import com.votelimes.memtask.databinding.FragmentStatisticBinding;
import com.votelimes.memtask.model.UserCaseStatistic;
import com.votelimes.memtask.storageutils.Tuple3;
import com.votelimes.memtask.viewmodels.StatisticViewModel;
import com.votelimes.memtask.viewmodels.ViewModelFactoryBase;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

public class StatisticFragment extends Fragment {

    private StatisticViewModel mViewModel;
    private BarChart chart1;
    private FragmentStatisticBinding binding;
    private TextInputEditText chart1RangeStart;
    private TextInputEditText chart1RangeEnd;
    private TextView comp;
    private TextView exp;
    private TextView all;
    private TextView rep;
    private TextView allTime;
    private TextView perWeekTime;
    public DrawerLayout drawerLayout;
    private int mode = -500;

    final ActivityResultLauncher<Intent> activityLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if(getUsageStatsList(getContext()).isEmpty() == false){
                    mode = 1;
                }
            });

    public static StatisticFragment newInstance() {
        return new StatisticFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_statistic, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

        ViewModelFactoryBase mFactory = new ViewModelFactoryBase(this
                .getActivity()
                .getApplication(), App.getDatabase(), App.getSilentDatabase());

        mViewModel = new ViewModelProvider(getActivity(), mFactory).get(StatisticViewModel.class);
        binding.setVm(mViewModel);
        drawerLayout = (DrawerLayout) getActivity().findViewById(R.id.drawerLayout);


        try {
            MaterialToolbar toolbar = getActivity().findViewById(R.id.toolbar);
            toolbar.findViewById(R.id.action_search).setVisibility(View.GONE);
            toolbar.setTitle("Статистика");
        }
        catch (Exception ignoring){

        }

        mViewModel.getIntermediate().observe(getViewLifecycleOwner(), statPoolObserver);
        chart1 = (BarChart) getView().findViewById(R.id.stat_chart1);
        chart1.setScaleEnabled(false);
        chart1.getDescription().setEnabled(false);

        chart1RangeStart = (TextInputEditText) getView().findViewById(R.id.stat_interval1_start_text);
        chart1RangeEnd = (TextInputEditText) getView().findViewById(R.id.stat_interval1_end_text);


        final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        chart1RangeStart.clearFocus();
                        chart1RangeStart.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                            @Override
                            public void onFocusChange(View view, boolean b) {
                                if(b && false){
                                    if(drawerLayout.isOpen()){
                                        view.clearFocus();
                                        return;
                                    }
                                    long millisInDay = 24 * 60 * 60 * 1000;
                                    MaterialDatePicker datepicker = MaterialDatePicker
                                            .Builder
                                            .dateRangePicker()
                                            .setTitleText("Интервал статистики")
                                            .setSelection(
                                                    new Pair<>(
                                                            mViewModel.mDataHolder.getStartChart1RangeLong() * 1000,
                                                            mViewModel.mDataHolder.getEndChart1RangeLong() * 1000
                                                    )
                                            )
                                            .build();

                                    datepicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Pair<Long, Long>>() {
                                        @Override
                                        public void onPositiveButtonClick(Pair<Long, Long> selection) {

                                            mViewModel.mDataHolder.setStartChart1Range(selection.first);
                                            mViewModel.mDataHolder.setEndChart1Range(selection.second);
                                            mViewModel.getStatPoolLiveData().removeObservers(getViewLifecycleOwner());
                                            mViewModel.updatePool().observe(getViewLifecycleOwner(), statPoolObserver);
                                            datepicker.dismiss();
                                        }
                                    });
                                    datepicker.show(getParentFragmentManager(), datepicker.toString());
                                    view.clearFocus();
                                }
                            }
                        });
                        chart1RangeEnd.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                            @Override
                            public void onFocusChange(View view, boolean b) {
                                if(b){
                                    if(drawerLayout.isOpen()){
                                        view.clearFocus();
                                        return;
                                    }
                                    MaterialDatePicker datepicker = MaterialDatePicker
                                            .Builder
                                            .dateRangePicker()
                                            .setTitleText("Интервал статистики")
                                            .setSelection(
                                                    new Pair<>(
                                                            mViewModel.mDataHolder.getStartChart1RangeLong() * 1000,
                                                            mViewModel.mDataHolder.getEndChart1RangeLong() * 1000
                                                    )
                                            )
                                            .build();

                                    datepicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Pair<Long, Long>>() {
                                        @Override
                                        public void onPositiveButtonClick(Pair<Long, Long> selection) {
                                            mViewModel.mDataHolder.setStartChart1Range(selection.first);
                                            mViewModel.mDataHolder.setEndChart1Range(selection.second);
                                            mViewModel.getStatPoolLiveData().removeObservers(getViewLifecycleOwner());
                                            mViewModel.updatePool().observe(getViewLifecycleOwner(), statPoolObserver);
                                            datepicker.dismiss();
                                        }
                                    });
                                    datepicker.show(getParentFragmentManager(), datepicker.toString());
                                    view.clearFocus();
                                }
                            }
                        });
                    }
                }, 200);


        chart1.getLegend().setTextColor(getResources().getColor(R.color.textPrimary));

        comp = getView().findViewById(R.id.stat1_val);
        exp = getView().findViewById(R.id.stat2_val);
        all = getView().findViewById(R.id.stat3_val);
        rep = getView().findViewById(R.id.stat4_val);
        allTime = getView().findViewById(R.id.stat5_val);
        perWeekTime = getView().findViewById(R.id.stat6_val);

        //Check if permission enabled
        if (getUsageStatsList(getContext()).isEmpty()){
            Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
            startActivity(intent);
        }
        else{
            mode = 1;
        }
    }
    final Observer<Tuple3<List<Integer>, List<Long>, List<UserCaseStatistic>>> statPoolObserver = new Observer<Tuple3<List<Integer>, List<Long>, List<UserCaseStatistic>>>() {
        @Override
        public void onChanged(Tuple3<List<Integer>, List<Long>, List<UserCaseStatistic>> data) {
            mViewModel.init();
            BarDataSet bds = new BarDataSet(mViewModel.getChart1Entries(), "Успешность выполнения задач");
            BarData bd = new BarData(bds);
            List<Integer> colorList = new ArrayList<>(mViewModel.getChart1Entries().size());
            Random random = new Random();
            for(int i = 0; i < mViewModel.getChart1Entries().size(); i++){
                colorList.add(Color.argb(255, random.nextInt(256), random.nextInt(256), random.nextInt(256)));
            }
            bds.setColors(colorList);
            chart1.setData(bd);
            XAxis xAxis = chart1.getXAxis();
            xAxis.setValueFormatter(new ValueFormatter() {
                @Override
                public String getAxisLabel(float value, AxisBase axis) {
                    return mViewModel.getChart1XAxisNames().get((int) value);
                }
            });
            chart1.invalidate();

            comp.setText(String.valueOf(mViewModel.getCompletedCount()));
            exp.setText(String.valueOf(mViewModel.getExpiredCount()));

            all.setText(String.valueOf(mViewModel.getSummary()));
            rep.setText(String.valueOf(mViewModel.getRepeating()));

            if(mode != -500) {
                allTime.setText(String.valueOf(mViewModel.getUsageTime(getContext())));
                perWeekTime.setText(String.valueOf(mViewModel.getUsageTimePerWeek(getContext())));
            }
            else{
                allTime.setText(String.valueOf(0));
                perWeekTime.setText(String.valueOf(0));
            }
        }
    };
    public static List<UsageStats> getUsageStatsList(Context context){
        UsageStatsManager usm = getUsageStatsManager(context);
        Calendar calendar = Calendar.getInstance();
        long endTime = calendar.getTimeInMillis();
        calendar.add(Calendar.YEAR, -1);
        long startTime = calendar.getTimeInMillis();

        List<UsageStats> usageStatsList = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY,startTime,endTime);
        return usageStatsList;
    }
    @SuppressWarnings("ResourceType")
    private static UsageStatsManager getUsageStatsManager(Context context){
        UsageStatsManager usm = (UsageStatsManager) context.getSystemService("usagestats");
        return usm;
    }
}