package com.example.clock.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "category_table")
public class Category implements Serializable {

    @PrimaryKey(autoGenerate = true)
    @NonNull
    @ColumnInfo(name = "categoryId")
    private long mCategoryId;

    private String mName;

    private String mDescription;

    private String mType;
    private int mFirstColor;
    private int mSecondColor;

    private String mThemeID;


    public Category(){
        mCategoryId = 0;
        mName = "New list";
        //mDescription = "";
        mType = "FIELD";
        mFirstColor = Integer.parseInt("FFFFFF", 16);
        mDescription = "";
        mThemeID = "";
    }

    public Category(@NonNull String name, @NonNull String type){
        mCategoryId = 0;
        mName = name;
        mType = type;
        mFirstColor = Integer.parseInt("FFFFFF", 16);
        mDescription = "";
        mThemeID = "";
    }

    public Category(@NonNull String name,
                    @NonNull String description,
                    @NonNull String type){
        mCategoryId = 0;
        mName = name;
        mType = type;
        mDescription = description;

        mFirstColor = 0;
        mSecondColor = 0;
        mThemeID = "";
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

    public int getFirstColor() {
        return mFirstColor;
    }

    public void setFirstColor(int mColor) {
        this.mFirstColor = mColor;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String mDescription) {
        this.mDescription = mDescription;
    }

    public int getSecondColor() {
        return this.mSecondColor;
    }

    public void setSecondColor(int color){
        this.mSecondColor = color;
    }

    public String getThemeID() {
        return mThemeID;
    }

    public void setThemeID(String mThemeID) {
        this.mThemeID = mThemeID;
    }

    public void installTheme(Theme theme){
        setFirstColor(theme.getFirstColor());
        setSecondColor(theme.getSecondColor());
        setThemeID(theme.getID());
    }
}
