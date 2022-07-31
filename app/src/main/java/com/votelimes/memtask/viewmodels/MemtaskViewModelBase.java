package com.votelimes.memtask.viewmodels;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.votelimes.memtask.R;
import com.votelimes.memtask.app.App;
import com.votelimes.memtask.model.Category;
import com.votelimes.memtask.model.Project;
import com.votelimes.memtask.model.Task;
import com.votelimes.memtask.model.Theme;
import com.votelimes.memtask.model.UserCaseStatistic;
import com.votelimes.memtask.repositories.MemtaskRepositoryBase;
import com.votelimes.memtask.storageutils.Database;
import com.votelimes.memtask.storageutils.SilentDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public abstract class MemtaskViewModelBase extends ViewModel {

    public static final int TASK_CREATING = 100001;
    public static final int TASK_EDITING = 100002;
    public static final int PROJECT_CREATING = 200001;
    public static final int PROJECT_EDITING = 200002;
    public static final String MTP_MODE = "mtp_mode";

    public static final String MTP_CATEGORY_ID = "mtp_category_ID";
    public static final String MTP_ID = "mtp_ID";
    public static final String MTP_PARENT = "mtp_parent";
    public static final String MTP_RANGE_START = "mtp_range_start";
    public static final String MTP_RANGE_END = "mtp_range_end";

    protected MemtaskRepositoryBase mRepository;
    protected LiveData<List<Task>> tasksLiveData;
    protected LiveData<List<Project>> projectsLiveData;
    protected LiveData<List<Category>> categoriesLiveData;
    protected LiveData<List<Theme>> themesLiveData;

    public MemtaskViewModelBase() {
        super();
    }

    //Load data
    protected void loadData(Application application, Database database, SilentDatabase silentDatabase){
        mRepository = new MemtaskRepositoryBase(database, silentDatabase);
        tasksLiveData = mRepository.getAllTasksLive();
        projectsLiveData = mRepository.getAllProjectsLive();
        categoriesLiveData = mRepository.getAllCategoriesLive();
        themesLiveData = mRepository.getAllThemesLive();
    }

    //Adding new data
    public void addTask (Task newTask) {
        mRepository.addTask(newTask);
    }

    public void addTaskSilently(Task task){
        mRepository.addTaskSilently(task);
    }

    public void addProject (Project newProject) {
        mRepository.addProject(newProject);
    }

    public void addProjectSilently(Project project){
        mRepository.addProjectSilently(project);
    }

    public void addCategory(Category newCategory){
        mRepository.addCategory(newCategory);
    }

    public void addTheme(Theme theme){
        mRepository.addTheme(theme);
    }

    public void addUserCaseStatisticSilently(UserCaseStatistic ucs){
        mRepository.addUserCaseStatisticSilently(ucs);
    }

    //Removing existing data
    public void removeTaskByID (String id) {
        mRepository.removeTaskByID(id);
    }

    public void removeTaskByIDSilently(String id){
        mRepository.removeTaskByIDSilently(id);
    }

    public void removeProjectByID (String id) {
        mRepository.removeProjectByID(id);
    }

    public void removeProjectByIDSilently(String id){
        mRepository.removeProjectByIDSilently(id);
    }

    //Updating existing data
    public void updateTask (Task updatableTask) {
        mRepository.updateTask(updatableTask);
    }

    public void updateProject (Project updatableProject) {
        mRepository.updateProject(updatableProject);
    }

    public void updateCategory(Category updatableCategory){
        mRepository.updateCategory(updatableCategory);
    }

    //Retrieving live data
    public LiveData<List<Task>> requestTasksData(){
        return this.tasksLiveData;
    }

    public LiveData<List<Project>> requestProjectsData(){
        return this.projectsLiveData;
    }

    public LiveData<List<Category>> requestCategoriesData(){
        return this.categoriesLiveData;
    }

    public LiveData<List<Theme>> requestThemesData() {
        return this.themesLiveData;
    }

    //Unils
    public Theme getRandomBaseTheme(){
        Theme theme;

        if(themesLiveData != null){
            List<Integer> baseThemesIndexes = new ArrayList<>(themesLiveData.getValue().size());

            for(int i = 0; i < themesLiveData.getValue().size(); i++){
                if(themesLiveData.getValue().get(i).isBaseTheme()){
                    baseThemesIndexes.add(i);
                }
            }
            Random random = new Random();
            int randomIndex = random.nextInt(baseThemesIndexes.size());

            theme = themesLiveData.getValue().get(baseThemesIndexes.get(randomIndex));
        }
        else{
            theme = new Theme("MainTaskTheme",
                    "#F7EDE2", "#F15152", 0);

            theme.setMainTextColor(App.getInstance().getColor(R.color.act_text_main));
            theme.setIconColor(App.getInstance().getColor(R.color.act_text_main));
        }
        return theme;
    }
}
