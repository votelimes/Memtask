package com.example.clock.fragments;

import androidx.core.util.Pair;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.clock.R;
import com.example.clock.app.App;
import com.example.clock.databinding.StatisticFragmentBinding;
import com.example.clock.model.UserCaseStatistic;
import com.example.clock.viewmodels.StatisticViewModel;
import com.example.clock.viewmodels.ViewModelFactoryBase;
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
import java.util.List;
import java.util.Random;

public class StatisticFragment extends Fragment {

    private StatisticViewModel mViewModel;
    private BarChart chart1;
    private StatisticFragmentBinding binding;
    private TextInputEditText chart1RangeStart;
    private TextInputEditText chart1RangeEnd;

    public static StatisticFragment newInstance() {
        return new StatisticFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.statistic_fragment, container, false);
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

        MaterialToolbar toolbar = getActivity().findViewById(R.id.toolbar);
        toolbar.findViewById(R.id.action_search).setVisibility(View.GONE);
        toolbar.setTitle("Статистика");

        mViewModel.getStatPoolLiveData().observe(getViewLifecycleOwner(), statPoolObserver);
        chart1 = (BarChart) getView().findViewById(R.id.stat_chart1);
        chart1.setScaleEnabled(false);
        chart1.getDescription().setEnabled(false);

        chart1RangeStart = (TextInputEditText) getView().findViewById(R.id.stat_interval1_start_text);
        chart1RangeEnd = (TextInputEditText) getView().findViewById(R.id.stat_interval1_end_text);

        chart1RangeStart.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b){
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
    final Observer<List<UserCaseStatistic>> statPoolObserver = new Observer<List<UserCaseStatistic>>() {
        @Override
        public void onChanged(List<UserCaseStatistic> data) {
            mViewModel.init();
            BarDataSet bds = new BarDataSet(mViewModel.getChart1Entries(), "Отношение выполненных задач к общему количеству по периодам");
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
        }
    };

}