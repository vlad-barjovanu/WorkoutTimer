package com.vbarjovanu.workouttimer.ui.workouts.training;

import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.databinding.Observable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.vbarjovanu.workouttimer.business.models.workouts.Workout;
import com.vbarjovanu.workouttimer.business.services.workouts.IWorkoutsService;
import com.vbarjovanu.workouttimer.session.IApplicationSession;
import com.vbarjovanu.workouttimer.ui.generic.events.SingleLiveEvent;
import com.vbarjovanu.workouttimer.ui.workouts.training.actions.DurationChangeActionData;
import com.vbarjovanu.workouttimer.ui.workouts.training.actions.WorkoutTrainingActionData;
import com.vbarjovanu.workouttimer.ui.workouts.training.actions.WorkoutTrainingActions;
import com.vbarjovanu.workouttimer.ui.workouts.training.logic.IWorkoutTrainingTimer;
import com.vbarjovanu.workouttimer.ui.workouts.training.models.IWorkoutTrainingItemColorProvider;
import com.vbarjovanu.workouttimer.ui.workouts.training.models.WorkoutTrainingItemColorProvider;
import com.vbarjovanu.workouttimer.ui.workouts.training.models.WorkoutTrainingItemModel;
import com.vbarjovanu.workouttimer.ui.workouts.training.models.WorkoutTrainingItemType;
import com.vbarjovanu.workouttimer.ui.workouts.training.models.WorkoutTrainingModel;

import java.util.HashMap;
import java.util.concurrent.CountDownLatch;

public class WorkoutTrainingViewModel extends IWorkoutTrainingViewModel {
    private IWorkoutTrainingItemColorProvider workoutTrainingItemColorProvider;
    private MutableLiveData<WorkoutTrainingModel> workoutTrainingModel;
    private SingleLiveEvent<WorkoutTrainingActionData> action;
    private IWorkoutTrainingTimer workoutTrainingTimer;
    private CountDownLatch countDownLatch;
    private IApplicationSession applicationSession;
    private IWorkoutsService workoutsService;
    private Observable.OnPropertyChangedCallback onPropertyChangedCallback;

