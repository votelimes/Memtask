package com.example.clock.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "user_list_table")
public class UserList {

    @PrimaryKey(autoGenerate = true)
    @NonNull
    @ColumnInfo(name = "taskId")
    private long mUserListId;

    private String mName;
    private String mDescription;

    private String mType;
    private int mColor;

    public UserList(){
        mUserListId = 0;
        mName = "New list";
        mDescription = "";
        mType = "FIELD";
        mColor = Integer.parseInt("FFFFFF", 16);
    }

    public UserList(@NonNull String name, @NonNull String type, String description){
        mUserListId = 0;
        mName = name;
        mType = type;
        mColor = Integer.parseInt("FFFFFF", 16);

        if(description == null){
            mDescription = "";
        }
        else{
            mDescription = description;
        }
    }

    public UserList(@NonNull String name, @NonNull String type, String description, int color){
        mUserListId = 0;
        mName = name;
        mType = type;
        mColor = color;

        if(description == null){
            mDescription = "";
        }
        else{
            mDescription = description;
        }
    }


}
