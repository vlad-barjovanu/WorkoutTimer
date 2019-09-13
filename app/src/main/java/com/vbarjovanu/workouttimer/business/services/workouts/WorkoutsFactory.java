package com.vbarjovanu.workouttimer.business.services.workouts;

import com.vbarjovanu.workouttimer.business.models.workouts.Workout;
import com.vbarjovanu.workouttimer.business.models.workouts.WorkoutsList;
import com.vbarjovanu.workouttimer.business.services.generic.IFileRepositorySettings;

public class WorkoutsFactory {
    public static IWorkoutsService getWorkoutsService(IFileRepositorySettings fileRepositorySettings) {
        IWorkoutsFileRepository workoutsFileRepository;
        workoutsFileRepository = WorkoutsFactory.getWorkoutsFileRepository();
        return new WorkoutsService(workoutsFileRepository, fileRepositorySettings, Workout.class, WorkoutsList.class);
    }

    public static IWorkoutsFileRepository getWorkoutsFileRepository(){
        return new WorkoutsFileRepository(Workout.class, Workout[].class);
    }
}
