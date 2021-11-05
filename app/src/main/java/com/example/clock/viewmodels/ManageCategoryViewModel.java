package com.example.clock.viewmodels;

import android.app.Application;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.lifecycle.LiveData;

import com.example.clock.BR;
import com.example.clock.model.Category;
import com.example.clock.model.Project;
import com.example.clock.model.Task;
import com.example.clock.model.Theme;
import com.example.clock.repositories.MemtaskRepositoryBase;
import com.example.clock.storageutils.Database;

import java.util.List;

public class ManageCategoryViewModel extends MemtaskViewModelBase {

    public Observer mManagingCategoryRepository;

    public ManageCategoryViewModel(Application application, Database database, Category managingCategory){
        mManagingCategoryRepository = new Observer(managingCategory);
        loadData(application, database);
    }

    public LiveData<List<Category>> getCategoriesLiveData(Application application, Database database){
        if(mRepository == null){
            loadData(application, database);
        }
        return this.categoriesLiveData;
    }

    public LiveData<List<Theme>> getThemesLiveData(Application application, Database database){
        if(mRepository == null){
            loadData(application, database);
        }
        return this.themesLiveData;
    }

    public void saveChanges(){
        this.mRepository.addCategory(this.mManagingCategoryRepository.mManagingCategory);
    }

    public static class Observer extends BaseObservable {

        private Category mManagingCategory;


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
            Log.d("MCVM: ", "GET_FIRST_COLOR_CALL");
            Log.d("VALUE: ", String.valueOf(this.mManagingCategory.getFirstColor()));
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
            this.mManagingCategory.setThemeID(-1);
            notifyPropertyChanged(BR.firstColor);
        }

        public void setSecondColor(int color){
            this.mManagingCategory.setSecondColor(color);
            this.mManagingCategory.setThemeID(-1);
            notifyPropertyChanged(BR.secondColor);
        }

        public void installTheme(Theme theme){
            mManagingCategory.installTheme(theme);
            notifyPropertyChanged(BR.firstColor);
            notifyPropertyChanged(BR.secondColor);
        }

        public long getThemeID(){
            return this.mManagingCategory.getThemeID();
        }

        public void setThemeID(long id){
            this.mManagingCategory.setThemeID(id);
        }
    }
}