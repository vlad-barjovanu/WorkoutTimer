package com.vbarjovanu.workouttimer.ui.workouts.list;

import android.os.AsyncTask;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.vbarjovanu.workouttimer.business.models.workouts.Workout;
import com.vbarjovanu.workouttimer.business.models.workouts.WorkoutsList;
import com.vbarjovanu.workouttimer.business.services.workouts.IWorkoutsService;
import com.vbarjovanu.workouttimer.ui.generic.events.SingleLiveEvent;

import java.util.concurrent.CountDownLatch;

public class WorkoutsViewModel extends IWorkoutsViewModel {

    private IWorkoutsService workoutsService;

    private MutableLiveData<WorkoutsList> workoutsLiveData;

    private SingleLiveEvent<WorkoutsFragmentActionData> actionData;

    private String selectedWorkoutId;

    private CountDownLatch countDownLatch;

    public WorkoutsViewModel(IWorkoutsService workoutsService) {
        this.workoutsService = workoutsService;
        this.workoutsLiveData = new MutableLiveData<>();
        this.actionData = new SingleLiveEvent<>();
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
    SingleLiveEvent<WorkoutsFragmentActionData> getActionData() {
        return this.actionData;
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
    public void newWorkout(String profileId) {
        this.actionData.setValue(new WorkoutsFragmentActionData(WorkoutsFragmentAction.GOTO_WORKOUT_NEW, profileId, null));
    }

    @Override
    public void editWorkout(String profileId, String workoutId) {
        this.actionData.setValue(new WorkoutsFragmentActionData(WorkoutsFragmentAction.GOTO_WORKOUT_EDIT, profileId, workoutId));
    }

    @Override
    public void deleteWorkout(String profileId, String workoutId) {
        if (this.workoutsService.deleteModel(profileId, workoutId)) {
            this.loadWorkouts(profileId);
        }
    }

    @Override
    public void setCountDownLatch(CountDownLatch countDownLatch) {
        this.countDownLatch = countDownLatch;
    }

    private void decreaseCountDownLatch() {
        if (this.countDownLatch != null) {
            this.countDownLatch.countDown();
        }
    }

    static class LoadAsyncTask extends AsyncTask<String, Void, WorkoutsList> {
        WorkoutsViewModel workoutsViewModel;

        LoadAsyncTask(WorkoutsViewModel workoutsViewModel) {
            this.workoutsViewModel = workoutsViewModel;
        }

        @Override
        protected WorkoutsList doInBackground(String... strings) {
            WorkoutsList data;
            String profileId;

            Log.v("loaddata", "doInBackground");
            profileId = strings[0];
            data = this.workoutsViewModel.workoutsService.loadModels(profileId);
            return data;
        }

        @Override
        protected void onPostExecute(WorkoutsList data) {
            Log.v("loaddata", "onPostExecute");
            this.workoutsViewModel.workoutsLiveData.setValue(data);
            this.workoutsViewModel.decreaseCountDownLatch();
        }
    }

    static class DeleteAsyncTask extends AsyncTask<String, Void, Boolean> {
        WorkoutsViewModel workoutsViewModel;
        private String profileId;
        private String workoutId;

        DeleteAsyncTask(WorkoutsViewModel workoutsViewModel) {
            this.workoutsViewModel = workoutsViewModel;
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            Boolean data;

            Log.v("deletedata", "doInBackground");
            this.profileId = strings[0];
            this.workoutId = strings[1];
            data = this.workoutsViewModel.workoutsService.deleteModel(this.profileId, this.workoutId);
            return data;
        }

        @Override
        protected void onPostExecute(Boolean data) {
            Log.v("deletedata", "onPostExecute");
            if (data) {
                this.workoutsViewModel.loadWorkouts(this.profileId);
            } else {
                this.workoutsViewModel.actionData.setValue(new WorkoutsFragmentActionData(WorkoutsFragmentAction.DISPLAY_WORKOUT_DELETE_FAILED, this.profileId, this.workoutId));
            }
        }
    }
}