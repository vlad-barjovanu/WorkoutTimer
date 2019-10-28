package com.vbarjovanu.workouttimer.business.services.workouts;

import android.graphics.Color;

import com.vbarjovanu.workouttimer.business.models.workouts.Workout;
import com.vbarjovanu.workouttimer.business.models.workouts.WorkoutsList;
import com.vbarjovanu.workouttimer.business.services.generic.IFileRepositorySettings;
import com.vbarjovanu.workouttimer.business.services.generic.ModelsService;

import java.lang.reflect.InvocationTargetException;
import java.util.Random;

public class WorkoutsService extends ModelsService<Workout, WorkoutsList> implements IWorkoutsService {
    WorkoutsService(IWorkoutsFileRepository workoutsFileRepository, IFileRepositorySettings modelsFileRepositorySettings, Class<Workout> classT, Class<WorkoutsList> classZ) {
        super(workoutsFileRepository, modelsFileRepositorySettings, classT, classZ);
    }

    @Override
    public Workout createModel() throws InstantiationException, IllegalAccessException, InvocationTargetException {
        int[] colors = this.getPossibleColors();
        int color = colors[new Random().nextInt(colors.length)];
        Workout workout = super.createModel();
        workout.setColor(color);
        workout.setIncreaseDuration(false);
        return workout;
    }

    public int getWorkoutsCount(String profileId) {
        WorkoutsList list = this.loadModels(profileId);
        return (list != null) ? list.size() : 0;
    }

    @Override
    public int[] getPossibleColors() {
        return new int[]{
                Color.parseColor("#ff33b5e5"),
                Color.parseColor("#ff99cc00"),
                Color.parseColor("#ffffbb33"),
                Color.parseColor("#ffff4444"),
                Color.parseColor("#ffaa66cc"),
                Color.parseColor("#ff0099cc"),
                Color.parseColor("#ff669900"),
                Color.parseColor("#ffff8800"),
                Color.parseColor("#ffcc0000")
        };
    }
}
