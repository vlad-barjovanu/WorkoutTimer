package com.vbarjovanu.workouttimer.ui.workouts.training;

import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.databinding.Observable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.vbarjovanu.workouttimer.BR;
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

import java.util.concurrent.CountDownLatch;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Function;

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
    void loadWorkout(String workoutId, WorkoutTrainingModel savedWorkoutTrainingModel) {
        WorkoutTrainingViewModel.LoadAsyncTask asyncTask;
        String userProfileId;

        userProfileId = this.applicationSession.getUserProfileId();
        asyncTask = new WorkoutTrainingViewModel.LoadAsyncTask(this, userProfileId);
        asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, workoutId, savedWorkoutTrainingModel);
    }

    @Override
    boolean isInitialised() {
        return this.workoutTrainingModel.getValue() != null;
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

    private void switchWorkoutTrainingItem(java.util.function.Consumer<WorkoutTrainingModel> func) {
        boolean isInTraining;
        WorkoutTrainingModel model = this.workoutTrainingModel.getValue();
        if (model != null && !model.isLocked()) {
            isInTraining = model.isInTraining();
            if (isInTraining) {
                this.workoutTrainingTimer.stop();
            }
            func.accept(model);
            if (isInTraining) {
                this.workoutTrainingTimer.start();
            }
        }
    }

    @Override
    void nextWorkoutTrainingItem() {
        this.switchWorkoutTrainingItem((model -> {
            model.getCurrentWorkoutTrainingItem().setDurationComplete();
            if (model.nextTrainingItem()) {
                model.getCurrentWorkoutTrainingItem().resetDuration();
            }
        }));
    }

    @Override
    void previousWorkoutTrainingItem() {
        this.switchWorkoutTrainingItem((model -> {
            model.getCurrentWorkoutTrainingItem().resetDuration();
            if (model.previousTrainingItem()) {
                model.getCurrentWorkoutTrainingItem().resetDuration();
            }
        }));
    }

    @Override
    void gotoWorkoutTrainingItem(int index) {
        this.switchWorkoutTrainingItem((model -> {
            if (index >= 0 && index < model.getWorkoutTrainingItems().size()) {
                if (index > model.getCurrentWorkoutTrainingItem().getTotalIndex()) {
                    model.getWorkoutTrainingItems().subList(model.getCurrentWorkoutTrainingItem().getTotalIndex(), index).forEach(WorkoutTrainingItemModel::setDurationComplete);
                }
                if (index < model.getCurrentWorkoutTrainingItem().getTotalIndex()) {
                    model.getWorkoutTrainingItems().subList(index + 1, model.getCurrentWorkoutTrainingItem().getTotalIndex() + 1).forEach(WorkoutTrainingItemModel::resetDuration);
                }

                if (model.goToTrainingItem(index)) {
                    model.getCurrentWorkoutTrainingItem().resetDuration();
                }
            }
        }));
    }

    private void toggle(Function<WorkoutTrainingModel, Boolean> condition, java.util.function.BiFunction<WorkoutTrainingModel, Boolean, WorkoutTrainingModel> setter, Function<WorkoutTrainingModel, Boolean> getter) {
        WorkoutTrainingModel model = this.workoutTrainingModel.getValue();
        if (model != null && condition.apply(model)) {
            setter.apply(model, !getter.apply(model));
        }
    }

    @Override
    void toggleLock() {
        this.toggle(WorkoutTrainingModel::isInTraining, WorkoutTrainingModel::setLocked, WorkoutTrainingModel::isLocked);
    }

    @Override
    public void toggleSound() {
        this.toggle(model -> !model.isLocked(), WorkoutTrainingModel::setSoundOn, WorkoutTrainingModel::isSoundOn);
    }

    @Override
    public void toggleVibrate() {
        this.toggle(model -> !model.isLocked(), WorkoutTrainingModel::setVibrateOn, WorkoutTrainingModel::isVibrateOn);
    }

    @Override
    public void toggleDisplayRemainingDuration() {
        this.toggle(model -> !model.isLocked(), WorkoutTrainingModel::setDisplayRemainingDuration, WorkoutTrainingModel::isDisplayRemainingDuration);
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
        if (model != null) {
            itemModel = model.getCurrentWorkoutTrainingItem();
            if (itemModel != null) {
                if (model.isInTraining()) {
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
                } else {
                    if (propertyId == com.vbarjovanu.workouttimer.BR.inTraining && itemModel.isComplete() && itemModel.getType() == WorkoutTrainingItemType.COOL_DOWN) {
                        //if is in training just changed from true to false (training finished) and current item training is COOL_DOWN than training is complete
                        this.action.postValue(new DurationChangeActionData(WorkoutTrainingActions.MARK_TRAINING_COMPLETE, model.isSoundOn(), model.isVibrateOn()));
                    }
                }
            }
        }
    }

    static class LoadAsyncTask extends AsyncTask<Object, Void, WorkoutTrainingModel> {
        private WorkoutTrainingViewModel viewModel;
        private final String userProfileId;
        private boolean shouldStartTraining = true;

        LoadAsyncTask(WorkoutTrainingViewModel viewModel, String userProfileId) {
            this.viewModel = viewModel;
            this.userProfileId = userProfileId;
        }

        @Override
        protected WorkoutTrainingModel doInBackground(Object... objects) {
            boolean includeLastRest;
            WorkoutTrainingModel data = null;
            Workout workout;
            String workoutId = objects[0].toString();
            WorkoutTrainingModel savedWorkoutTrainingModel = (WorkoutTrainingModel) objects[1];

            Log.v("loaddata", "doInBackground");
            IWorkoutsService workoutsService = this.viewModel.workoutsService;
            workout = workoutsService.loadModel(this.userProfileId, workoutId);
            if (workout != null) {
                //TODO proper initialisation of includeLastRest from Workout (add property in workout)
                includeLastRest = this.viewModel.applicationSession.getWorkoutTimerPreferences().getWorkoutPreferences().getIncludeLastRest();
                data = new WorkoutTrainingModel(workout, includeLastRest);
                if (savedWorkoutTrainingModel != null) {
                    this.shouldStartTraining = savedWorkoutTrainingModel.isInTraining();
                    data.update(savedWorkoutTrainingModel);
                    data.setInTraining(false);// in order for the timer to be able to start (if needed)
                }
            }
            return data;
        }

        @Override
        protected void onPostExecute(WorkoutTrainingModel data) {
            Log.v("loaddata", "onPostExecute");
            if (data != null) {
                data.addOnPropertyChangedCallback(this.viewModel.onPropertyChangedCallback);
                this.viewModel.workoutTrainingModel.setValue(data);
                this.viewModel.workoutTrainingTimer.loadWorkout(data);
                if (this.shouldStartTraining) {
                    this.viewModel.startWorkoutTraining();
                }
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
