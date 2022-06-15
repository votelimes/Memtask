package com.example.clock.model;


import android.graphics.Color;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.UUID;

@Entity(tableName = "theme_table")
public class Theme {
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "theme_ID", index = true)
    private String mID;
    @ColumnInfo(name = "theme_name")
    private String mName;
    private int mFirstColor;
    private int mSecondColor;

    private int mColorThird;

    private int mIconColor;

    private int mMainTextColor;

    private int mAdditionalTextColor;

    private int mImage;

    private boolean baseTheme;

    public Theme(String name, int mFirstColor, int mSecondColor, int mImage) {
        this.mID = generateUUID();
        this.mName = name;
        this.mFirstColor = mFirstColor;
        this.mSecondColor = mSecondColor;
        this.mImage = mImage;
        this.mColorThird = 0;
        this.baseTheme = true;
        this.mAdditionalTextColor = -1;
    }
    public Theme(){

    }

    public Theme(String name, String mFirstColor, String mSecondColor, String mThirdColor, int mImage) {
        this.mID = generateUUID();
        this.mName = name;
        this.mFirstColor = Color.parseColor(mFirstColor);
        this.mSecondColor = Color.parseColor(mSecondColor);
        this.mColorThird = Color.parseColor(mThirdColor);
        this.mImage = mImage;
        this.baseTheme = true;
        this.mAdditionalTextColor = -1;
    }

    public Theme(String name, String mFirstColor, String mSecondColor, int mImage) {
        this.mID = generateUUID();
        this.mName = name;
        this.mFirstColor = Color.parseColor(mFirstColor);
        this.mSecondColor = Color.parseColor(mSecondColor);
        this.mColorThird = 0;
        this.mImage = mImage;
        this.baseTheme = true;
        this.mAdditionalTextColor = -1;
    }

    protected String generateUUID(){
        return UUID.randomUUID().toString();
    }

    public void reGenerateUUID(){
        this.mID = generateUUID();
    }

    public String getID() {
        return mID;
    }

    public void setID(String mId) {
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

    public int getMainTextColor() {
        return mMainTextColor;
    }

    public void setMainTextColor(int mTextColor) {
        this.mMainTextColor = mTextColor;
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

    public int getAdditionalTextColor() {
        return mAdditionalTextColor;
    }

    public void setAdditionalTextColor(int mAdditionalTextColor) {
        this.mAdditionalTextColor = mAdditionalTextColor;
    }

    public void setAdditionalTextColor(String mAdditionalTextColor) {
        this.mAdditionalTextColor = Color.parseColor(mAdditionalTextColor);
    }

    public boolean isBaseTheme() {
        return baseTheme;
    }

    public void setBaseTheme(boolean baseTheme) {
        this.baseTheme = baseTheme;
    }
}
