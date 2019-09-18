package com.vbarjovanu.workouttimer.ui.workouts.list;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.vbarjovanu.workouttimer.business.models.workouts.WorkoutsList;
import com.vbarjovanu.workouttimer.ui.generic.viewmodels.ISynchronizable;

public abstract class IWorkoutsViewModel extends ViewModel implements ISynchronizable {
    /**
     * Loads the workouts for the specified user profile
     * @param profileId ID of the user's profile
     */
    public abstract void loadWorkouts(String profileId);

    /**
     * Rerturns the available workouts
     * @return workouts live data
     */
    public abstract LiveData<WorkoutsList> getWorkouts();

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
