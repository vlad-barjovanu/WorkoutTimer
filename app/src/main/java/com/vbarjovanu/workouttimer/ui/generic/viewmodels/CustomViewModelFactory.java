package com.vbarjovanu.workouttimer.ui.generic.viewmodels;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.vbarjovanu.workouttimer.business.services.generic.IFileRepositorySettings;
import com.vbarjovanu.workouttimer.ui.workouts.IWorkoutsViewModel;
import com.vbarjovanu.workouttimer.ui.workouts.WorkoutsViewModel;

public class CustomViewModelFactory implements ViewModelProvider.Factory {
    private IFileRepositorySettings fileRepositorySettings;

    public CustomViewModelFactory(IFileRepositorySettings fileRepositorySettings) {
        this.fileRepositorySettings = fileRepositorySettings;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if(modelClass.isAssignableFrom(IWorkoutsViewModel.class)){
            //noinspection unchecked
            return (T) new WorkoutsViewModel(this.fileRepositorySettings);
        }
        try {
            return modelClass.newInstance();
        } catch (Exception e) {
            return null;
        }
    }
}
