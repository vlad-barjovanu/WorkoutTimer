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
    public void newWorkout(String profileId) {
        this.actionData.setValue(new WorkoutsFragmentActionData(WorkoutsFragmentAction.GOTO_WORKOUT_NEW, profileId, null));
    }

    @Override
    public void editWorkout(String profileId, String workoutId) {
        Workout workout = null;
        WorkoutsList workoutsList = this.getWorkouts().getValue();
        if (workoutsList != null) {
            workout = workoutsList.find(workoutId);
        }
        if (workout != null) {
            this.actionData.setValue(new WorkoutsFragmentActionData(WorkoutsFragmentAction.GOTO_WORKOUT_EDIT, profileId, workoutId));
        }
    }

    @Override
    public void trainWorkout(String profileId, String workoutId) {
        Workout workout = null;
        WorkoutsList workoutsList = this.getWorkouts().getValue();
        if (workoutsList != null) {
            workout = workoutsList.find(workoutId);
        }
        if (workout != null) {
            this.actionData.setValue(new WorkoutsFragmentActionData(WorkoutsFragmentAction.GOTO_WORKOUT_TRAINING, profileId, workoutId));
        }
    }

    @Override
    public void deleteWorkout(String profileId, String workoutId) {
        new DeleteAsyncTask(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, profileId, workoutId);
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
            if (data != null || this.workoutsViewModel.workoutsLiveData.getValue() != null) {
                this.workoutsViewModel.workoutsLiveData.setValue(data);
            }
            this.workoutsViewModel.decreaseCountDownLatch();
        }
    }

    static class DeleteAsyncTask extends AsyncTask<String, Void, Object[]> {
        WorkoutsViewModel workoutsViewModel;
        private String profileId;
        private String workoutId;

        DeleteAsyncTask(WorkoutsViewModel workoutsViewModel) {
            this.workoutsViewModel = workoutsViewModel;
        }

        @Override
        protected Object[] doInBackground(String... strings) {
            boolean success;
            WorkoutsList workoutsList;
            Workout workout = null;

            Log.v("deletedata", "doInBackground");
            this.profileId = strings[0];
            this.workoutId = strings[1];
            workoutsList = this.workoutsViewModel.getWorkouts().getValue();
            if (workoutsList != null) {
                workout = workoutsList.find(workoutId);
            }
            workoutsList = null;
            success = this.workoutsViewModel.workoutsService.deleteModel(this.profileId, workout);
            if (success) {
                workoutsList = this.workoutsViewModel.workoutsService.loadModels(profileId);
            }
            return new Object[]{success, workoutsList};
        }

        @Override
        protected void onPostExecute(Object[] data) {
            Boolean success;
            WorkoutsList workoutsList;
            Log.v("deletedata", "onPostExecute");
            success = (Boolean)data[0];
            workoutsList = (WorkoutsList) data[1];
            if (success) {
                if (workoutsList != null || this.workoutsViewModel.workoutsLiveData.getValue() != null) {
                    this.workoutsViewModel.workoutsLiveData.setValue(workoutsList);
                }
            } else {
                this.workoutsViewModel.actionData.setValue(new WorkoutsFragmentActionData(WorkoutsFragmentAction.DISPLAY_WORKOUT_DELETE_FAILED, this.profileId, this.workoutId));
            }
            this.workoutsViewModel.decreaseCountDownLatch();
        }
    }
}