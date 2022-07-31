package com.votelimes.memtask.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.UUID;

@Entity(tableName = "category_table")
public class Category implements Serializable {

    @PrimaryKey()
    @NonNull
    @ColumnInfo(name = "categoryId")
    private String mCategoryId;

    private String mOuterID;

    @ColumnInfo(name = "categoryName")
    private String mName;

    private String mDescription;

    private String mType;
    private int mFirstColor;
    private int mSecondColor;

    private String mThemeID;

    private boolean outer;


    public Category(){
        mCategoryId = UUID.randomUUID().toString();
        mName = "New list";
        //mDescription = "";
        mType = "FIELD";
        mFirstColor = Integer.parseInt("FFFFFF", 16);
        mDescription = "";
        mThemeID = "";
        mOuterID = "";
    }

    public Category(@NonNull String name, @NonNull String type){
        mCategoryId = UUID.randomUUID().toString();
        mName = name;
        mType = type;
        mFirstColor = Integer.parseInt("FFFFFF", 16);
        mDescription = "";
        mThemeID = "";
        mOuterID = "";
    }

    public Category(@NonNull String name,
                    @NonNull String description,
                    @NonNull String type){
        mCategoryId = UUID.randomUUID().toString();
        mName = name;
        mType = type;
        mDescription = description;

        mFirstColor = 0;
        mSecondColor = 0;
        mThemeID = "";
        mOuterID = "";
    }

    public String getCategoryId() {
        return mCategoryId;
    }

    public void setCategoryId(String mCategoryId) {
        this.mCategoryId = mCategoryId;
    }

    public void regenerateID(){
        this.mCategoryId = UUID.randomUUID().toString();
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

    public String getOuterID() {
        return mOuterID;
    }

    public void setOuterID(String mOuterID) {
        this.mOuterID = mOuterID;
    }

    public boolean isOuter() {
        return outer;
    }

    public void setOuter(boolean outer) {
        this.outer = outer;
    }
}
