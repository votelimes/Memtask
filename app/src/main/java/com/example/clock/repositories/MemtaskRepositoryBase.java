package com.example.clock.repositories;

import android.content.Context;

import androidx.core.util.Pair;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import com.example.clock.app.App;
import com.example.clock.dao.CategoryDao;
import com.example.clock.dao.ProjectDao;
import com.example.clock.dao.TaskDao;
import com.example.clock.dao.ThemeDao;
import com.example.clock.dao.UserCaseStatisticDao;
import com.example.clock.model.Category;
import com.example.clock.model.Project;
import com.example.clock.model.ProjectAndTheme;
import com.example.clock.model.ProjectData;
import com.example.clock.model.Task;
import com.example.clock.model.TaskAndTheme;
import com.example.clock.model.TaskData;
import com.example.clock.model.Theme;
import com.example.clock.model.UserCaseStatistic;
import com.example.clock.services.CalendarProvider;
import com.example.clock.storageutils.Database;
import com.example.clock.storageutils.LiveDataTransformations;
import com.example.clock.storageutils.SilentDatabase;
import com.example.clock.storageutils.Tuple2;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.CalendarList;
import com.google.api.services.calendar.model.CalendarListEntry;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class MemtaskRepositoryBase {

    private final Database mDatabase;
    private final SilentDatabase mSilentDatabase;
    private final TaskDao mTaskDao;
    private final TaskDao mSilentTaskDao;
    private final ProjectDao mProjectDao;
    private final ProjectDao mSilentProjectDao;
    private final CategoryDao mCategoryDao;
    private final ThemeDao mThemeDao;
    private final UserCaseStatisticDao mUserCaseStatisticDao;
    private List<OuterCalendar> calendars = null;
    private List<OuterEvent> events = null;
    private Thread syncThread;

    public MemtaskRepositoryBase(Database database, SilentDatabase silentDatabase){
        this.mDatabase = database;
        this.mSilentDatabase = silentDatabase;

        mTaskDao = mDatabase.taskDao();
        mProjectDao = mDatabase.projectDao();
        mCategoryDao = mDatabase.categoryDao();
        mThemeDao = mDatabase.themeDao();
        mSilentTaskDao = mSilentDatabase.taskDao();
        mSilentProjectDao = mSilentDatabase.projectDao();
        mUserCaseStatisticDao = mDatabase.userCaseStatisticDao();
    }

    //Getting existing data

    public LiveData<List<Task>> getAllTasksLive(){
        return this.mDatabase.taskDao().getTasksLiveData();
    }

    public LiveData<List<Project>> getAllProjectsLive(){
        return this.mDatabase.projectDao().getProjectsLiveData();
    }

    public LiveData<List<Category>> getAllCategoriesLive(){
        return this.mDatabase.categoryDao().getCategoriesLiveData();
    }

    public LiveData<List<Theme>> getAllThemesLive(){
        return this.mDatabase.themeDao().getThemesLiveData();
    }

    public LiveData<List<TaskData>> getTasksByNotification(long startMillis, long endMillis){
        return this.mDatabase.taskDao() .getTasksWithThemeByNotification(startMillis, endMillis);
    }

    public LiveData<List<TaskData>> getTasksByNotificationByName(long startMillis, long endMillis, String name){
        return this.mDatabase.taskDao() .getTasksWithThemeByNotificationByName(startMillis, endMillis, "%" + name + "%");
    }

    public LiveData<TaskData> getTaskAndTheme(String taskID){
        return this.mDatabase.taskDao().getTaskThemeLiveData(taskID);
    }

    public LiveData<ProjectAndTheme> getProjectAndTheme(String projectID){
        return this.mDatabase.projectDao().getProjectThemeLiveData(projectID);
    }

    public LiveData<List<TaskData>> getTaskDataByCategory(String ID){
        return this.mDatabase.taskDao().getTasksWithThemeByNotification(ID);
    }

    public LiveData<List<ProjectData>> getProjectDataByCategory(String ID){
        return this.mDatabase.projectDao().getProjectsDataByCat(ID);
    }

    public LiveData<List<ProjectData>> getProjectDataByCategoryByName(String ID, String name){
        return this.mDatabase.projectDao().getProjectsDataByCatByName(ID, "%" + name + "%");
    }

    public LiveData<List<UserCaseStatistic>> getUserCaseStatistic(long rangeStartMillis, long rangeEndMillis){
        return this.mDatabase.userCaseStatisticDao().getUserCaseStatistic(rangeStartMillis, rangeEndMillis);
    }

    public LiveData<List<TaskData>> getSingleTasksByCategoryLiveData(String categoryID){
        return this.mDatabase.taskDao().getSingleTasksWithThemeLiveData(categoryID);
    }

    public LiveData<List<TaskData>> getProjectTasksByCategoryLiveData(String categoryID){
        return this.mDatabase.taskDao().getProjectTasksWithThemeLiveData(categoryID);
    }

    public Task getTask(String taskID){
        return this.mDatabase.taskDao().getTask(taskID);
    }

    public Task getTaskSilently(String taskID){
        return this.mSilentDatabase.taskDao().getTask(taskID);
    }

    public LiveData<Task> getTaskLiveData(String taskID){
        return this.mDatabase.taskDao().getTaskLiveData(taskID);
    }

    public LiveData<List<TaskAndTheme>> getTaskAndThemeByCategory(String categoryID){
        return mTaskDao.getSingleTaskAndThemeByCategory(categoryID);
    }



    // Adding new data
    public void addUserCaseStatisticSilently(UserCaseStatistic ucs){
        mSilentDatabase.databaseWriteExecutor.execute(() -> {
            mUserCaseStatisticDao.insert(ucs);
        });
    }

    public void addTask (Task newTask) {
        Database.databaseWriteExecutor.execute(() -> {
            mTaskDao.insertWithReplace(newTask);
        });
    }

    public void addTaskSilently(Task newTask){
        Database.databaseWriteExecutor.execute(() -> {
            mSilentTaskDao.insertWithReplace(newTask);
        });
    }

    public void addProject (Project newProject) {
        Database.databaseWriteExecutor.execute(() -> {
            mProjectDao.insertWithReplace(newProject);
        });
    }

    public void addProjectSilently(Project newProject){
        Database.databaseWriteExecutor.execute(() -> {
            mSilentProjectDao.insertWithReplace(newProject);
        });
    }

    public void addCategory (Category newCategory){
        Database.databaseWriteExecutor.execute(() -> {
            mCategoryDao.insertWithReplace(newCategory);
        });
    }

    public void addTheme(Theme theme){
        Database.databaseWriteExecutor.execute(() -> {
            mThemeDao.insertWithReplace(theme);
        });
    }

    //Removing existing data
    public void removeTask (Task removableTask) {
        Database.databaseWriteExecutor.execute(() -> {
            mTaskDao.delete(removableTask);
        });
    }

    public void removeTaskByID (String id){
        Database.databaseWriteExecutor.execute(() -> {
            mTaskDao.deleteById(id);
        });
    }

    public void removeTaskByIDSilently(String id){
        mSilentDatabase.databaseWriteExecutor.execute(() -> {
            mSilentTaskDao.deleteById(id);
        });
    }

    public void removeProject (String id) {
        Database.databaseWriteExecutor.execute(() -> {
            mProjectDao.deleteWithItemsTransaction(id);
        });
    }

    public void removeProjectByID (String id){
        Database.databaseWriteExecutor.execute(() -> {
            mProjectDao.deleteById(id);
        });
    }

    public void removeProjectByIDSilently(String id){
        mSilentDatabase.databaseWriteExecutor.execute(() -> {
            mSilentProjectDao.deleteById(id);
        });
    }

    public void removeCategory (Category removableCategory) {
        Database.databaseWriteExecutor.execute(() -> {
            mCategoryDao.delete(removableCategory);
        });
    }

    public void removeCategoryByID (String id){
        Database.databaseWriteExecutor.execute(() -> {
            mCategoryDao.delete(id);
        });
    }

    public void removeThemeByID (long id){
        Database.databaseWriteExecutor.execute(() -> {
            mThemeDao.deleteByID(id);
        });
    }

    public void removeCategoryWithItems(String id){
        Database.databaseWriteExecutor.execute(() -> {
            mCategoryDao.deleteWithItemsTransaction(id);
        });
    }

    //Updating existing data
    public void updateTask (Task updatableTask) {
        Database.databaseWriteExecutor.execute(() -> {
            mTaskDao.update(updatableTask);
        });
    }

    public void updateProject (Project updatableProject) {
        Database.databaseWriteExecutor.execute(() -> {
            mProjectDao.update(updatableProject);
        });
    }

    public void updateCategory (Category updatableCategory) {
        Database.databaseWriteExecutor.execute(() -> {
            mCategoryDao.update(updatableCategory);
        });
    }

    public void updateTheme (Theme theme){
        Database.databaseWriteExecutor.execute(() -> {
            mThemeDao.update(theme);
        });
    }

    public LiveData<List<TaskAndTheme>> getSingleTaskAndThemeByCategory(String categoryID) {
        return mTaskDao.getSingleTaskAndThemeByCategory(categoryID);
    }

    public LiveData<List<TaskAndTheme>> getSingleTaskAndThemeByCategoryByName(String categoryID, String name) {
        return mTaskDao.getSingleTaskAndThemeByCategoryByName(categoryID, "%" + name + "%");
    }

    // SYNC GET GC DATA METH
    public void synchronizeGCCalendars(Context context, LifecycleOwner lco){
        if(!App.getSettings().isAccountSigned(context)){
            return;
        }

        CalendarProvider calendarProvider = new CalendarProvider(context);
        Pair<LiveData<CalendarList>, Calendar> result = calendarProvider.getCalendars();
        result.first.observe(lco, new Observer<CalendarList>() {
            @Override
            public void onChanged(CalendarList calendarList) {
                LiveData<List<Category>> syncItemsCategoriesLD = getCategorySyncing();
                LiveData<List<Task>> syncItemsTasksLD = getTaskSyncing();

                LiveData<Tuple2<List<Category>, List<Task>>> intermediate =
                        LiveDataTransformations.ifNotNull(syncItemsCategoriesLD, syncItemsTasksLD);

                AtomicBoolean syncLoop = new AtomicBoolean(false);

                intermediate.observe(lco, new Observer<Tuple2<List<Category>, List<Task>>>() {
                    @Override
                    public void onChanged(Tuple2<List<Category>, List<Task>> data) {
                        syncThread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                if(syncLoop.get() != true) {
                                    Theme baseTheme = getThemeByName("MainTaskTheme");
                                    calendars = new ArrayList<>(calendarList.getItems().size());
                                    events = new ArrayList<>();
                                    List<Category> dbInCats = new ArrayList<>(calendarList.getItems().size());
                                    List<Task> dbInTasks = new ArrayList<>();

                                    for (int i = 0; i < calendarList.getItems().size(); i++) {
                                        CalendarListEntry le = calendarList.getItems().get(i);
                                        AtomicBoolean ft = new AtomicBoolean(false);
                                        final Events[] eventsWB = {null};
                                        data.first.forEach(category -> {
                                            if (le.getId().equals(category.getOuterID())) {
                                                calendars.add(new OuterCalendar(le, category));
                                                ft.set(true);
                                            }
                                        });
                                        if (!ft.get()) {
                                            calendars.add(new OuterCalendar((le)));
                                        }
                                        final int c = i;
                                        Thread thrd = new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                try {
                                                    eventsWB[0] = result.second.events().list(le.getId()).execute();
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                }
                                                List<Event> eventsList = eventsWB[0].getItems();
                                                for(int j = 0; j < eventsList.size(); j++){
                                                    AtomicBoolean fte = new AtomicBoolean(false);
                                                    Event ev = eventsList.get(j);
                                                    data.second.forEach(task -> {
                                                        if(ev.getId().equals(task.getTaskId())){
                                                            events.add(new OuterEvent(ev, task));
                                                            calendars.get(c).addEvent(ev);
                                                            fte.set(true);
                                                        }
                                                    });
                                                    if(!fte.get()){
                                                        events.add(new OuterEvent(ev, calendars.get(c).ct.getCategoryId(), baseTheme.getID()));
                                                    }
                                                    dbInTasks.add(events.get(events.size() - 1).ts);
                                                }
                                            }
                                        });
                                        thrd.start();
                                        dbInCats.add(calendars.get(calendars.size() - 1).ct);
                                        try {
                                            thrd.join();
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    insertAndUpdateGCData(dbInCats, dbInTasks);
                                    syncLoop.set(true);
                                }
                            }
                        });
                        syncThread.start();
                    }
                });
            }
        });
    }

    public List<Task> getAllTasks(){
        return mSilentTaskDao.getAllTasks();
    }

    public LiveData<List<Category>> getCategorySyncing(){
        return mCategoryDao.getAllSyncing();
    }

    public LiveData<List<Task>> getTaskSyncing(){
        return mTaskDao.getAllSyncing();
    }

    public void insertCategories(List<Category> list){
        Database.databaseWriteExecutor.execute(() -> {
            mCategoryDao.insertList(list);
        });
    }

    public void insertAndUpdate(List<Category> list){
        Database.databaseWriteExecutor.execute(() -> {
            mCategoryDao.insertListWithReplace(list);
        });
    }

    public void insertAndUpdateGCData(List<Category> categories, List<Task> tasks){
        Database.databaseWriteExecutor.execute(() -> {
            mCategoryDao.setGCData(categories, tasks);
        });
    }

    public Theme getThemeByName(String name){
        return mSilentDatabase.themeDao().getByName(name);
    }

    class OuterCalendar{
        public CalendarListEntry le;
        public Category ct;
        public boolean checked;
        public List<Event> events;
        public OuterCalendar(CalendarListEntry le, Category ct){
            this.le = le;
            this.ct = ct;
            events = new ArrayList<>();
            checked = false;
            processMerge();
        }
        public OuterCalendar(CalendarListEntry le){
            this.le = le;
            this.ct = new Category();
            events = new ArrayList<>();
            checked = false;
            processMerge();
        }

        public void processMerge(){
            ct.setName(le.getSummary());
            ct.setDescription(le.getDescription());
            ct.setOuterID(le.getId());
        }
        public void addEvent(Event ev){
            events.add(ev);
        }
    }
    class OuterEvent{
        public Event ev;
        public Task ts;
        public boolean checked;
        public OuterEvent(Event ev, Task ts){
            this.ev = ev;
            this.ts = ts;
            checked = false;
            processMerge();
        }
        public OuterEvent(Event ev, String categoryID, String themeID){
            this.ev = ev;
            this.ts = new Task();
            checked = false;
            processMerge(categoryID, themeID);
        }

        public void processMerge(String categoryID, String themeID){
            if(ev.getSummary() != null) {
                ts.setName(ev.getSummary());
            }
            if(ev.getDescription() != null) {
                ts.setDescription(ev.getDescription());
            }
            ts.setTaskId(ev.getId());
            ts.setCategoryId(categoryID);
            ts.setThemeID(themeID);
        }

        public void processMerge(){
            if(ev.getSummary() != null) {
                ts.setName(ev.getSummary());
            }
            if(ev.getDescription() != null) {
                ts.setDescription(ev.getDescription());
            }
            ts.setTaskId(ev.getId());
        }
    }
}