    public WorkoutTrainingViewModel(@NonNull IApplicationSession applicationSession, @NonNull IWorkoutsService workoutsService, @NonNull IWorkoutTrainingTimer workoutTrainingTimer) {
        this.applicationSession = applicationSession;
        this.workoutsService = workoutsService;
        this.workoutTrainingTimer = workoutTrainingTimer;
        this.workoutTrainingModel = new MutableLiveData<>();
        this.action = new SingleLiveEvent<>();
        this.onPropertyChangedCallback = new OnPropertyChangedCallback(this);
        this.workoutTrainingItemColorProvider = new WorkoutTrainingItemColorProvider(applicationSession.getWorkoutTimerPreferences().getWorkoutTrainingPreferences());
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
    SingleLiveEvent<WorkoutTrainingActionData> getAction() {
        return this.action;
    }

    @Override
    IWorkoutTrainingItemColorProvider getWorkoutTrainingItemColorProvider() {
        return this.workoutTrainingItemColorProvider;
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
        WorkoutTrainingModel model = this.workoutTrainingModel.getValue();
        if (model != null && !model.isLocked()) {
            this.workoutTrainingTimer.start();
        }
    }

    @Override
    void pauseWorkoutTraining() {
        WorkoutTrainingModel model = this.workoutTrainingModel.getValue();
        if (model != null && !model.isLocked()) {
            this.workoutTrainingTimer.pause();
        }
    }

    @Override
    void stopWorkoutTraining() {
        this.workoutTrainingTimer.stop();
        //TODO set action GOTO workouts
    }

    @Override
    void nextWorkoutTrainingItem() {
        boolean isInTraining;
        WorkoutTrainingModel model = this.workoutTrainingModel.getValue();
        if (model != null && !model.isLocked()) {
            isInTraining = model.isInTraining();
            if (isInTraining) {
                this.workoutTrainingTimer.stop();
            }
            if (model.nextTrainingItem()) {
                model.getCurrentWorkoutTrainingItem().resetDuration();
            }
            if (isInTraining) {
                this.workoutTrainingTimer.start();
            }
        }
    }

    @Override
    void previousWorkoutTrainingItem() {
        boolean isInTraining;
        WorkoutTrainingModel model = this.workoutTrainingModel.getValue();
        if (model != null && !model.isLocked()) {
            isInTraining = model.isInTraining();
            if (isInTraining) {
                this.workoutTrainingTimer.stop();
            }
            if (model.previousTrainingItem()) {
                model.getCurrentWorkoutTrainingItem().resetDuration();
            }
            if (isInTraining) {
                this.workoutTrainingTimer.start();
            }
        }
    }

    @Override
    void toggleLock() {
        WorkoutTrainingModel model = this.workoutTrainingModel.getValue();
        if (model != null && model.isInTraining()) {
            model.setLocked(!model.isLocked());
        }
    }

    @Override
    public void toggleSound() {
        WorkoutTrainingModel model = this.workoutTrainingModel.getValue();
        if (model != null && !model.isLocked()) {
            model.setSoundOn(!model.isSoundOn());
        }
    }

    @Override
    public void toggleVibrate() {
        WorkoutTrainingModel model = this.workoutTrainingModel.getValue();
        if (model != null && !model.isLocked()) {
            model.setVibrateOn(!model.isVibrateOn());
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

    private void onPropertyChangedCallback(Observable sender, int propertyId) {
        WorkoutTrainingModel model;
        WorkoutTrainingItemModel itemModel;
        model = this.workoutTrainingModel.getValue();
        if (model != null && model.isInTraining()) {
            itemModel = model.getCurrentWorkoutTrainingItem();
            if (itemModel != null) {
                if (propertyId == com.vbarjovanu.workouttimer.BR.totalRemainingDuration) {
                    if (itemModel.isAtStart()) {
                        switch (itemModel.getType()) {
                            case WORK:
                                this.action.postValue(new DurationChangeActionData(WorkoutTrainingActions.MARK_START_WORK, model.isSoundOn(), model.isVibrateOn()));
                                break;
                            case REST:
                            case SET_REST:
                                this.action.postValue(new DurationChangeActionData(WorkoutTrainingActions.MARK_START_REST, model.isSoundOn(), model.isVibrateOn()));
                                break;
                        }
                    } else {
                        if (itemModel.isCloseToCompletion() && !itemModel.isComplete()) {
                            this.action.postValue(new DurationChangeActionData(WorkoutTrainingActions.MARK_DURATION_CHANGE, model.isSoundOn(), model.isVibrateOn()));
                        }
                    }
                }
            }
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
            WorkoutTrainingModel data = null;
            Workout workout;
            String workoutId = strings[0];

            Log.v("loaddata", "doInBackground");
            IWorkoutsService workoutsService = this.viewModel.workoutsService;
            workout = workoutsService.loadModel(this.userProfileId, workoutId);
            if (workout != null) {
                //TODO proper initialisation of includeLastRest from Workout (add property in workout)
                includeLastRest = this.viewModel.applicationSession.getWorkoutTimerPreferences().getWorkoutPreferences().getIncludeLastRest();
                data = new WorkoutTrainingModel(workout, includeLastRest);
            }
            return data;
        }

        @Override
        protected void onPostExecute(WorkoutTrainingModel data) {
            Log.v("loaddata", "onPostExecute");
            if (data != null) {
                data.addOnPropertyChangedCallback(this.viewModel.onPropertyChangedCallback);
            }
            if (data != null) {
                this.viewModel.workoutTrainingModel.setValue(data);
                this.viewModel.workoutTrainingTimer.loadWorkout(data);
                this.viewModel.workoutTrainingTimer.start();
            }
            this.viewModel.decreaseCountDownLatch();
        }
    }

    private static class OnPropertyChangedCallback extends androidx.databinding.Observable.OnPropertyChangedCallback {
        private final WorkoutTrainingViewModel viewModel;

        OnPropertyChangedCallback(WorkoutTrainingViewModel viewModel) {
            this.viewModel = viewModel;
        }

        @Override
        public void onPropertyChanged(Observable sender, int propertyId) {
            this.viewModel.onPropertyChangedCallback(sender, propertyId);
        }
    }

}
