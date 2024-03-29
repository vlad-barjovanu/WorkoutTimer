package com.vbarjovanu.workouttimer.ui.workouts.training.models;

import androidx.annotation.NonNull;
import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.Observable;

import com.vbarjovanu.workouttimer.business.models.workouts.Workout;

import java.io.Serializable;

public class WorkoutTrainingModel extends BaseObservable implements Serializable {

    @NonNull
    private final Workout workout;
    private final boolean includeLastRest;
    private WorkoutTrainingItemModelsList workoutTrainingItems;
    private int currentIndex;
    private boolean inTraining;
    transient private Observable.OnPropertyChangedCallback onPropertyChangedCallback;
    private boolean locked;
    private boolean soundOn;
    private boolean vibrateOn;
    private boolean displayRemainingDuration;

    /**
     * @param workout         workout to train
     * @param includeLastRest true if to include the rest duration for the last cycle and last set
     */
    public WorkoutTrainingModel(@NonNull Workout workout, boolean includeLastRest) {
        this.workout = workout;
        this.includeLastRest = includeLastRest;
        this.inTraining = false;
        this.buildWorkoutTrainingItems();
        this.currentIndex = 0;
        this.locked = false;
        this.setSoundOn(true);
        this.setVibrateOn(true);
        this.setDisplayRemainingDuration(!workout.isIncreaseDuration());
    }

    public void close() {
        this.removeObserverWorkoutTrainingItemsChanges();
    }

    @NonNull
    @Bindable
    public Workout getWorkout() {
        return workout;
    }

    @SuppressWarnings("WeakerAccess")
    @Bindable
    public int getTotalInitialDuration() {
        return this.computeInitialDurationFromIndex(0);
    }

    @Bindable
    public int getTotalRemainingDuration() {
        return this.computeRemainingDurationFromIndex(this.currentIndex);
    }

    private int computeInitialDurationFromIndex(int index) {
        int d = 0;
        if (index >= 0 && index < this.workoutTrainingItems.size()) {
            for (int i = index; i < this.workoutTrainingItems.size(); i++) {
                d += this.workoutTrainingItems.get(i).getInitialDuration();
            }
        }
        return d;
    }

    private int computeRemainingDurationFromIndex(int index) {
        int d = 0;
        if (index >= 0 && index < this.workoutTrainingItems.size()) {
            for (int i = index; i < this.workoutTrainingItems.size(); i++) {
                d += this.workoutTrainingItems.get(i).getRemainingDuration();
            }
        }
        return d;
    }

    public WorkoutTrainingItemModelsList getWorkoutTrainingItems() {
        return workoutTrainingItems;
    }

    @Bindable
    public WorkoutTrainingItemModel getCurrentWorkoutTrainingItem() {
        return this.workoutTrainingItems.get(this.currentIndex);
    }

    @Bindable
    public boolean isInTraining() {
        return inTraining;
    }

    @Bindable
    public boolean isLocked() {
        return locked;
    }

    @Bindable
    public boolean isSoundOn() {
        return soundOn;
    }

    @Bindable
    public boolean isVibrateOn() {
        return vibrateOn;
    }

    @Bindable
    public boolean isDisplayRemainingDuration() {
        return displayRemainingDuration;
    }

    @SuppressWarnings("UnusedReturnValue")
    public WorkoutTrainingModel setInTraining(boolean inTraining) {
        this.inTraining = inTraining;
        this.notifyPropertyChanged(com.vbarjovanu.workouttimer.BR.inTraining);
        return this;
    }

    public WorkoutTrainingModel setLocked(boolean locked) {
        this.locked = locked;
        this.notifyPropertyChanged(com.vbarjovanu.workouttimer.BR.locked);
        return this;
    }

    public WorkoutTrainingModel setSoundOn(boolean soundOn) {
        this.soundOn = soundOn;
        this.notifyPropertyChanged(com.vbarjovanu.workouttimer.BR.soundOn);
        return this;
    }

