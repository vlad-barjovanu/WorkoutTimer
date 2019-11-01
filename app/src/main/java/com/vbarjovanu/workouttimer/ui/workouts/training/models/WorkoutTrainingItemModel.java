package com.vbarjovanu.workouttimer.ui.workouts.training.models;

import androidx.annotation.NonNull;
import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.vbarjovanu.workouttimer.business.models.generic.IModel;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;

public class WorkoutTrainingItemModel extends BaseObservable implements Serializable, IModel<WorkoutTrainingItemModel> {
    @NonNull
    private final WorkoutTrainingItemType type;
    private final boolean increaseDuration;
    private int totalIndex;
    private final int cycleIndex;
    private final int setIndex;
    private AtomicInteger duration;
    private final int initialDuration;
    @NonNull
    private final String description;

    /**
     * @param type             workout training item type
     * @param duration         initial duration of the workout item
     * @param increaseDuration true if the training measurement is made from 0 towards the duration; false if training measurement is made from duration towards 0
     * @param totalIndex       index of the workout item
     * @param cycleIndex       index of the workout cycle
     * @param setIndex         index of the workout set
     */
    WorkoutTrainingItemModel(WorkoutTrainingItemType type, int duration, boolean increaseDuration, int totalIndex, int cycleIndex, int setIndex) {
        this(type, duration, increaseDuration, totalIndex, cycleIndex, setIndex, "");
    }

    /**
     * @param type             workout training item type
     * @param duration         initial duration of the workout item; negative durations are interpreted as 0
     * @param increaseDuration true if the training measurement is made from 0 towards the duration; false if training measurement is made from duration towards 0
     * @param totalIndex       index of the workout item
     * @param cycleIndex       index of the workout cycle
     * @param setIndex         index of the workout set
     * @param description      description of the workout item
     */
    WorkoutTrainingItemModel(@NonNull WorkoutTrainingItemType type, int duration, boolean increaseDuration, int totalIndex, int cycleIndex, int setIndex, @NonNull String description) {
        this.type = type;
        this.initialDuration = (duration < 0 ? 0 : duration);
        this.duration = new AtomicInteger();
        this.increaseDuration = increaseDuration;
        this.totalIndex = totalIndex;
        this.cycleIndex = cycleIndex;
        this.setIndex = setIndex;
        this.description = description;
        this.resetDuration();
    }

    @NonNull
    @Bindable
    public WorkoutTrainingItemType getType() {
        return type;
    }

    @Bindable
    public int getDuration() {
        return duration.get();
    }

    @SuppressWarnings("WeakerAccess")
    @Bindable
    public int getInitialDuration() {
        return initialDuration;
    }

    @SuppressWarnings("WeakerAccess")
    @Bindable
    public int getRemainingDuration() {
        if (this.increaseDuration) {
            return this.getInitialDuration() - this.getDuration();
        } else {
            return this.getDuration();
        }
    }

    @Bindable
    public int getCompletedDuration() {
        return this.getInitialDuration() - this.getRemainingDuration();
    }

    @Bindable
    @NonNull
    public String getDescription() {
        return description;
    }

    @Bindable
    public int getTotalIndex() {
        return totalIndex;
    }

    @Bindable
    public int getCycleIndex() {
        return cycleIndex;
    }

    @Bindable
    public int getSetIndex() {
        return setIndex;
    }

    /**
     * Sets the duration to the initial value.
     *
     * @return The new values. May be the duration value or 0 depending on the increaseDuration flag
     */
    public int resetDuration() {
        int value;
        if (this.increaseDuration) {
            value = 0;
        } else {
            value = this.initialDuration;
        }
        this.duration.getAndSet(value);
        this.notifyPropertyChanged(com.vbarjovanu.workouttimer.BR.duration);
        return value;
    }

    /**
     * Changes the duration by incrementing or decrementing it, based on the increaseDuration flag
     * Doesn't decrement below 0, doesn't increment above initial duration
     *
     * @return the new duration value
     */
    public int alterDuration() {
        int duration;
        synchronized (this) {
            duration = this.getDuration();
            if (this.increaseDuration) {
                if (duration < this.initialDuration) {
                    duration = this.duration.incrementAndGet();
                    this.notifyPropertyChanged(com.vbarjovanu.workouttimer.BR.duration);
                }
            } else {
                if (duration > 0) {
                    duration = this.duration.decrementAndGet();
                    this.notifyPropertyChanged(com.vbarjovanu.workouttimer.BR.duration);
                }
            }
        }

        return duration;
    }

    /**
     * Changes the duration by incrementing or decrementing it, based on the increaseDuration flag, up till complete
     *
     * @return the new duration value
     */
    public int setDurationComplete(){
        int value;
        if (this.increaseDuration) {
            value = this.initialDuration;
        } else {
            value = 0;
        }
        this.duration.getAndSet(value);
        this.notifyPropertyChanged(com.vbarjovanu.workouttimer.BR.duration);
        return value;
    }

    /**
     * Checks if the workout training item is at start (duration was altered zero times since initial duration)
     * When increaseDuration flag is true it means the current value equals 0
     * When increaseDuration flag is false it means the current value equals the initial duration
     *
     * @return true if training is complete
     */
    public boolean isAtStart() {
        return (this.increaseDuration && this.getDuration() == 0)
                ||
                (!this.increaseDuration && this.getDuration() == this.initialDuration);
    }

    /**
     * Checks if the workout training item is complete (duration was altered enough times till completion)
     * When increaseDuration flag is true it means the current value equals the initial duration
     * When increaseDuration flag is false it means the current value equals 0
     *
     * @return true if training is complete
     */
    public boolean isComplete() {
        return (this.increaseDuration && this.getDuration() == this.initialDuration)
                ||
                (!this.increaseDuration && this.getDuration() == 0);
    }

    /**
     * Checks if the workout training item is close to be complete (duration was altered enough times till almost completion)
     * When increaseDuration flag is true it means the current value is greater or equal to the initial duration minus 3
     * When increaseDuration flag is false it means the current value is smaller or equal to 3
     *
     * @return true if training is close to completion
     */
    public boolean isCloseToCompletion() {
        return (this.increaseDuration && this.getDuration() >= this.initialDuration - 3)
                ||
                (!this.increaseDuration && this.getDuration() <= 3);
    }

    @Override
    public void update(WorkoutTrainingItemModel savedItem) {
        this.duration = savedItem.duration;
    }

    @Override
    public String getPrimaryKey() {
        return Integer.toString(this.totalIndex);
    }

    @Override
    public void setPrimaryKey(String primaryKey) {
        this.totalIndex = Integer.parseInt(primaryKey);
    }
}
