package com.vbarjovanu.workouttimer.business.models.workouts;

import android.widget.TextView;

import androidx.databinding.BindingAdapter;
import androidx.databinding.InverseBindingAdapter;

import com.vbarjovanu.workouttimer.business.models.generic.IModel;

import java.io.Serializable;

public class Workout implements IModel<Workout>, Serializable {
    private String id;
    private String name;
    private String description;
    private Integer prepareDuration;
    private String workDescription;
    private Integer workDuration;
    private String restDescription;
    private Integer restDuration;
    private Integer cyclesCount;
    private Integer setsCount;
    private Integer restBetweenSetsDuration;
    private Integer coolDownDuration;
    private int color;
    private boolean increaseDuration;

    public Workout(String id) {
        this.setId(id);
        this.setIncreaseDuration(false);
    }

    public String getId() {
        return id;
    }

    private Workout setId(String id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public Workout setName(String name) {
        this.name = name;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public Workout setDescription(String description) {
        this.description = description;
        return this;
    }

    public Integer getPrepareDuration() {
        return prepareDuration;
    }

    public Workout setPrepareDuration(Integer prepareDuration) {
        this.prepareDuration = prepareDuration;
        return this;
    }

    public String getWorkDescription() {
        return workDescription;
    }

    public Workout setWorkDescription(String workDescription) {
        this.workDescription = workDescription;
        return this;
    }

    public Integer getWorkDuration() {
        return workDuration;
    }

    public Workout setWorkDuration(Integer workDuration) {
        this.workDuration = workDuration;
        return this;
    }

    public String getRestDescription() {
        return restDescription;
    }

    public Workout setRestDescription(String restDescription) {
        this.restDescription = restDescription;
        return this;
    }

    public Integer getRestDuration() {
        return restDuration;
    }

    public Workout setRestDuration(Integer restDuration) {
        this.restDuration = restDuration;
        return this;
    }

    public Integer getCyclesCount() {
        return cyclesCount;
    }

    public Workout setCyclesCount(Integer cyclesCount) {
        this.cyclesCount = cyclesCount;
        return this;
    }

    public Integer getSetsCount() {
        return setsCount;
    }

    public Workout setSetsCount(Integer setsCount) {
        this.setsCount = setsCount;
        return this;
    }

    public Integer getRestBetweenSetsDuration() {
        return restBetweenSetsDuration;
    }

    public Workout setRestBetweenSetsDuration(Integer restBetweenSetsDuration) {
        this.restBetweenSetsDuration = restBetweenSetsDuration;
        return this;
    }

    public Integer getCoolDownDuration() {
        return coolDownDuration;
    }

    public Workout setCoolDownDuration(Integer coolDownDuration) {
        this.coolDownDuration = coolDownDuration;
        return this;
    }

    public int getColor() {
        return color;
    }

    public Workout setColor(int color) {
        this.color = color;
        return this;
    }

    public boolean isIncreaseDuration() {
        return this.increaseDuration;
    }

    public Workout setIncreaseDuration(boolean increaseDuration) {
        this.increaseDuration = increaseDuration;
        return this;
    }

    /**
     * Update all workout properties from referenced workout
     *
     * @param object referenced workout
     */
    @Override
    public void update(Workout object) {
        Workout workout;
        workout = object;
        this.setId(workout.getId())
                .setName(workout.getName())
                .setDescription(workout.getDescription())
                .setPrepareDuration(workout.getPrepareDuration())
                .setWorkDescription(workout.getWorkDescription())
                .setWorkDuration(workout.getWorkDuration())
                .setRestDescription(workout.getRestDescription())
                .setRestDuration(workout.getRestDuration())
                .setCyclesCount(workout.getCyclesCount())
                .setSetsCount(workout.getSetsCount())
                .setRestBetweenSetsDuration(workout.getRestBetweenSetsDuration())
                .setCoolDownDuration(workout.getCoolDownDuration())
                .setColor(workout.getColor())
                .setIncreaseDuration(workout.isIncreaseDuration());
    }

    @Override
    public String getPrimaryKey() {
        return this.getId();
    }

    @Override
    public void setPrimaryKey(String primaryKey) {
        this.setId(primaryKey);
    }
}
