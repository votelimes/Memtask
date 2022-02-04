package com.example.clock.viewmodels;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.clock.app.App;
import com.example.clock.model.Project;
import com.example.clock.model.ProjectAndTheme;
import com.example.clock.model.Task;
import com.example.clock.model.TaskAndTheme;
import com.example.clock.model.Theme;
import com.example.clock.model.UserCaseBase;
import com.example.clock.repositories.MemtaskRepositoryBase;
import com.example.clock.storageutils.Database;
import com.example.clock.storageutils.LiveDataTransformations;
import com.example.clock.storageutils.SilentDatabase;
import com.example.clock.storageutils.Tuple2;
import com.example.clock.storageutils.Tuple3;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.stream.Collectors;

public class CategoryActivitiesViewModel extends MemtaskViewModelBase{
    public static final int MODE_INDEPENDENTLY = 0;
    public static final int MODE_PROJECT_ITEM = 1;
    public long currentCategoryID;

    public LiveData<Tuple3<List<TaskAndTheme>, List<TaskAndTheme>, List<ProjectAndTheme>>> intermediate;

    public List<Tuple2<UserCaseBase, Theme>> singleActivitiesPool;
    public LiveData<List<TaskAndTheme>> singleTaskThemeLiveData;
    public LiveData<List<TaskAndTheme>> projectTaskThemeLiveData;
    public LiveData<List<ProjectAndTheme>> projectThemeLiveData;

    private int sortType;
    private boolean shouldUpdate;

    CategoryActivitiesViewModel(Application application, Database database, SilentDatabase silentDatabase){
        mRepository = new MemtaskRepositoryBase(application, database, silentDatabase);
    }

    public void loadData(){
        currentCategoryID = App.getSettings().getLastCategory().first;
        categoriesLiveData = mRepository.getAllCategoriesLive();

        singleTaskThemeLiveData = mRepository.getSingleTasksByCategoryLiveData(currentCategoryID);
        projectTaskThemeLiveData = mRepository.getProjectTasksByCategoryLiveData(currentCategoryID);
        projectThemeLiveData = mRepository.getProjectsByCategoryLiveData(currentCategoryID);

        intermediate =  LiveDataTransformations.ifNotNull(
                singleTaskThemeLiveData, projectTaskThemeLiveData, projectThemeLiveData);

        tasksLiveData = null;
        projectsLiveData = null;
        themesLiveData = null;
    }

    public List<Task> getTasksByCategory(long categoryID){

        List<Task> tasksList = tasksLiveData.getValue();
        if(tasksList == null)
            return null;

        List<Task> filteredByCategoryTasks = tasksList
                .stream()
                .filter(c -> c.getCategoryId() == categoryID)
                .collect(Collectors.toList());

        return filteredByCategoryTasks;
    }

    public List<Project> getProjectsByCategory(long categoryID){

        List<Project> projectsList = projectsLiveData.getValue();
        if(projectsList == null)
            return null;

        return projectsList
                .stream()
                .filter(c -> c.getCategoryId() == categoryID)
                .collect(Collectors.toList());
    }

    public List<Task> getTasksByParent(String parentID){
        List<Task> tasksList = tasksLiveData.getValue();
        if(tasksList == null)
            return null;

        return tasksList
                .stream()
                .filter(c -> c.getParentID().equals(parentID))
                .collect(Collectors.toList());
    }

    public List<Theme> getThemes(){
        return themesLiveData.getValue();
    }

    public boolean isShouldUpdate() {
        return shouldUpdate;
    }

    public void setShouldUpdate(boolean shouldUpdate) {
        this.shouldUpdate = shouldUpdate;
    }

    public void update(){
        long currentCategoryID = App.getSettings().getLastCategory().first;
        singleTaskThemeLiveData = mRepository.getSingleTasksByCategoryLiveData(currentCategoryID);
        projectTaskThemeLiveData = mRepository.getProjectTasksByCategoryLiveData(currentCategoryID);
        projectThemeLiveData = mRepository.getProjectsByCategoryLiveData(currentCategoryID);
    }

    public void clean(){

    }

    public void init(){
        mergeTasksAndProjects();
    }

    public void mergeTasksAndProjects(){
        int totalSize = singleTaskThemeLiveData.getValue().size() + projectThemeLiveData.getValue().size();
        singleActivitiesPool = new ArrayList<>(totalSize);

        singleTaskThemeLiveData.getValue().forEach(item -> {
            singleActivitiesPool.add(new Tuple2((UserCaseBase) item.task, item.theme));
        });

        projectThemeLiveData.getValue().forEach(item -> {
            singleActivitiesPool.add(new Tuple2((UserCaseBase) item.project, item.theme));
        });
    }

    public List<TaskAndTheme> getProjectPool(){
        return projectTaskThemeLiveData.getValue();
    }

    public UserCaseBase getItem(int mode, int position){
        if(mode == MODE_INDEPENDENTLY){
            return singleActivitiesPool.get(position).first;
        }
        else if (mode == MODE_PROJECT_ITEM){
            return projectTaskThemeLiveData.getValue().get(position).task;
        }
        else {
            return null;
        }
    }

    public Theme getSingleItemTheme(int position){
        return singleActivitiesPool.get(position).second;
    }

    public Task getProjItem(int position){
        return projectTaskThemeLiveData.getValue().get(position).task;
    }

    public Theme getProjItemTheme(int position){
        return projectTaskThemeLiveData.getValue().get(position).theme;
    }