    public WorkoutTrainingModel setVibrateOn(boolean vibrateOn) {
        this.vibrateOn = vibrateOn;
        this.notifyPropertyChanged(com.vbarjovanu.workouttimer.BR.vibrateOn);
        return this;
    }

    public WorkoutTrainingModel setDisplayRemainingDuration(boolean displayRemainingDuration) {
        this.displayRemainingDuration = displayRemainingDuration;
        return this;
    }

    /**
     * Moves to next training item if available. If not returns false
     *
     * @return true if moved to next available training item
     */
    public boolean nextTrainingItem() {
        boolean result = false;
        synchronized (this) {
            int count = 0;
            if (this.workoutTrainingItems != null) {
                count = this.workoutTrainingItems.size();
            }
            if (this.currentIndex < count - 1) {
                this.currentIndex++;
                result = true;
            }
        }
        if (result) {
            this.notifyChangeCurrentWorkoutTrainingItem();
        }
        return result;
    }

    /**
     * Moves to previous training item if available. If not returns false
     *
     * @return true if moved to previous available training item
     */
    public boolean previousTrainingItem() {
        boolean result = false;
        synchronized (this) {
            if (this.currentIndex > 0) {
                this.currentIndex--;
                result = true;
            }
        }
        if (result) {
            this.notifyChangeCurrentWorkoutTrainingItem();
        }
        return result;
    }

    /**
     * Moves to a certain training item if available. If not returns false
     *
     * @param index training item index
     * @return true if moved to desired training item
     */
    public boolean goToTrainingItem(int index) {
        boolean result = false;
        int count = 0;
        synchronized (this) {
            if (this.workoutTrainingItems != null) {
                count = this.workoutTrainingItems.size();
            }
            if (index >= 0 && index < count) {
                this.currentIndex = index;
                result = true;
            }
        }
        if (result) {
            this.notifyChangeCurrentWorkoutTrainingItem();
        }
        return result;
    }

    private void buildWorkoutTrainingItems() {
        int prepareDuration;
        int cyclesCount;
        int workDuration;
        int restDuration;
        int setsCount;
        int restBetweenSetsDuration;
        int coolDownDuration;
        String workDescription, restDescription;
        int index, cycleIndex, setIndex;

        prepareDuration = (this.workout.getPrepareDuration() == null) ? 0 : this.workout.getPrepareDuration();
        cyclesCount = (this.workout.getCyclesCount() == null) ? 0 : this.workout.getCyclesCount();
        workDuration = (this.workout.getWorkDuration() == null) ? 0 : this.workout.getWorkDuration();
        workDescription = (this.workout.getWorkDescription() == null ? "" : this.workout.getWorkDescription());
        restDuration = (this.workout.getRestDuration() == null) ? 0 : this.workout.getRestDuration();
        restDescription = (this.workout.getRestDescription() == null ? "" : this.workout.getRestDescription());
        setsCount = (this.workout.getSetsCount() == null) ? 0 : this.workout.getSetsCount();
        restBetweenSetsDuration = (this.workout.getRestBetweenSetsDuration() == null) ? 0 : this.workout.getRestBetweenSetsDuration();
        coolDownDuration = (this.workout.getCoolDownDuration() == null) ? 0 : this.workout.getCoolDownDuration();

        index = 0;
        setIndex = 1;
        cycleIndex = 1;
        this.workoutTrainingItems = new WorkoutTrainingItemModelsList();
        this.workoutTrainingItems.add(new WorkoutTrainingItemModel(WorkoutTrainingItemType.PREPARE, prepareDuration, index++, cycleIndex, setIndex));
        for (setIndex = 1; setIndex <= setsCount; setIndex++) {
            for (cycleIndex = 1; cycleIndex <= cyclesCount; cycleIndex++) {
                if (workDuration > 0) {
                    this.workoutTrainingItems.add(new WorkoutTrainingItemModel(WorkoutTrainingItemType.WORK, workDuration, index++, cycleIndex, setIndex, workDescription));
                }
                if (restDuration > 0 && (cycleIndex < cyclesCount || this.includeLastRest)) {
                    this.workoutTrainingItems.add(new WorkoutTrainingItemModel(WorkoutTrainingItemType.REST, restDuration, index++, cycleIndex, setIndex, restDescription));
                }
            }
            cycleIndex--;
            if (restBetweenSetsDuration > 0 && (setIndex < setsCount || this.includeLastRest)) {
                this.workoutTrainingItems.add(new WorkoutTrainingItemModel(WorkoutTrainingItemType.SET_REST, restBetweenSetsDuration, index++, cycleIndex, setIndex));
            }
        }
        setIndex--;
        this.workoutTrainingItems.add(new WorkoutTrainingItemModel(WorkoutTrainingItemType.COOL_DOWN, coolDownDuration, index, cycleIndex, setIndex));
        this.addObserverWorkoutTrainingItemsChanges();
    }

