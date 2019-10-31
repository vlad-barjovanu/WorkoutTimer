package com.vbarjovanu.workouttimer.ui.workouts.training;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.vbarjovanu.workouttimer.ui.generic.events.SingleLiveEvent;
import com.vbarjovanu.workouttimer.ui.generic.viewmodels.ISynchronizable;
import com.vbarjovanu.workouttimer.ui.workouts.training.actions.WorkoutTrainingActionData;
import com.vbarjovanu.workouttimer.ui.workouts.training.models.IWorkoutTrainingItemColorProvider;
import com.vbarjovanu.workouttimer.ui.workouts.training.models.WorkoutTrainingModel;

public abstract class IWorkoutTrainingViewModel extends ViewModel implements ISynchronizable {
    abstract void close();

    abstract LiveData<WorkoutTrainingModel> getWorkoutTrainingModel();

    abstract SingleLiveEvent<WorkoutTrainingActionData> getAction();

    abstract IWorkoutTrainingItemColorProvider getWorkoutTrainingItemColorProvider();

    abstract void loadWorkout(String workoutId, WorkoutTrainingModel savedWorkoutTrainingModel);

    abstract boolean isInitialised();

    abstract void startWorkoutTraining();

    abstract void pauseWorkoutTraining();

    abstract void stopWorkoutTraining();

    abstract void nextWorkoutTrainingItem();

    abstract void previousWorkoutTrainingItem();

    abstract void toggleLock();

    public abstract void toggleSound();

    public abstract void toggleVibrate();
}
