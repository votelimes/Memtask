package com.votelimes.memtask.model;

public class TaskProjectMerge {
    public final Task task;
    public final Project project;
    private final int type;

    public TaskProjectMerge(Task mergableTask, Project mergableProject){
        task = mergableTask;
        project = mergableProject;

        if(task == null){
            type = 1;
        }
        else{
            type = 0;
        }

    }

    public int getType(){
        return type;
    }


}
