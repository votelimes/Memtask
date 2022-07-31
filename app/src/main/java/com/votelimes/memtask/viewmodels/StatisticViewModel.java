package com.votelimes.memtask.viewmodels;

import android.app.Application;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.lifecycle.LiveData;

import com.votelimes.memtask.BR;
import com.votelimes.memtask.app.App;
import com.votelimes.memtask.model.UserCaseStatistic;
import com.votelimes.memtask.repositories.MemtaskRepositoryBase;
import com.votelimes.memtask.storageutils.Database;
import com.votelimes.memtask.storageutils.LiveDataTransformations;
import com.votelimes.memtask.storageutils.SilentDatabase;
import com.votelimes.memtask.storageutils.Tuple3;
import com.github.mikephil.charting.data.BarEntry;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class StatisticViewModel extends MemtaskViewModelBase {
    private final long millisInDay = 24*60*60*1000;
    LiveData<List<UserCaseStatistic>> mStatPool;

    LiveData<List<Integer>> mTaskStat;
    LiveData<List<Long>> mProjectStat;

    public LiveData<Tuple3<List<Integer>, List<Long>, List<UserCaseStatistic>>> intermediate;

    private List<BarEntry> chart1Entries;
    private List<String> chart1XAxisNames;
    int numberOfDivisionsChart1 = 6;
    public Observer mDataHolder;


    public StatisticViewModel(Application application, Database database, SilentDatabase silentDatabase){
        mDataHolder = new Observer();
        loadData(application, database, silentDatabase);
        chart1Entries = new ArrayList<>();
    }

    @Override
    protected void loadData(Application application, Database database, SilentDatabase silentDatabase){
        mRepository = new MemtaskRepositoryBase(database, silentDatabase);
        tasksLiveData = null;
        projectsLiveData = null;
        categoriesLiveData = null;
        themesLiveData = null;
        mStatPool = mRepository.getUserCaseStatistic
                (mDataHolder.getStartChart1RangeLong()*1000,
                        mDataHolder.getEndChart1RangeLong()*1000);
        mTaskStat = mRepository.getTaskRepeatModeStatistic();
        mProjectStat = mRepository.getProjectTimeCreatedStatistic();

        intermediate = LiveDataTransformations.ifNotNull(mTaskStat, mProjectStat, mStatPool);
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

    public LiveData<Tuple3<List<Integer>, List<Long>, List<UserCaseStatistic>>> updatePool(){
        mStatPool = mRepository.getUserCaseStatistic
                (mDataHolder.getStartChart1RangeLong()*1000,
                        mDataHolder.getEndChart1RangeLong()*1000);
        intermediate = LiveDataTransformations.ifNotNull(mTaskStat, mProjectStat, mStatPool);
        return intermediate;
    }

    public int getCompletedCount(){
        if(mStatPool.getValue() != null) {
            return (int) mStatPool.getValue().stream().filter(UserCaseStatistic::isStateCompleted).count();
        }
        return 0;
    }

    public int getExpiredCount(){
        if(mStatPool.getValue() != null) {
            return (int) mStatPool.getValue().stream().filter(UserCaseStatistic::isStateExpired).count();
        }
        return 0;
    }

    public int getSummary(){
        int c1 = mTaskStat.getValue().size();
        return c1 + mProjectStat.getValue().size();
    }

    public int getRepeating(){
        return (int) mTaskStat.getValue().stream().filter(item -> item > 0).count();
    }

    public String getUsageTime(Context context){
        UsageStatsManager mUsageStatsManager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
        Map<String, UsageStats> lUsageStatsMap = mUsageStatsManager.queryAndAggregateUsageStats(0, System.currentTimeMillis());
        long ut = lUsageStatsMap.get("com.example.clock").getTotalTimeInForeground();

        long hours = TimeUnit.MILLISECONDS.toHours(ut);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(ut) -
                TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(ut));
        if(App.getSettings().TESTING){
            hours += 2;
        }

        return String.valueOf(hours) + " часов, " + String.valueOf(minutes) + " минут";
    }

    public String getUsageTimePerWeek(Context context){
        UsageStatsManager mUsageStatsManager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
        long currentTime = System.currentTimeMillis();
        long lastWeek = currentTime - (1000*60*24*7);

        Map<String, UsageStats> lUsageStatsMap = mUsageStatsManager.queryAndAggregateUsageStats(lastWeek,currentTime);
        long ut = lUsageStatsMap.get("com.example.clock").getTotalTimeInForeground();

        long hours = TimeUnit.MILLISECONDS.toHours(ut);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(ut) -
                TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(ut));

        return String.valueOf(hours) + " часов, " + String.valueOf(minutes) + " минут";
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

    public LiveData<List<Integer>> getmTaskStat() {
        return mTaskStat;
    }

    public LiveData<List<Long>> getmProjectStat() {
        return mProjectStat;
    }

    public LiveData<Tuple3<List<Integer>, List<Long>, List<UserCaseStatistic>>> getIntermediate() {
        return intermediate;
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