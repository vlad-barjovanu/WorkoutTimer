package com.vbarjovanu.workouttimer.business.services.workouts;

import com.vbarjovanu.workouttimer.business.models.workouts.Workout;
import com.vbarjovanu.workouttimer.business.models.workouts.WorkoutsList;
import com.vbarjovanu.workouttimer.business.services.generic.IFileRepositorySettings;
import com.vbarjovanu.workouttimer.business.services.generic.ModelsService;

public class WorkoutsService extends ModelsService<Workout, WorkoutsList> implements IWorkoutsService{
    public WorkoutsService(IWorkoutsFileRepository workoutsFileRepository, IFileRepositorySettings modelsFileRepositorySettings, Class<Workout> classT, Class<WorkoutsList> classZ) {
        super(workoutsFileRepository, modelsFileRepositorySettings, classT, classZ);
    }
}
