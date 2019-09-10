package com.vbarjovanu.workouttimer.business.services.workouts;

import com.vbarjovanu.workouttimer.business.models.workouts.Workout;
import com.vbarjovanu.workouttimer.business.services.generic.ModelsFileRepository;

public class WorkoutsFileRepository extends ModelsFileRepository<Workout> implements IWorkoutsFileRepository  {
    /**
     * @param classT
     * @param classArrayOfT
     */
    public WorkoutsFileRepository(Class<Workout> classT, Class<Workout[]> classArrayOfT) {
        super(classT, classArrayOfT);
    }
}
