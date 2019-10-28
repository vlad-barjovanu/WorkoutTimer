package com.vbarjovanu.workouttimer.ui.workouts.training;

import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.vbarjovanu.workouttimer.business.models.workouts.Workout;
import com.vbarjovanu.workouttimer.business.services.workouts.IWorkoutsService;
import com.vbarjovanu.workouttimer.session.IApplicationSession;
import com.vbarjovanu.workouttimer.ui.workouts.training.logic.IWorkoutTrainingTimer;
import com.vbarjovanu.workouttimer.ui.workouts.training.models.WorkoutTrainingItemType;
import com.vbarjovanu.workouttimer.ui.workouts.training.models.WorkoutTrainingModel;

import java.util.HashMap;
import java.util.concurrent.CountDownLatch;

public class WorkoutTrainingViewModel extends IWorkoutTrainingViewModel {
    private MutableLiveData<WorkoutTrainingModel> workoutTrainingModel;
    private IWorkoutTrainingTimer workoutTrainer;
    private CountDownLatch countDownLatch;
    private IApplicationSession applicationSession;
    private IWorkoutsService workoutsService;

    public WorkoutTrainingViewModel(@NonNull IApplicationSession applicationSession, @NonNull IWorkoutsService workoutsService, @NonNull IWorkoutTrainingTimer workoutTrainer) {
        this.applicationSession = applicationSession;
        this.workoutsService = workoutsService;
        this.workoutTrainer = workoutTrainer;
        this.workoutTrainingModel = new MutableLiveData<>();
    }

    @Override
    void close() {
        WorkoutTrainingModel model = this.workoutTrainingModel.getValue();
        if (model != null) {
            model.close();
        }
    }

    @Override
    LiveData<WorkoutTrainingModel> getWorkoutTrainingModel() {
        return this.workoutTrainingModel;
    }

    @Override
    void loadWorkout(String workoutId) {
        WorkoutTrainingViewModel.LoadAsyncTask asyncTask;
        String userProfileId;
        userProfileId = this.applicationSession.getUserProfileId();
        asyncTask = new WorkoutTrainingViewModel.LoadAsyncTask(this, userProfileId);
        asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, workoutId);
    }

    @Override
    void startWorkoutTraining() {
        this.workoutTrainer.start();
    }

    @Override
    void pauseWorkoutTraining() {
        this.workoutTrainer.pause();
    }

    @Override
    void stopWorkoutTraining() {
        this.workoutTrainer.stop();
        //TODO set action GOTO workouts
    }

    @Override
    void nextWorkoutTrainingItem() {
        boolean isInTraining;
        WorkoutTrainingModel model = this.workoutTrainingModel.getValue();
        if (model != null) {
            isInTraining = model.isInTraining();
            if (isInTraining) {
                this.workoutTrainer.stop();
            }
            if (model.nextTrainingItem()) {
                model.getCurrentWorkoutTrainingItem().resetDuration();
            }
            if (isInTraining) {
                this.workoutTrainer.start();
            }
        }
    }

    @Override
    void previousWorkoutTrainingItem() {
        boolean isInTraining;
        WorkoutTrainingModel model = this.workoutTrainingModel.getValue();
        if (model != null) {
            isInTraining = model.isInTraining();
            if (isInTraining) {
                this.workoutTrainer.stop();
            }
            if (model.previousTrainingItem()) {
                model.getCurrentWorkoutTrainingItem().resetDuration();
            }
            if (isInTraining) {
                this.workoutTrainer.start();
            }
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

    static class LoadAsyncTask extends AsyncTask<String, Void, WorkoutTrainingModel> {
        private WorkoutTrainingViewModel viewModel;
        private final String userProfileId;

        LoadAsyncTask(WorkoutTrainingViewModel viewModel, String userProfileId) {
            this.viewModel = viewModel;
            this.userProfileId = userProfileId;
        }

        @Override
        protected WorkoutTrainingModel doInBackground(String... strings) {
            boolean includeLastRest;
            WorkoutTrainingModel data;
            Workout workout;
            String workoutId = strings[0];

            Log.v("loaddata", "doInBackground");
            IWorkoutsService workoutsService = this.viewModel.workoutsService;
            workout = workoutsService.loadModel(this.userProfileId, workoutId);
            //TODO proper initialisation of includeLastRest from Workout (add property in workout)
            includeLastRest = this.viewModel.applicationSession.getWorkoutTimerPreferences().getWorkoutPreferences().getIncludeLastRest();
            data = new WorkoutTrainingModel(workout, includeLastRest, this.getWorkoutTrainingItemsColors());
            return data;
        }

        private HashMap<WorkoutTrainingItemType, Integer> getWorkoutTrainingItemsColors() {
            HashMap<WorkoutTrainingItemType, Integer> workoutTrainingItemsColors;
            workoutTrainingItemsColors = new HashMap<>();
            for (WorkoutTrainingItemType itemType : WorkoutTrainingItemType.values()) {
                workoutTrainingItemsColors.put(itemType, this.viewModel.applicationSession.getWorkoutTimerPreferences().getWorkoutTrainingPreferences().getColor(itemType));
            }

            return workoutTrainingItemsColors;
        }

        @Override
        protected void onPostExecute(WorkoutTrainingModel data) {
            Log.v("loaddata", "onPostExecute");
            this.viewModel.workoutTrainingModel.setValue(data);
            this.viewModel.workoutTrainer.loadWorkout(data);
            this.viewModel.workoutTrainer.start();
            this.viewModel.decreaseCountDownLatch();
        }
    }
}
