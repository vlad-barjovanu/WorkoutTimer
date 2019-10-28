package com.vbarjovanu.workouttimer.ui.workouts.training.models;

import android.graphics.Color;

import androidx.annotation.NonNull;
import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.Observable;

import com.vbarjovanu.workouttimer.BR;
import com.vbarjovanu.workouttimer.business.models.workouts.Workout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class WorkoutTrainingModel extends BaseObservable {

    @NonNull
    private final Workout workout;
    private final boolean includeLastRest;
    private List<WorkoutTrainingItemModel> workoutTrainingItems;
    private int currentIndex;
    private boolean inTraining;
    private Observable.OnPropertyChangedCallback onPropertyChangedCallback;
    @NonNull
    private HashMap<WorkoutTrainingItemType, Integer> workoutTrainingItemsColors;

    /**
     * @param workout         workout to train
     * @param includeLastRest true if to include the rest duration for the last cycle and last set
     */
    public WorkoutTrainingModel(@NonNull Workout workout, boolean includeLastRest, @NonNull HashMap<WorkoutTrainingItemType, Integer> workoutTrainingItemsColors) {
        this.workout = workout;
        this.includeLastRest = includeLastRest;
        this.inTraining = false;
        this.buildWorkoutTrainingItems();
        this.currentIndex = 0;
        this.workoutTrainingItemsColors = workoutTrainingItemsColors;
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
    public int getTotalDuration() {
        return this.computeDurationFromIndex(0);
    }

    @Bindable
    public int getTotalRemainingDuration() {
        return this.computeDurationFromIndex(this.currentIndex);
    }

    private int computeDurationFromIndex(int index) {
        int d = 0;
        for (int i = index; i < this.workoutTrainingItems.size(); i++) {
            d += this.workoutTrainingItems.get(i).getDuration();
        }
        return d;
    }

    @SuppressWarnings("WeakerAccess")
    public List<WorkoutTrainingItemModel> getWorkoutTrainingItems() {
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

    public int getColor(WorkoutTrainingItemModel itemModel) {
        WorkoutTrainingItemType itemType;
        int color;
        itemType = itemModel.getType();
        if (this.workoutTrainingItemsColors.containsKey(itemType) && this.workoutTrainingItemsColors.get(itemType) != null) {
            //noinspection ConstantConditions
            color = this.workoutTrainingItemsColors.get(itemType);
        } else {
            color = Color.parseColor("#ff33b5e5");
        }
        return color;
    }

    @SuppressWarnings("UnusedReturnValue")
    public WorkoutTrainingModel setInTraining(boolean inTraining) {
        this.inTraining = inTraining;
        this.notifyPropertyChanged(com.vbarjovanu.workouttimer.BR.inTraining);
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
    @SuppressWarnings("WeakerAccess")
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
        boolean increaseDuration;

        prepareDuration = (this.workout.getPrepareDuration() == null) ? 0 : this.workout.getPrepareDuration();
        cyclesCount = (this.workout.getCyclesCount() == null) ? 0 : this.workout.getCyclesCount();
        workDuration = (this.workout.getWorkDuration() == null) ? 0 : this.workout.getWorkDuration();
        workDescription = (this.workout.getWorkDescription() == null ? "" : this.workout.getWorkDescription());
        restDuration = (this.workout.getRestDuration() == null) ? 0 : this.workout.getRestDuration();
        restDescription = (this.workout.getRestDescription() == null ? "" : this.workout.getRestDescription());
        setsCount = (this.workout.getSetsCount() == null) ? 0 : this.workout.getSetsCount();
        restBetweenSetsDuration = (this.workout.getRestBetweenSetsDuration() == null) ? 0 : this.workout.getRestBetweenSetsDuration();
        coolDownDuration = (this.workout.getCoolDownDuration() == null) ? 0 : this.workout.getCoolDownDuration();
        increaseDuration = this.workout.isIncreaseDuration();

        index = 1;
        setIndex = 1;
        cycleIndex = 1;
        this.workoutTrainingItems = new ArrayList<>();
        this.workoutTrainingItems.add(new WorkoutTrainingItemModel(WorkoutTrainingItemType.PREPARE, prepareDuration, increaseDuration, index, cycleIndex, setIndex));
        for (setIndex = 1; setIndex <= setsCount; setIndex++) {
            for (cycleIndex = 1; cycleIndex <= cyclesCount; cycleIndex++) {
                if (workDuration > 0) {
                    this.workoutTrainingItems.add(new WorkoutTrainingItemModel(WorkoutTrainingItemType.WORK, workDuration, increaseDuration, index++, cycleIndex, setIndex, workDescription));
                }
                if (restDuration > 0 && (cycleIndex < cyclesCount || this.includeLastRest)) {
                    this.workoutTrainingItems.add(new WorkoutTrainingItemModel(WorkoutTrainingItemType.REST, restDuration, increaseDuration, index++, cycleIndex, setIndex, restDescription));
                }
            }
            cycleIndex--;
            if (restBetweenSetsDuration > 0 && (setIndex < setsCount || this.includeLastRest)) {
                this.workoutTrainingItems.add(new WorkoutTrainingItemModel(WorkoutTrainingItemType.SET_REST, restBetweenSetsDuration, increaseDuration, index++, cycleIndex, setIndex));
            }
        }
        setIndex--;
        this.workoutTrainingItems.add(new WorkoutTrainingItemModel(WorkoutTrainingItemType.COOL_DOWN, coolDownDuration, increaseDuration, index, cycleIndex, setIndex));
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

    private class OnPropertyChangedCallback extends androidx.databinding.Observable.OnPropertyChangedCallback {
        @Override
        public void onPropertyChanged(Observable sender, int propertyId) {
            if (propertyId == com.vbarjovanu.workouttimer.BR.duration) {
                WorkoutTrainingModel.this.notifyPropertyChanged(com.vbarjovanu.workouttimer.BR.totalDuration);
                WorkoutTrainingModel.this.notifyPropertyChanged(com.vbarjovanu.workouttimer.BR.totalRemainingDuration);
            }
        }
    }
}
