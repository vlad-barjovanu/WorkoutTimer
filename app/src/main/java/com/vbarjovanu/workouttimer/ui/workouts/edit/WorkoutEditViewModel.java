package com.vbarjovanu.workouttimer.ui.workouts.edit;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.vbarjovanu.workouttimer.business.models.workouts.Workout;
import com.vbarjovanu.workouttimer.business.services.workouts.IWorkoutsService;
import com.vbarjovanu.workouttimer.session.IApplicationSession;
import com.vbarjovanu.workouttimer.ui.generic.events.SingleLiveEvent;

import java.util.concurrent.CountDownLatch;

public class WorkoutEditViewModel extends IWorkoutEditViewModel {
    private IWorkoutsService workoutsService;
    private IApplicationSession applicationSession;
    private MutableLiveData<Workout> workoutLiveData;
    private SingleLiveEvent<WorkoutEditFragmentAction> action;
    private CountDownLatch countDownLatch;

    public WorkoutEditViewModel(@NonNull IApplicationSession applicationSession, @NonNull IWorkoutsService workoutsService) {
        super();
        this.applicationSession = applicationSession;
        this.workoutsService = workoutsService;
        this.workoutLiveData = new MutableLiveData<>();
        this.action = new SingleLiveEvent<>();
    }

    @Override
    void newWorkout() {
        Workout workout;
        try {
            workout = this.workoutsService.createModel();
            this.workoutLiveData.setValue(workout);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    void loadWorkout(String workoutId) {
        LoadAsyncTask asyncTask;
        String userProfileId;
        userProfileId = this.applicationSession.getUserProfileId();
        asyncTask = new LoadAsyncTask(this, userProfileId);
        asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, workoutId);
    }

    @Override
    LiveData<Workout> getWorkout() {
        return this.workoutLiveData;
    }

    @Override
    public void saveWorkout(Workout workoutToSave) {
        String filePath;
        String userProfileId;
        SaveAsyncTask asyncTask;

        //TODO add validation
//        workoutToSave.validate();

        Workout workout = this.workoutLiveData.getValue();
        if (workout != null) {
            workout.update(workoutToSave);
            userProfileId = this.applicationSession.getUserProfileId();
            asyncTask = new SaveAsyncTask(this, userProfileId);
            asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, workout);
        }
    }

    @Override
    void cancelWorkoutEdit() {
        this.workoutLiveData.setValue(null);
        this.action.setValue(WorkoutEditFragmentAction.GOTO_WORKOUTS);
    }

    @Override
    SingleLiveEvent<WorkoutEditFragmentAction> getAction() {
        return this.action;
    }

    @Override
    int[] getWorkoutPossibleColors() {
        return this.workoutsService.getPossibleColors();
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

    static class LoadAsyncTask extends AsyncTask<String, Void, Workout> {
        private WorkoutEditViewModel workoutEditViewModel;
        private final String userProfileId;

        LoadAsyncTask(WorkoutEditViewModel workoutEditViewModel, String userProfileId) {
            this.workoutEditViewModel = workoutEditViewModel;
            this.userProfileId = userProfileId;
        }

        @Override
        protected Workout doInBackground(String... strings) {
            Workout data;
            String workoutId = strings[0];

            Log.v("loaddata", "doInBackground");
            IWorkoutsService workoutsService = this.workoutEditViewModel.workoutsService;
            data = workoutsService.loadModel(this.userProfileId, workoutId);
            return data;
        }

        @Override
        protected void onPostExecute(Workout data) {
            Log.v("loaddata", "onPostExecute");
            this.workoutEditViewModel.workoutLiveData.setValue(data);
            this.workoutEditViewModel.decreaseCountDownLatch();
        }
    }

    static class SaveAsyncTask extends AsyncTask<Workout, Void, Workout> {
        private final WorkoutEditViewModel workoutEditViewModel;
        private final String userProfileId;

        SaveAsyncTask(WorkoutEditViewModel workoutEditViewModel, String userProfileId) {
            this.workoutEditViewModel = workoutEditViewModel;
            this.userProfileId = userProfileId;
        }

        @Override
        protected Workout doInBackground(Workout... data) {
            Workout workout;

            Log.v("savedata", "doInBackground");
            IWorkoutsService workoutsService = this.workoutEditViewModel.workoutsService;
            workout = data[0];
            workoutsService.saveModel(this.userProfileId, workout);
            return workout;
        }

        @Override
        protected void onPostExecute(Workout data) {
            Log.v("savedata", "onPostExecute");
            this.workoutEditViewModel.decreaseCountDownLatch();
            this.workoutEditViewModel.workoutLiveData.setValue(data);
            this.workoutEditViewModel.action.setValue(WorkoutEditFragmentAction.GOTO_WORKOUTS);
        }
    }
}
