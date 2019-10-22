package com.vbarjovanu.workouttimer.ui.workouts.list;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.vbarjovanu.workouttimer.business.models.workouts.WorkoutsList;
import com.vbarjovanu.workouttimer.ui.generic.events.SingleLiveEvent;
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
     *
     * @return data of the workouts action
     */
    abstract SingleLiveEvent<WorkoutsFragmentActionData> getActionData();

    /**
     * Starts the workflow of creating a new workout
     * @param profileId ID of the user profile
     */
    public abstract void newWorkout(String profileId);

    /**
     * edits a workout
     * @param profileId ID of the user profile
     * @param workoutId ID of the workout to be edited
     */
    public abstract void editWorkout(String profileId, String workoutId);

    /**
     * deletes a workout
     * @param profileId ID of the user profile
     * @param workoutId ID of the workout to be deleted
     */
    public abstract void deleteWorkout(String profileId, String workoutId);
}
