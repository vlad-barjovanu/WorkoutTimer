package com.vbarjovanu.workouttimer.ui.workouts.list;

import android.os.AsyncTask;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.vbarjovanu.workouttimer.business.models.workouts.Workout;
import com.vbarjovanu.workouttimer.business.models.workouts.WorkoutsList;
import com.vbarjovanu.workouttimer.business.services.generic.IFileRepositorySettings;
import com.vbarjovanu.workouttimer.business.services.workouts.IWorkoutsService;
import com.vbarjovanu.workouttimer.business.services.workouts.WorkoutsFactory;

import java.util.concurrent.CountDownLatch;

public class WorkoutsViewModel extends IWorkoutsViewModel {

    private MutableLiveData<WorkoutsList> workoutsLiveData;

    private String selectedWorkoutId;

    private CountDownLatch countDownLatch;

    private final IFileRepositorySettings fileRepositorySettings;

    public WorkoutsViewModel(IFileRepositorySettings fileRepositorySettings) {
        this.fileRepositorySettings = fileRepositorySettings;
        this.workoutsLiveData = new MutableLiveData<>();
    }

    @Override
    public void loadWorkouts(String profileId) {
        new LoadAsyncTask(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, profileId);
    }

    @Override
    public LiveData<WorkoutsList> getWorkouts() {
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

    @Override
    public void setCountDownLatch(CountDownLatch countDownLatch) {
        this.countDownLatch = countDownLatch;
    }

    private void decreaseCountDownLatch() {
        if(this.countDownLatch!=null) {
            this.countDownLatch.countDown();
        }
    }

    static class LoadAsyncTask extends AsyncTask<String, Void, WorkoutsList> {
        WorkoutsViewModel workoutsViewModel;

        LoadAsyncTask(WorkoutsViewModel workoutsViewModel) {
            this.workoutsViewModel = workoutsViewModel;
        }

        @Override
        protected void onPreExecute() {
            Log.v("loaddata", "preexecute");
            super.onPreExecute();
        }

        @Override
        protected WorkoutsList doInBackground(String... strings) {
            WorkoutsList data;
            String profileId;

            Log.v("loaddata", "doInBackground");
            profileId = strings[0];
            IWorkoutsService workoutsService = WorkoutsFactory.getWorkoutsService(this.workoutsViewModel.fileRepositorySettings);
            data = workoutsService.loadModels(profileId);
            return data;
        }

        @Override
        protected void onPostExecute(WorkoutsList data) {
            Log.v("loaddata", "onPostExecute");
            this.workoutsViewModel.workoutsLiveData.setValue(data);
            this.workoutsViewModel.decreaseCountDownLatch();
        }
    }
}