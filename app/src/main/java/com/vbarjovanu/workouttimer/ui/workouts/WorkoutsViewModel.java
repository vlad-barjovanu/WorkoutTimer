package com.vbarjovanu.workouttimer.ui.workouts;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.vbarjovanu.workouttimer.business.models.workouts.WorkoutsList;

public class WorkoutsViewModel extends IWorkoutsViewModel {

    private MutableLiveData<WorkoutsList> workoutsList;

    public WorkoutsViewModel() {
        this.workoutsList = new MutableLiveData<>();
        //load workouts
//        mText.setValue("This is gallery fragment");
    }

    @Override
    public void loadWorkouts(String profileId){

    }

    @Override
    public LiveData<WorkoutsList> getWorkouts() {
        return this.workoutsList;
    }
}