    public List<TaskAndTheme> getTasksByProject(String projectID){
        List<TaskAndTheme> taskList = new ArrayList<>();

        for(int i = 0; i < projectTaskThemeLiveData.getValue().size(); i++){
            if(projectID.equals(projectTaskThemeLiveData.getValue().get(i).task.getParentID())) {
                taskList.add(projectTaskThemeLiveData.getValue().get(i));
            }
        }
        return taskList;
    }

    public int getPoolSize(){
        return singleActivitiesPool.size();
    }

    public void removeSilently(int mode, int pos){
        if(mode == MainViewModel.MODE_INDEPENDENTLY){
            if (singleActivitiesPool.get(pos).first.getClass() == Task.class) {
                Task task = (Task) singleActivitiesPool.get(pos).first;
                removeTaskByIDSilently(task.getTaskId());
            }
            else if (singleActivitiesPool.get(pos).first.getClass() == Project.class) {
                Project project = (Project) singleActivitiesPool.get(pos).first;
                removeProjectByIDSilently(project.getProjectId());
            }
            singleActivitiesPool.remove(pos);
        }
        else if(mode == MainViewModel.MODE_PROJECT_ITEM){
            Task task = (Task) projectTaskThemeLiveData.getValue().get(pos).task;
            removeTaskByIDSilently(task.getTaskId());
            projectTaskThemeLiveData.getValue().remove(pos);
        }
    }

    public String getPoolItemName(int mode, int position){
        if(mode == MODE_INDEPENDENTLY) {
            return singleActivitiesPool.get(position).first.getName();
        }
        else if(mode == MODE_PROJECT_ITEM){
            return projectTaskThemeLiveData.getValue().get(position).task.getName();
        }
        return "NULL";
    }

    /*public void setPoolItemName(int mode, int position, String name){
        if(mode == MainViewModel.MODE_INDEPENDENTLY){
            categoryActivities.get(position).setName(name);
            if(categoryActivities.get(position).getClass() == Task.class){
                addTaskSilently((Task) categoryActivities.get(position));
            }
            else if(categoryActivities.get(position).getClass() == Project.class){
                addProjectSilently((Project) categoryActivities.get(position));
            }
        }
        else if(mode == MainViewModel.MODE_PROJECT_ITEM){
            projectItemsActivities.get(position).setName(name);
            addTaskSilently(projectItemsActivities.get(position));
        }
    }*/

    public String getPoolItemDescrCT(int mode, int position){
        if(mode == MainViewModel.MODE_INDEPENDENTLY){
            return singleActivitiesPool.get(position).first.getDescription();
        }
        else if(mode == MainViewModel.MODE_PROJECT_ITEM){
            return projectTaskThemeLiveData.getValue().get(position).task.getDescription();
        }
        return "null";
    }

    public String getTaskNotifyTime(int mode, int position){
        if(mode == MainViewModel.MODE_INDEPENDENTLY){
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yy HH:mm");
            long millis = ((Task) singleActivitiesPool.get(position).first).getNotificationStartMillis();
            return LocalDateTime.ofEpochSecond((int) millis / 1000, 0, ZoneOffset.UTC).format(dtf);
        }
        else if(mode == MainViewModel.MODE_PROJECT_ITEM){
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yy HH:mm");
            long millis = projectTaskThemeLiveData.getValue().get(position).task.getNotificationStartMillis();
            return LocalDateTime.ofEpochSecond((int) millis / 1000, 0, ZoneOffset.UTC).format(dtf);
        }
        return "null";
    }

    public void setPoolItemDescrCT(int mode, int position, String descr){

        if(mode == MainViewModel.MODE_INDEPENDENTLY){
            singleActivitiesPool.get(position).first.setDescription(descr);
            if(singleActivitiesPool.get(position).first.getClass() == Task.class){
                addTaskSilently((Task) singleActivitiesPool.get(position).first);
            }
            else if(singleActivitiesPool.get(position).first.getClass() == Project.class){
                addProjectSilently((Project) singleActivitiesPool.get(position).first);
            }
        }
        else if(mode == MainViewModel.MODE_PROJECT_ITEM){
            projectTaskThemeLiveData.getValue().get(position).task.setDescription(descr);
            addTaskSilently(projectTaskThemeLiveData.getValue().get(position).task);
        }
    }

    public String getTimeRangeCT(int mode, int position){
        Calendar startTime = GregorianCalendar.getInstance();
        Calendar endTime = GregorianCalendar.getInstance();

        if(mode == MainViewModel.MODE_INDEPENDENTLY){
            startTime.setTimeInMillis(singleActivitiesPool.get(position).first.getStartTime());
            endTime.setTimeInMillis(singleActivitiesPool.get(position).first.getEndTime());
        }
        else if(mode == MainViewModel.MODE_PROJECT_ITEM){
            startTime.setTimeInMillis(projectTaskThemeLiveData.getValue().get(position).task.getNotificationStartMillis());
            endTime.setTimeInMillis(projectTaskThemeLiveData.getValue().get(position).task.getEndTime());
        }

        Date startDate = startTime.getTime();
        Date endDate = endTime.getTime();

        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");

        return sdf.format(startDate) + " — " + sdf.format(endDate);
    }

    public void removeCategoryWithItems(long id){
        mRepository.removeCategoryWithItems(id);
    }

    public Task getTaskByPos(int mode, int pos){

        if(mode == MODE_INDEPENDENTLY){
            UserCaseBase ucb = getItem(mode, pos);

            if(ucb.getClass() == Task.class){
                return (Task) ucb;
            }
        }
        else{
            return projectTaskThemeLiveData.getValue().get(pos).task;
        }

        return null;
    }

    public List<TaskAndTheme> getAllProjectTasks(){
        return projectTaskThemeLiveData.getValue();
    }
}
