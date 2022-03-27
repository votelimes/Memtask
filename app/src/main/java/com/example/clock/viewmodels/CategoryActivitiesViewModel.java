package com.example.clock.viewmodels;

import android.app.Application;
import android.content.Context;

import androidx.core.util.Pair;
import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.lifecycle.LiveData;

import com.example.clock.BR;
import com.example.clock.app.App;
import com.example.clock.model.Project;
import com.example.clock.model.ProjectData;
import com.example.clock.model.Task;
import com.example.clock.model.TaskAndTheme;
import com.example.clock.model.Theme;
import com.example.clock.repositories.MemtaskRepositoryBase;
import com.example.clock.storageutils.Database;
import com.example.clock.storageutils.LiveDataTransformations;
import com.example.clock.storageutils.SilentDatabase;
import com.example.clock.storageutils.Tuple3;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class CategoryActivitiesViewModel extends MemtaskViewModelBase{
    public final int RESTORE_ITEM_SNACKBAR_TIME = 5750;
    public long currentCategoryID;

    public LiveData<Tuple3<List<TaskAndTheme>, List<ProjectData>, List<Theme>>> intermediate;

    public LiveData<List<TaskAndTheme>> singleTaskThemeLiveData;
    public LiveData<List<ProjectData>> projectLiveData;

    // remove back mechanism
    private List<ParentObserver> itemObservers;
    private ParentObserver removableObs;
    private int removableItemObserversIntermediatePos;
    private int removableItemObserversListPos;
    private int removableItemObserverChildPos;

    private int sortType;
    private boolean shouldUpdate;

    CategoryActivitiesViewModel(Application application, Database database, SilentDatabase silentDatabase){
        mRepository = new MemtaskRepositoryBase(application, database, silentDatabase);
    }

    // Util methods

    public void loadData(){
        currentCategoryID = App.getSettings().getLastCategory().first;
        categoriesLiveData = mRepository.getAllCategoriesLive();

        singleTaskThemeLiveData = mRepository.getSingleTaskAndThemeByCategory(currentCategoryID);
        projectLiveData = mRepository.getProjectDataByCategory(currentCategoryID);
        themesLiveData = mRepository.getAllThemesLive();

        intermediate =  LiveDataTransformations.ifNotNull(
                singleTaskThemeLiveData, projectLiveData, themesLiveData);

        tasksLiveData = null;
        projectsLiveData = null;
    }

    public LiveData<Tuple3<List<TaskAndTheme>, List<ProjectData>, List<Theme>>> updateData(String filterName){
        if(filterName != null && filterName.length() > 1){
            singleTaskThemeLiveData = mRepository.getSingleTaskAndThemeByCategoryByName(currentCategoryID, filterName);
            projectLiveData = mRepository.getProjectDataByCategoryByName(currentCategoryID, filterName);
        }
        else{
            singleTaskThemeLiveData = mRepository.getSingleTaskAndThemeByCategory(currentCategoryID);
            projectLiveData = mRepository.getProjectDataByCategory(currentCategoryID);
        }

        intermediate =  LiveDataTransformations.ifNotNull(
                singleTaskThemeLiveData, projectLiveData, themesLiveData);

        return intermediate;
    }

    public void init(){
        mergeTasksAndProjects();
    }



    private void mergeTasksAndProjects(){
        int totalSize = singleTaskThemeLiveData.getValue().size() + projectLiveData.getValue().size();

        itemObservers = new ArrayList<>(totalSize);

        projectLiveData.getValue().forEach(item -> {
            itemObservers.add((ParentObserver) new ProjectObserver(item));
        });

        singleTaskThemeLiveData.getValue().forEach(item -> {
            itemObservers.add((ParentObserver) new TaskObserver(item, false));
        });
    }


    public void update(){
        long currentCategoryID = App.getSettings().getLastCategory().first;
        singleTaskThemeLiveData = mRepository.getSingleTaskAndThemeByCategory(currentCategoryID);
        projectLiveData = mRepository.getProjectDataByCategory(currentCategoryID);
    }

    public void removeSilently(int pos){
        if(itemObservers.get(pos) instanceof TaskObserver && !(((TaskObserver) itemObservers.get(pos)).projectItem)){
            removableObs = itemObservers.get(pos);
            Thread thread = new Thread() {
                public void run() {
                    try {
                        removableItemObserversIntermediatePos = intermediate.getValue().first.indexOf(((TaskObserver) removableObs).getData());
                    } catch(Exception e) {
                        System.out.println(e);
                    }
                }
            };
            thread.run();
            removableItemObserversListPos = pos;

            mRepository.removeTaskByIDSilently((((TaskObserver) itemObservers.get(pos)).getTask().getTaskId()));
            intermediate.getValue().first.remove(((TaskObserver) itemObservers.get(pos)).getData());
            itemObservers.remove(pos);
        }
        else if(itemObservers.get(pos) instanceof ProjectObserver){
            removableObs = itemObservers.get(pos);
            mRepository.removeProjectByIDSilently((((ProjectObserver) itemObservers.get(pos)).getProject().getProjectId()));
            intermediate.getValue().second.remove(((ProjectObserver) itemObservers.get(pos)).getData());
            itemObservers.remove(pos);
        }
    }

    public void removeSilentlyProjItem(int projectPos, int itemPos){
        ProjectObserver beforeChangesProjectObs = (ProjectObserver) new ProjectObserver(getProjectObs(projectPos));
        ProjectObserver afterChangesProjectObs = getProjectObs(projectPos);

        removableObs = (ParentObserver) beforeChangesProjectObs.getChild(itemPos);
        removableItemObserversListPos = projectPos;
        removableItemObserverChildPos = itemPos;

        afterChangesProjectObs.removeChild(itemPos);
    }

    public int returnItemBack(){
        if(removableObs != null && removableObs instanceof TaskObserver && !((TaskObserver) removableObs).projectItem){
            intermediate.getValue().first.add(removableItemObserversIntermediatePos, ((TaskObserver) removableObs).getData());
            itemObservers.add(removableItemObserversListPos, removableObs);
            addTaskSilently(((TaskObserver) removableObs).getTask());
            // TODO: add task theme back to DB, do theme ID to be a String
            removableObs = null;
        }
        else if(removableObs != null && removableObs instanceof ProjectObserver){
            intermediate.getValue().second.add(removableItemObserversIntermediatePos, ((ProjectObserver) removableObs).getData());
            itemObservers.add(removableItemObserversListPos, removableObs);
            addProjectSilently(((ProjectObserver) removableObs).getData().project);
            // TODO: add project theme back to DB, do theme ID to be a String
            int childCount = ((ProjectObserver) removableObs).getChildsCount();
            for(int i = 0; i < childCount; i++){
                Task task = ((ProjectObserver) removableObs).getChild(i).getTask();
                addTaskSilently(task);
                // TODO: add child tasks theme back to DB, do theme ID to be a String
            }
        }
        else if(removableObs != null && removableObs instanceof TaskObserver && ((TaskObserver) removableObs).projectItem){
            ProjectObserver projObs = getProjectObs(removableItemObserversListPos);
            TaskObserver taskObs = (TaskObserver) removableObs;

            mRepository.addTaskSilently(taskObs.getTask());
            projObs.addChild(removableItemObserverChildPos, taskObs);
            // TODO: provide child task return back mechanism
            // TODO: add single child task theme back to DB, do theme ID to be a String

            return removableItemObserverChildPos;
        }
        return removableItemObserversListPos;
    }

    public TaskObserver addTaskChild(){
        Task task = new Task();
        task.setCategoryId(App.getSettings().getLastCategory().first);

        Theme currentTheme = intermediate
                .getValue()
                .third
                .stream()
                .filter(theme -> theme.getName().equals("MainTaskTheme")).findFirst().get();

        task.setThemeID(currentTheme.getID());

        TaskAndTheme taskAndTheme = new TaskAndTheme(task, currentTheme);

        singleTaskThemeLiveData.getValue().add(0, taskAndTheme);

        TaskObserver taskObs = new TaskObserver(taskAndTheme, false);
        itemObservers.add(0, (ParentObserver) taskObs);

        mRepository.addTaskSilently(task);

        return taskObs;
    }

    public TaskObserver addProjectChild(int projPos){

        ProjectObserver projObs = getProjectObs(projPos);
        Task task = new Task();

        Theme currentTheme = intermediate
                .getValue()
                .third
                .stream()
                .filter(theme -> theme.getName().equals("MainTaskTheme")).findFirst().get();

        task.setThemeID(currentTheme.getID());
        task.setParentID(projObs.getData().project.getProjectId());
        TaskAndTheme taskAndTheme = new TaskAndTheme(task, currentTheme);

        TaskObserver taskObs = new TaskObserver(taskAndTheme, true);

        projObs.addChild(taskObs);

        mRepository.addTaskSilently(task);

        return taskObs;
    }

    public void filter(String filterNameField){
        if(filterNameField.length() > 0) {
            itemObservers = itemObservers
                    .parallelStream()
                    .filter(item -> item.getName().toLowerCase(Locale.ROOT).contains(filterNameField))
                    .collect(Collectors.toList());
        }
        else{
            mergeTasksAndProjects();
        }
    }

    // Getters

    public Theme getItemTheme(int pos){
        if(itemObservers.get(pos) instanceof TaskObserver){
            return ((((TaskObserver) itemObservers.get(pos)).getTheme()));
        }
        else if(itemObservers.get(pos) instanceof ProjectObserver){
            return ((((ProjectObserver) itemObservers.get(pos)).getTheme()));
        }
        return null;
    }

    public TaskObserver getProjItemObs(int projPos, int itemPos){
        return ((ProjectObserver) itemObservers.get(projPos)).getChild(itemPos);
    }

    public int getPoolSize(){
        return itemObservers.size();
    }

    public List<Theme> getThemes(){
        return themesLiveData.getValue();
    }

    public ProjectData getAllProjectTasks(){
        return projectLiveData.getValue().get(0);
    }

    public TaskObserver getSingleTaskObs(int pos){
        return (TaskObserver) itemObservers.get(pos);
    }

    public ProjectObserver getProjectObs(int pos){
        return (ProjectObserver) itemObservers.get(pos);
    }

    public ParentObserver getObs(int pos){
        return itemObservers.get(pos);
    }


    //Sub classes

    public class ParentObserver extends BaseObservable{

        public ParentObserver() {
            super();
        }

        public ParentObserver(ParentObserver other) {
            super();
            this.closeEndDate = other.closeEndDate;
        }

        protected String closeEndDate;

        public String getCloseEndDate() {
            return closeEndDate;
        }

        public void setCloseEndDate(String closeEndDate) {
            this.closeEndDate = closeEndDate;
        }

        public String getName(){
            return "";
        }
    }

    public class TaskObserver extends ParentObserver {
        private TaskAndTheme data;
        private boolean projectItem;

        TaskObserver(TaskAndTheme data, boolean projectItem){
            this.data = data;
            this.projectItem = projectItem;
        }

        public TaskAndTheme getData(){
            return data;
        }

        public Task getTask(){
            return this.data.task;
        }

        public Theme getTheme(){
            return this.data.theme;
        }

        public String getRange(){
            LocalDateTime startTime;
            LocalDateTime endTime;
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy");


            startTime = LocalDateTime.ofEpochSecond(
                    data
                            .task
                            .getStartTime() / 1000,
                    0, ZoneOffset.UTC
            );
            endTime = LocalDateTime.ofEpochSecond(
                    data
                            .task
                            .getEndTime() / 1000,
                    0, ZoneOffset.UTC
            );

            return startTime.format(dtf) + " — " + endTime.format(dtf);
        }

        public LocalDateTime getEndRange(){
            return LocalDateTime.ofEpochSecond(data.task.getEndTime() * 1000, 0, ZoneOffset.UTC);
        }

        @Bindable
        public boolean getCompletedOrExpired(){
            return data.task.isCompleted() || data.task.isExpired();
        }

        @Bindable
        public boolean getDescriptionState(){
            return data.task.getDescription().length() != 0;
        }

        public String getNotify(){
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yy HH:mm");

            return LocalDateTime.ofEpochSecond(
                    (long) data.task.getNotificationStartMillis() / 1000,
                    0, ZoneOffset.UTC).format(dtf);
        }

        @Override
        @Bindable
        public String getName(){
            return data.task.getName();
        }

        @Bindable
        public String getDescription(){
            return data.task.getDescription();
        }

        public boolean isImportant(){
            return data.task.getImportance() == 0;
        }

        public boolean isProjectItem(){
            return projectItem;
        }

        public void setName(String name){
            data.task.setName(name);
            CategoryActivitiesViewModel.this.addTaskSilently(data.task);
            notifyPropertyChanged(BR.name);
            notifyPropertyChanged(BR.image);
        }

        public void setDescription(String description){
            data.task.setDescription(description);
            CategoryActivitiesViewModel.this.addTaskSilently(data.task);
            notifyPropertyChanged(BR.description);
            notifyPropertyChanged(BR.image);
        }

        public void setCompletedOrExpired(boolean state){
            if(state){
                data.task.setExpired(false);
                data.task.setCompleted(true);
            }
            else{
                data.task.setExpired(false);
                data.task.setCompleted(false);
            }
            addTaskSilently(data.task);
            notifyPropertyChanged(BR.completedOrExpired);
            notifyPropertyChanged(BR.completedExpired);
        }

        @Bindable
        public boolean getNotificationEnabled(){
            return data.task.isNotificationEnabled();
        }

        public int setNotificationEnabled(Context context, boolean state){
            if(data.task.getNotificationStartMillis() == 0){
                return 1;
            }

            if((data.task.getNotificationStartMillis() / 1000) < LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)){
                return 2;
            }

            data.task.setNotificationEnabled(state);

            int returnCode;

            if(state){
                data.task.schedule(context);
                returnCode = 0;
            }
            else{
                data.task.cancelAlarm(context);
                returnCode = -1;
            }

            addTaskSilently(data.task);
            notifyPropertyChanged(BR.notificationEnabled);

            return returnCode;
        }

        @Bindable
        public Pair<Boolean, Boolean> getCompletedExpired(){
            return new Pair<Boolean, Boolean>(data.task.isCompleted(), data.task.isExpired());
        }

        public void setCompletedExpired(Pair<Boolean, Boolean> data){
            // void
        }

        @Bindable
        public String getImage(){
            return data.task.getImageResource();
        }
    }

    public class ProjectObserver extends ParentObserver {
        private ProjectData data;
        private List<TaskObserver> childObservers;
        private float progress;

        ProjectObserver(ProjectData data){
            this.data = data;
            childObservers = new ArrayList<>(data.tasksData.size());
            data.tasksData.forEach(item -> {
                childObservers.add(new TaskObserver(item, true));
            });
        }

        public ProjectObserver(ProjectObserver other) {
            super(other);
            this.data = other.data;
            this.childObservers = new ArrayList<>(other.childObservers);
            this.progress = other.progress;
        }

        // Getters
        @Bindable
        public boolean getCompletedOrExpired(){
            boolean flag = data.project.isCompleted() || data.project.isExpired();
            return data.project.isCompleted() || data.project.isExpired();
        }

        public ProjectData getData(){
            return data;
        }

        public Project getProject(){
            return this.data.project;
        }

        public Theme getTheme(){
            return this.data.theme;
        }

        public TaskObserver getChild(int pos){
            return childObservers.get(pos);
        }

        public int getChildsCount(){
            return childObservers.size();
        }

        public String getRange(){
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy");

            final LocalDateTime startTime = LocalDateTime.ofEpochSecond(
                    data
                            .project
                            .getStartTime() / 1000,
                    0, ZoneOffset.UTC
            );
            AtomicReference<LocalDateTime> endTime = new AtomicReference<>(LocalDateTime.ofEpochSecond(
                    data
                            .project
                            .getEndTime() / 1000,
                    0, ZoneOffset.UTC
            ));

            AtomicBoolean shouldUpdateFlag = new AtomicBoolean(false);

            childObservers.forEach(item -> {
                if(item.getEndRange().isAfter(endTime.get())){
                    shouldUpdateFlag.set(true);
                    endTime.set(item.getEndRange());
                }
            });

            if(shouldUpdateFlag.get()){
                this
                        .data
                        .project
                        .setEndTime(endTime
                                .get()
                                .toEpochSecond(ZoneOffset.UTC));
                CategoryActivitiesViewModel
                        .this
                        .mRepository
                        .addProjectSilently(this.data.project);
            }

            return startTime.format(dtf) + " — " + endTime.get().format(dtf);
        }

        @Bindable
        public float getProgress(){
            AtomicReference<Float> progress = new AtomicReference<>((float) 0);
            float childPrice = (float) (100.0 / (float) childObservers.size());

            childObservers.forEach(item ->{
                if(item.getTask().isCompleted() || item.getTask().isExpired()){
                    progress.updateAndGet(v -> Float.valueOf((float) (v + childPrice)));
                }
            });
            progress.set((float) Math.round(progress.get()));
            this.progress = progress.get();
            return progress.get();
        }

        @Bindable
        public String getProgressText(){
            getProgress();
            String value = String.valueOf((int) progress + "%");
            return value;
        }

        @Bindable
        public boolean getDescriptionState(){
            return data.project.getDescription().length() != 0;
        }

        @Bindable
        public String getName(){
            return data.project.getName();
        }

        @Bindable
        public String getDescription(){
            return data.project.getDescription();
        }

        public boolean isImportant(){
            return data.project.getImportance() == 0;
        }

        // Setters
        public void setName(String name){
            data.project.setName(name);
            CategoryActivitiesViewModel.this.addProjectSilently(data.project);
            notifyPropertyChanged(BR.name);
        }

        public void setDescription(String description){
            data.project.setDescription(description);
            CategoryActivitiesViewModel.this.addProjectSilently(data.project);
            notifyPropertyChanged(BR.description);
        }

        public void setCompleted(boolean state){
            if(state){
                data.project.setExpired(false);
                data.project.setCompleted(true);
            }
            else{
                data.project.setExpired(false);
                data.project.setCompleted(false);
            }
            addProjectSilently(data.project);
            notifyPropertyChanged(BR.completedOrExpired);
            notifyPropertyChanged(BR.progressText);
        }

        // Utils
        public void addChild(TaskObserver child){
            childObservers.add(child);
            recalcProgress();
        }
        public void addChild(int pos, TaskObserver child){
            childObservers.add(pos, child);
            recalcProgress();
        }

        public void removeChild(int pos){
            CategoryActivitiesViewModel
                    .this
                    .mRepository
                    .removeTaskByIDSilently(childObservers.get(pos).getTask().getTaskId());
            childObservers.remove(childObservers.get(pos));
            recalcProgress();
        }

        public void recalcProgress(){
            notifyPropertyChanged(BR.progress);
            notifyPropertyChanged(BR.progressText);
            int c = (int) childObservers.stream().filter(item -> (item.getTask().isCompleted() || item.getTask().isExpired())).count();

            setCompleted(c == childObservers.size());
        }

        @Bindable
        public Pair<Boolean, Boolean> getCompletedExpired(){
            return new Pair<Boolean, Boolean>(data.project.isCompleted(), data.project.isExpired());
        }

        public void setCompletedExpired(Pair<Boolean, Boolean> data){
            // void
        }

        public String getImage(){
            return data.project.getImageResource();
        }
    }
}
