package com.example.clock.model;


import android.graphics.Color;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "theme_table")
public class Theme {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "theme_ID")
    private long mID;
    //@ColumnInfo(name = "theme_name")
    @ColumnInfo(name = "theme_name")
    private String mName;
    private int mFirstColor;
    private int mSecondColor;

    private int mColorThird;

    private int mIconColor;

    private int mTextColor;

    private int mImage;

    public Theme(long mID, String name, int mFirstColor, int mSecondColor, int mImage) {
        this.mID = mID;
        this.mName = name;
        this.mFirstColor = mFirstColor;
        this.mSecondColor = mSecondColor;
        this.mImage = mImage;
        this.mColorThird = 0;
    }

    public Theme(long mID, String name, String mFirstColor, String mSecondColor, String mThirdColor, int mImage) {
        this.mID = mID;
        this.mName = name;
        this.mFirstColor = Color.parseColor(mFirstColor);
        this.mSecondColor = Color.parseColor(mSecondColor);
        this.mColorThird = Color.parseColor(mThirdColor);
        this.mImage = mImage;
    }

    public Theme(long mID, String name, String mFirstColor, String mSecondColor, int mImage) {
        this.mID = mID;
        this.mName = name;
        this.mFirstColor = Color.parseColor(mFirstColor);
        this.mSecondColor = Color.parseColor(mSecondColor);
        this.mColorThird = 0;
        this.mImage = mImage;
    }

    public long getID() {
        return mID;
    }

    public void setID(long mId) {
        this.mID = mId;
    }

    public String getName() {
        return mName;
    }

    public void setName(String mName) {
        this.mName = mName;
    }

    public int getFirstColor() {
        return mFirstColor;
    }

    public void setFirstColor(int mFirstColor) {
        this.mFirstColor = mFirstColor;
    }

    public int getSecondColor() {
        return mSecondColor;
    }

    public void setSecondColor(int mSecondColor) {
        this.mSecondColor = mSecondColor;
    }

    public int getColorThird() {
        return mColorThird;
    }

    public void setColorThird(int mColorThird) {
        this.mColorThird = mColorThird;
    }

    public int getImage() {
        return mImage;
    }

    public void setImage(int mImage) {
        this.mImage = mImage;
    }

    public int getTextColor() {
        return mTextColor;
    }

    public void setTextColor(int mTextColor) {
        this.mTextColor = mTextColor;
    }

    public int getIconColor() {
        return mIconColor;
    }

    public void setIconColor(int mIconColor) {
        this.mIconColor = mIconColor;
    }

    public void setIconColor(String mIconColor) {
        this.mIconColor = Color.parseColor(mIconColor);
    }
}
