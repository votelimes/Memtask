package com.example.clock.viewmodels;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.clock.app.App;
import com.example.clock.model.Project;
import com.example.clock.model.Task;
import com.example.clock.model.Theme;
import com.example.clock.model.UserCaseBase;
import com.example.clock.storageutils.Database;
import com.example.clock.storageutils.LiveDataTransformations;
import com.example.clock.storageutils.Tuple3;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.stream.Collectors;

public class MainViewModel extends MemtaskViewModelBase {

    public static final int MODE_INDEPENDENTLY = 0;
    public static final int MODE_PROJECT_ITEM = 1;

    public LiveData<Tuple3<List<Task>, List<Project>, List<Theme>>> intermediate;
    public List<UserCaseBase> categoryActivities;
    public List<Task> projectItemsActivities;

    private int sortType;
    private boolean shouldUpdate;

    MainViewModel(Application application, Database database, Database silentDatabase){
        loadData(application, database, silentDatabase);
        intermediate =  LiveDataTransformations.ifNotNull(tasksLiveData, projectsLiveData, themesLiveData);
        shouldUpdate = true;
        projectItemsActivities = new ArrayList<>();
        categoryActivities = new ArrayList<>();
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

    public void clean(){
        if(projectItemsActivities != null) {
            projectItemsActivities.clear();
        }
        if(categoryActivities != null){
            categoryActivities.clear();
        }
    }

    public void mergeAndSortLists(){
        clean();
        List<UserCaseBase> poolActivities = new ArrayList<UserCaseBase>
                (intermediate.getValue().first.size() + intermediate.getValue().second.size());

        for(int i = 0; i < Math.max(intermediate.getValue().first.size(),
                intermediate.getValue().second.size()); i++){

            if(i < intermediate.getValue().first.size()){
                if(!intermediate.getValue().first.get(i).getParentID().equals("")){
                    projectItemsActivities.add(intermediate.getValue().first.get(i));
                }
                else {
                    poolActivities.add((UserCaseBase) intermediate.getValue().first.get(i));
                }
            }
            if(i < intermediate.getValue().second.size()){
                poolActivities.add((UserCaseBase) intermediate.getValue().second.get(i));
            }
        }

        poolActivities.forEach(item -> {
            if(item.getCategoryId() == App.getSettings().getLastCategory().first){
                categoryActivities.add(item);
            }
        });
        // sort by private sortType
        if(sortType == 0){
            Calendar now = GregorianCalendar.getInstance();
            /*Comparator<UserCaseBase> comp = new Comparator<UserCaseBase>() {
                @Override
                public int compare(UserCaseBase o, UserCaseBase t1) {

                    if(o.getStartTime()){

                    }

                    return o.getStartTime() <= now.getTimeInMillis();
                }
            };*/
            categoryActivities = categoryActivities
                    .parallelStream()
                    .sorted(Comparator.comparingLong(UserCaseBase::getStartTime))
                    .collect(Collectors.toList());
        }
    }

    public List<Task> getProjectPool(){
        return projectItemsActivities;
    }

    public UserCaseBase getByPos(int position){
        return categoryActivities.get(position);
    }

    public Task getProjItem(int position){
        return projectItemsActivities.get(position);
    }

    public List<Task> getTasksByProject(String projectID){
        List<Task> taskList = new ArrayList<>();

        for (Object obj : projectItemsActivities) {
            Task task = (Task) obj;
            if(projectID.equals(task.getParentID())) {
                taskList.add(task);
            }
        }
        return taskList;
    }

    public int getPoolSize(){
        return categoryActivities.size();
    }

    public void removeSilently(int mode, int pos){
        if(mode == MainViewModel.MODE_INDEPENDENTLY){
            if (categoryActivities.get(pos).getClass() == Task.class) {
                Task task = (Task) categoryActivities.get(pos);
                removeTaskByIDSilently(task.getTaskId());
            }
            else if (categoryActivities.get(pos).getClass() == Project.class) {
                Project project = (Project) categoryActivities.get(pos);
                removeProjectByIDSilently(project.getProjectId());
            }
            categoryActivities.remove(pos);
        }
        else if(mode == MainViewModel.MODE_PROJECT_ITEM){
            Task task = (Task) projectItemsActivities.get(pos);
            removeTaskByIDSilently(task.getTaskId());
            projectItemsActivities.remove(pos);
        }
    }

    public String getPoolItemName(int mode, int position){
        if(mode == MODE_INDEPENDENTLY) {
            return categoryActivities.get(position).getName();
        }
        else if(mode == MODE_PROJECT_ITEM){
            return projectItemsActivities.get(position).getName();
        }
        return "NULL";
    }

    public void setPoolItemName(int mode, int position, String name){
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
    }

    public String getPoolItemDescrCT(int mode, int position){
        if(mode == MainViewModel.MODE_INDEPENDENTLY){
            return categoryActivities.get(position).getDescription();
        }
        else if(mode == MainViewModel.MODE_PROJECT_ITEM){
            return projectItemsActivities.get(position).getDescription();
        }
        return "null";
    }
    public void setPoolItemDescrCT(int mode, int position, String descr){

        if(mode == MainViewModel.MODE_INDEPENDENTLY){
            categoryActivities.get(position).setDescription(descr);
            if(categoryActivities.get(position).getClass() == Task.class){
                addTaskSilently((Task) categoryActivities.get(position));
            }
            else if(categoryActivities.get(position).getClass() == Project.class){
                addProjectSilently((Project) categoryActivities.get(position));
            }
        }
        else if(mode == MainViewModel.MODE_PROJECT_ITEM){
            projectItemsActivities.get(position).setDescription(descr);
            addTaskSilently(projectItemsActivities.get(position));
        }
    }

    public String getTimeRangeCT(int mode, int position){
        Calendar startTime = GregorianCalendar.getInstance();
        Calendar endTime = GregorianCalendar.getInstance();

        if(mode == MainViewModel.MODE_INDEPENDENTLY){
            startTime.setTimeInMillis(categoryActivities.get(position).getNotificationStartMillis());
            endTime.setTimeInMillis(categoryActivities.get(position).getEndTime());
        }
        else if(mode == MainViewModel.MODE_PROJECT_ITEM){
            startTime.setTimeInMillis(projectItemsActivities.get(position).getNotificationStartMillis());
            endTime.setTimeInMillis(projectItemsActivities.get(position).getEndTime());
        }

        Date startDate = startTime.getTime();
        Date endDate = endTime.getTime();

        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");

        return sdf.format(startDate) + " — " + sdf.format(endDate);
    }

    public void removeCategoryWithItems(long id){
        mRepository.removeCategoryWithItems(id);
    }
}
