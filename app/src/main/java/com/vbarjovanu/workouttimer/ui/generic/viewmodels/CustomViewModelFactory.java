package com.vbarjovanu.workouttimer.ui.generic.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.vbarjovanu.workouttimer.IMainActivityViewModel;
import com.vbarjovanu.workouttimer.MainActivityViewModel;
import com.vbarjovanu.workouttimer.business.services.generic.FileRepositorySettings;
import com.vbarjovanu.workouttimer.business.services.generic.IFileRepositorySettings;
import com.vbarjovanu.workouttimer.business.services.userprofiles.IUserProfilesService;
import com.vbarjovanu.workouttimer.business.services.userprofiles.UserProfilesFactory;
import com.vbarjovanu.workouttimer.business.services.workouts.IWorkoutsService;
import com.vbarjovanu.workouttimer.business.services.workouts.WorkoutsFactory;
import com.vbarjovanu.workouttimer.session.ApplicationSessionFactory;
import com.vbarjovanu.workouttimer.session.IApplicationSession;
import com.vbarjovanu.workouttimer.ui.colors.ColorsPickerViewModel;
import com.vbarjovanu.workouttimer.ui.colors.IColorsPickerViewModel;
import com.vbarjovanu.workouttimer.ui.userprofiles.edit.IUserProfileEditViewModel;
import com.vbarjovanu.workouttimer.ui.userprofiles.list.IUserProfilesViewModel;
import com.vbarjovanu.workouttimer.ui.userprofiles.edit.UserProfileEditViewModel;
import com.vbarjovanu.workouttimer.ui.userprofiles.list.UserProfilesViewModel;
import com.vbarjovanu.workouttimer.ui.workouts.edit.IWorkoutEditViewModel;
import com.vbarjovanu.workouttimer.ui.workouts.edit.WorkoutEditViewModel;
import com.vbarjovanu.workouttimer.ui.workouts.list.IWorkoutsViewModel;
import com.vbarjovanu.workouttimer.ui.workouts.list.WorkoutsViewModel;

public class CustomViewModelFactory implements ViewModelProvider.Factory {
    private IFileRepositorySettings fileRepositorySettings;
    private Application application;

    public static CustomViewModelFactory getInstance(@NonNull Application application){
        String folderPath = ApplicationSessionFactory.getApplicationSession(application.getApplicationContext()).getFileRepositoriesFolderPath();
        return new CustomViewModelFactory(application, new FileRepositorySettings(folderPath));
    }

    private CustomViewModelFactory(@NonNull Application application, IFileRepositorySettings fileRepositorySettings) {
        this.fileRepositorySettings = fileRepositorySettings;
        this.application = application;
    }

    private IApplicationSession getApplicationSession(){
        return ApplicationSessionFactory.getApplicationSession(this.application.getApplicationContext());
    }

    private IWorkoutsService getWorkoutsService(){
        return WorkoutsFactory.getWorkoutsService(this.fileRepositorySettings);
    }

    private IUserProfilesService getUserProfilesService(){
        return UserProfilesFactory.getUserProfilesService(this.fileRepositorySettings);
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {

        if (modelClass.isAssignableFrom(IMainActivityViewModel.class)) {
            //noinspection unchecked
            return (T) new MainActivityViewModel(this.getApplicationSession(), this.getUserProfilesService());
        }
        if (modelClass.isAssignableFrom(IWorkoutsViewModel.class)) {
            //noinspection unchecked
            return (T) new WorkoutsViewModel(this.getWorkoutsService());
        }
        if (modelClass.isAssignableFrom(IWorkoutEditViewModel.class)) {
            //noinspection unchecked
            return (T) new WorkoutEditViewModel(this.getApplicationSession(), this.getWorkoutsService());
        }
        if (modelClass.isAssignableFrom(IUserProfilesViewModel.class)) {
            //noinspection unchecked
            return (T) new UserProfilesViewModel(this.getApplicationSession(), this.getUserProfilesService());
        }
        if (modelClass.isAssignableFrom(IUserProfileEditViewModel.class)) {
            //noinspection unchecked
            return (T) new UserProfileEditViewModel(this.getApplicationSession(), this.getUserProfilesService());
        }
        if(modelClass.isAssignableFrom(IColorsPickerViewModel.class)){
            //noinspection unchecked
            return (T) new ColorsPickerViewModel();
        }
        try {
            return modelClass.newInstance();
        } catch (Exception e) {
            //noinspection ConstantConditions
            return null;
        }
    }
}
