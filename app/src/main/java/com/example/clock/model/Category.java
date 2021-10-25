package com.example.clock.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "category_table")
public class Category {

    @PrimaryKey(autoGenerate = true)
    @NonNull
    @ColumnInfo(name = "categoryId")
    private long mCategoryId;

    private String mName;

    private String mDescription;

    private String mType;
    private int mColor;

    public Category(){
        mCategoryId = 0;
        mName = "New list";
        //mDescription = "";
        mType = "FIELD";
        mColor = Integer.parseInt("FFFFFF", 16);
        mDescription = "";
    }

    public Category(@NonNull String name, @NonNull String type){
        mCategoryId = 0;
        mName = name;
        mType = type;
        mColor = Integer.parseInt("FFFFFF", 16);
        mDescription = "";
    }

    public Category(@NonNull String name,
                    @NonNull String description,
                    @NonNull String type, int color){
        mCategoryId = 0;
        mName = name;
        mType = type;
        mColor = color;
        mDescription = description;
    }

    public long getCategoryId() {
        return mCategoryId;
    }

    public void setCategoryId(long mCategoryId) {
        this.mCategoryId = mCategoryId;
    }

    public String getName() {
        return mName;
    }

    public void setName(String mName) {
        this.mName = mName;
    }

    public String getType() {
        return mType;
    }

    public void setType(String mType) {
        this.mType = mType;
    }

    public int getColor() {
        return mColor;
    }

    public void setColor(int mColor) {
        this.mColor = mColor;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String mDescription) {
        this.mDescription = mDescription;
    }
}