    private void addObserverWorkoutTrainingItemsChanges() {
        if (this.workoutTrainingItems != null) {
            Observable.OnPropertyChangedCallback onPropertyChangedCallback = new OnPropertyChangedCallback();
            for (WorkoutTrainingItemModel itemModel : workoutTrainingItems) {
                itemModel.removeOnPropertyChangedCallback(this.onPropertyChangedCallback);
                itemModel.addOnPropertyChangedCallback(onPropertyChangedCallback);
            }
            this.onPropertyChangedCallback = onPropertyChangedCallback;
        }
    }

    private void removeObserverWorkoutTrainingItemsChanges() {
        if (this.workoutTrainingItems != null) {
            for (WorkoutTrainingItemModel itemModel : workoutTrainingItems) {
                itemModel.removeOnPropertyChangedCallback(this.onPropertyChangedCallback);
            }
        }
    }

    private void notifyChangeCurrentWorkoutTrainingItem() {
        this.notifyPropertyChanged(com.vbarjovanu.workouttimer.BR.currentWorkoutTrainingItem);
    }

    public void update(WorkoutTrainingModel savedWorkoutTrainingModel) {
        this.workout.update(savedWorkoutTrainingModel.getWorkout());
        this.currentIndex = savedWorkoutTrainingModel.currentIndex;
        this.inTraining = savedWorkoutTrainingModel.inTraining;
        this.locked = savedWorkoutTrainingModel.locked;
        this.vibrateOn = savedWorkoutTrainingModel.vibrateOn;
        this.soundOn = savedWorkoutTrainingModel.soundOn;
        if (this.workoutTrainingItems != null && savedWorkoutTrainingModel.workoutTrainingItems != null && this.workoutTrainingItems.size() == savedWorkoutTrainingModel.workoutTrainingItems.size()) {
            for (int i = 0; i < this.workoutTrainingItems.size(); i++) {
                WorkoutTrainingItemModel item = workoutTrainingItems.get(i);
                WorkoutTrainingItemModel savedItem = savedWorkoutTrainingModel.workoutTrainingItems.get(i);
                if (item.getTotalIndex() == savedItem.getTotalIndex() && item.getType() == savedItem.getType()) {
                    item.update(savedItem);
                }
            }
        }
    }

    private class OnPropertyChangedCallback extends androidx.databinding.Observable.OnPropertyChangedCallback {
        @Override
        public void onPropertyChanged(Observable sender, int propertyId) {
            if (propertyId == com.vbarjovanu.workouttimer.BR.duration) {
                WorkoutTrainingModel.this.notifyPropertyChanged(com.vbarjovanu.workouttimer.BR.totalInitialDuration);
                WorkoutTrainingModel.this.notifyPropertyChanged(com.vbarjovanu.workouttimer.BR.totalRemainingDuration);
            }
        }
    }
}
