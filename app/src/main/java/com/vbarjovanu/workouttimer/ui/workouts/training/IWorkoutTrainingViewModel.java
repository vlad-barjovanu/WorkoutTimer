package com.vbarjovanu.workouttimer.ui.workouts.training;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.vbarjovanu.workouttimer.ui.generic.viewmodels.ISynchronizable;
import com.vbarjovanu.workouttimer.ui.workouts.training.models.WorkoutTrainingModel;

public abstract class IWorkoutTrainingViewModel extends ViewModel implements ISynchronizable {
    abstract void close();

    abstract LiveData<WorkoutTrainingModel> getWorkoutTrainingModel();

    abstract void loadWorkout(String workoutId);

    abstract void startWorkoutTraining();

    abstract void pauseWorkoutTraining();

    abstract void stopWorkoutTraining();

    abstract void nextWorkoutTrainingItem();

    abstract void previousWorkoutTrainingItem();
}
