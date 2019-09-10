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
     * @return workouts list
     */
    public abstract LiveData<WorkoutsList> getWorkouts();
}
