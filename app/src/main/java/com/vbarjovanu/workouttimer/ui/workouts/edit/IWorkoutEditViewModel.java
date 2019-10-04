package com.vbarjovanu.workouttimer.ui.workouts.edit;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.vbarjovanu.workouttimer.business.models.workouts.Workout;
import com.vbarjovanu.workouttimer.ui.generic.events.SingleLiveEvent;
import com.vbarjovanu.workouttimer.ui.generic.viewmodels.ISynchronizable;

public abstract class IWorkoutEditViewModel extends ViewModel implements ISynchronizable {
    IWorkoutEditViewModel() {
        super();
    }

    abstract void newWorkout();

    abstract void loadWorkout(String workoutId);

    abstract LiveData<Workout> getWorkout();

    abstract void saveWorkout(Workout workout);

    abstract void cancelWorkoutEdit();

    abstract SingleLiveEvent<WorkoutEditFragmentAction> getAction();
}
