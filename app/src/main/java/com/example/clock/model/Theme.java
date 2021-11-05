package com.example.clock.model;


import android.graphics.Color;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "theme_table")
public class Theme {

    @PrimaryKey(autoGenerate = true)
    private long mId;
    private String mName;
    private int mFirstColor;
    private int mSecondColor;

    private int mImage;

    public Theme(long mId, String name, int mFirstColor, int mSecondColor, int mImage) {
        this.mId = mId;
        this.mName = name;
        this.mFirstColor = mFirstColor;
        this.mSecondColor = mSecondColor;
        this.mImage = mImage;
    }

    public Theme(long mId, String name, String mFirstColor, String mSecondColor, int mImage) {
        this.mId = mId;
        this.mName = name;
        this.mFirstColor = Color.parseColor(mFirstColor);
        this.mSecondColor = Color.parseColor(mSecondColor);
        this.mImage = mImage;
    }

    public long getId() {
        return mId;
    }

    public void setId(long mId) {
        this.mId = mId;
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

    public int getImage() {
        return mImage;
    }

    public void setImage(int mImage) {
        this.mImage = mImage;
    }
}
