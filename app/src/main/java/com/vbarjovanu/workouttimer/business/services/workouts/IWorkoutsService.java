package com.vbarjovanu.workouttimer.business.services.workouts;

import com.vbarjovanu.workouttimer.business.models.workouts.Workout;
import com.vbarjovanu.workouttimer.business.models.workouts.WorkoutsList;
import com.vbarjovanu.workouttimer.business.services.generic.IModelsService;

public interface IWorkoutsService extends IModelsService<Workout, WorkoutsList> {
    int[] getPossibleColors();
}
