package com.example.clock.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.lifecycle.LiveData;

import com.example.clock.BR;
import com.example.clock.model.Category;
import com.example.clock.model.Theme;
import com.example.clock.storageutils.Database;
import com.example.clock.storageutils.SilentDatabase;

import java.util.List;

public class ManageCategoryViewModel extends MemtaskViewModelBase {

    public Observer mManagingCategoryRepository;

    public ManageCategoryViewModel(Application application, Database database, SilentDatabase silentDatabase, Category managingCategory){
        mManagingCategoryRepository = new Observer(managingCategory);
        loadData(application, database, silentDatabase);
    }

    public LiveData<List<Category>> getCategoriesLiveData(Application application, Database database, SilentDatabase silentDatabase){
        if(mRepository == null){
            loadData(application, database, silentDatabase);
        }
        return this.categoriesLiveData;
    }

    public LiveData<List<Theme>> getThemesLiveData(Application application, Database database, SilentDatabase silentDatabase){
        if(mRepository == null){
            loadData(application, database, silentDatabase);
        }
        return this.themesLiveData;
    }

    public void saveChanges(){
        this.mRepository.addCategory(this.mManagingCategoryRepository.mManagingCategory);
    }

    public static class Observer extends BaseObservable {

        private Category mManagingCategory;
        private Theme mTheme;


        Observer(@NonNull Category managingCategory){
            this.mManagingCategory = managingCategory;
        }

        @Bindable
        public String getCategoryName() {
            return this.mManagingCategory.getName();
        }

        @Bindable
        public String getCategoryDescription(){
            return this.mManagingCategory.getDescription();
        }

        @Bindable
        public int getFirstColor(){
            return this.mManagingCategory.getFirstColor();
        }

        @Bindable
        public int getSecondColor(){
            return this.mManagingCategory.getSecondColor();
        }

        public void setCategoryName(String name) {
            this.mManagingCategory.setName(name);
            notifyPropertyChanged(BR.categoryName);
        }

        public void setCategoryDescription(String description){
            this.mManagingCategory.setDescription(description);
            notifyPropertyChanged(BR.categoryDescription);
        }

        public void setFirstColor(int color){
            this.mManagingCategory.setFirstColor(color);
            this.mManagingCategory.setThemeID("");
            notifyPropertyChanged(BR.firstColor);
        }

        public void setSecondColor(int color){
            this.mManagingCategory.setSecondColor(color);
            this.mManagingCategory.setThemeID("");
            notifyPropertyChanged(BR.secondColor);
        }

        public void installTheme(Theme theme){
            mManagingCategory.installTheme(theme);
            notifyPropertyChanged(BR.firstColor);
            notifyPropertyChanged(BR.secondColor);
        }

        public String getThemeID(){
            return this.mManagingCategory.getThemeID();
        }

        public void setThemeID(String id){
            this.mManagingCategory.setThemeID(id);
        }
    }
}