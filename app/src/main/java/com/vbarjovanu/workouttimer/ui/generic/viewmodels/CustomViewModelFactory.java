package com.vbarjovanu.workouttimer.ui.generic.viewmodels;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.vbarjovanu.workouttimer.ui.workouts.IWorkoutsViewModel;
import com.vbarjovanu.workouttimer.ui.workouts.WorkoutsViewModel;

public class CustomViewModelFactory implements ViewModelProvider.Factory {
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if(modelClass.isAssignableFrom(IWorkoutsViewModel.class)){
            //noinspection unchecked
            return (T) new WorkoutsViewModel();
        }
        try {
            return modelClass.newInstance();
        } catch (Exception e) {
            return null;
        }
    }
}
