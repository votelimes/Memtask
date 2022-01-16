package com.example.clock.viewmodels;

import android.app.Application;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.lifecycle.LiveData;

import com.example.clock.BR;
import com.example.clock.model.UserCaseStatistic;
import com.example.clock.repositories.MemtaskRepositoryBase;
import com.example.clock.storageutils.Database;
import com.github.mikephil.charting.data.BarEntry;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class StatisticViewModel extends MemtaskViewModelBase {
    private final long millisInDay = 24*60*60*1000;
    LiveData<List<UserCaseStatistic>> mStatPool;

    private List<BarEntry> chart1Entries;
    private List<String> chart1XAxisNames;
    int numberOfDivisionsChart1 = 6;
    public Observer mDataHolder;


    public StatisticViewModel(Application application, Database database, Database silentDatabase){
        mDataHolder = new Observer();
        loadData(application, database, silentDatabase);
        chart1Entries = new ArrayList<>();
    }

    @Override
    protected void loadData(Application application, Database database, Database silentDatabase){
        mRepository = new MemtaskRepositoryBase(application, database, silentDatabase);
        tasksLiveData = null;
        projectsLiveData = null;
        categoriesLiveData = null;
        themesLiveData = null;
        mStatPool = mRepository.getUserCaseStatistic
                (mDataHolder.getStartChart1RangeLong()*1000,
                        mDataHolder.getEndChart1RangeLong()*1000);
    }

    public LiveData<List<UserCaseStatistic>> getStatPoolLiveData(){
        return mStatPool;
    }

    public void init(){
        calcChart1Data();
    }

    public List<BarEntry> getChart1Entries() {
        return chart1Entries;
    }

    public List<String> getChart1XAxisNames() {
        return chart1XAxisNames;
    }

    public LiveData<List<UserCaseStatistic>> updatePool(){
        mStatPool = mRepository.getUserCaseStatistic
                (mDataHolder.getStartChart1RangeLong()*1000,
                        mDataHolder.getEndChart1RangeLong()*1000);
        return mStatPool;
    }

    //Private
    private void calcChart1Data(){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yy");
        long currentDiv = mDataHolder.getStartChart1RangeLong();
        chart1XAxisNames = new ArrayList<>(numberOfDivisionsChart1);
        chart1Entries = new ArrayList<>();
        long divSecValue = ((mDataHolder.getEndChart1RangeLong() - mDataHolder.getStartChart1RangeLong()) / numberOfDivisionsChart1);

        for(int i = 0; i < numberOfDivisionsChart1; i++){
            AtomicInteger expiredCount = new AtomicInteger();
            AtomicInteger completedCount = new AtomicInteger();

            long finalCurrentDiv = currentDiv;

            mStatPool.getValue().forEach(item -> {
                if(((long)item.getMillisRecordDateTime() / 1000) >= finalCurrentDiv
                        && ((long)item.getMillisRecordDateTime() / 1000) < finalCurrentDiv + divSecValue){
                    if(item.isStateCompleted()){
                        completedCount.getAndIncrement();
                    }
                    if(item.isStateExpired()){
                        expiredCount.getAndIncrement();
                    }
                }
            });
            currentDiv += divSecValue;
            chart1Entries.add(new BarEntry((float) i, (float) expiredCount.get() / (completedCount.get() + expiredCount.get())));
            chart1XAxisNames.add(LocalDateTime.ofEpochSecond(currentDiv, 0, ZoneOffset.UTC).format(dtf));
        }
    }

    //Obs
    public static class Observer extends BaseObservable {
        private long epochSecondPeriodStart = 0;
        private long epochSecondPeriodEnd = 0;
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy");


        Observer(long start, long end){
            epochSecondPeriodStart = (long) start / 1000;
            epochSecondPeriodEnd = (long) end / 1000;
        }

        Observer(){
            epochSecondPeriodEnd = LocalDateTime.now(ZoneOffset.UTC).toEpochSecond(ZoneOffset.UTC);
            epochSecondPeriodStart = LocalDateTime.now(ZoneOffset.UTC).minusMonths(2).toEpochSecond(ZoneOffset.UTC);
        }

        public void setStartChart1Range(long rms){
            epochSecondPeriodStart = (long) rms / 1000;
            notifyPropertyChanged(BR.startChart1Range);
        }

        public void setEndChart1Range(long rme){
            epochSecondPeriodEnd = (long) rme / 1000;
            notifyPropertyChanged(BR.endChart1Range);
        }

        @Bindable
        public String getStartChart1Range(){
            return LocalDateTime.ofEpochSecond(epochSecondPeriodStart, 0, ZoneOffset.UTC).format(dtf);
        }
        @Bindable
        public String getEndChart1Range(){
            return LocalDateTime.ofEpochSecond(epochSecondPeriodEnd, 0, ZoneOffset.UTC).format(dtf);
        }

        public long getStartChart1RangeLong(){
            return epochSecondPeriodStart;
        }
        public long getEndChart1RangeLong(){
            return epochSecondPeriodEnd;
        }

    }
}