package com.vbarjovanu.workouttimer.ui.workouts;

import com.vbarjovanu.workouttimer.business.models.workouts.Workout;
import com.vbarjovanu.workouttimer.business.services.generic.IFileRepositorySettings;
import com.vbarjovanu.workouttimer.ui.generic.viewmodels.ISynchronizable;

import java.util.concurrent.CountDownLatch;

public class WorkoutsViewModel extends IWorkoutsViewModel {

    private WorkoutsLiveData workoutsLiveData;

    private String selectedWorkoutId;

    public WorkoutsViewModel(IFileRepositorySettings fileRepositorySettings) {
        this.workoutsLiveData = new WorkoutsLiveData(fileRepositorySettings);
    }

    @Override
    public void loadWorkouts(String profileId) {
        this.workoutsLiveData.loadWorkouts(profileId);
    }

    @Override
    public WorkoutsLiveData getWorkouts() {
        return this.workoutsLiveData;
    }

    @Override
    public boolean setSelectedWorkoutId(String id) {
        Workout workout = null;
        if (this.workoutsLiveData.getValue() != null) {
            workout = this.workoutsLiveData.getValue().find(id);
        }
        if (workout != null) {
            this.selectedWorkoutId = id;
            return true;
        }
        return false;
    }

    @Override
    public String getSelectedWorkoutId() {
        return this.selectedWorkoutId;
    }
}