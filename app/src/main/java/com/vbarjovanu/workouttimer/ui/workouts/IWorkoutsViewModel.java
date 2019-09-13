package com.vbarjovanu.workouttimer.ui.workouts;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.vbarjovanu.workouttimer.business.models.workouts.WorkoutsList;

public abstract class IWorkoutsViewModel extends ViewModel {
    /**
     * Loads the workouts for the specified user profile
     * @param profileId ID of the user's profile
     */
    public abstract void loadWorkouts(String profileId);

    /**
     * Rerturns the available workouts
     * @return workouts live data
     */
    public abstract WorkoutsLiveData getWorkouts();

    /**
     * Selects a specific workout by ID
     * @param id workout's ID that was selected by user
     * @return true if selection is successful
     */
    public abstract boolean setSelectedWorkoutId(String id);

    /**
     * Returns the selected workout ID
     * @return selected workout's ID
     */
    public abstract String getSelectedWorkoutId();
}
