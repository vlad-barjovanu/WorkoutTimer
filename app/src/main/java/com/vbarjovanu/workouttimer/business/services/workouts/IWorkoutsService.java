package com.vbarjovanu.workouttimer.business.services.workouts;

import com.vbarjovanu.workouttimer.business.models.workouts.Workout;
import com.vbarjovanu.workouttimer.business.models.workouts.WorkoutsList;
import com.vbarjovanu.workouttimer.business.services.generic.IModelsService;

public interface IWorkoutsService extends IModelsService<Workout, WorkoutsList> {
    /**
     * Returns an array with the available colors for workouts
     *
     * @return array of colors (RGB int value)
     */
    int[] getPossibleColors();

    /**
     * Returns the total workouts count for a certain user profile
     *
     * @param profileId user profile ID
     * @return total workouts count for the profile
     */
    int getWorkoutsCount(String profileId);
}