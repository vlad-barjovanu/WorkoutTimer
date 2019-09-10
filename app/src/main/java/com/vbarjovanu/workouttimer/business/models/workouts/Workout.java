package com.vbarjovanu.workouttimer.business.models.workouts;

import com.vbarjovanu.workouttimer.business.models.generic.IModel;

public class Workout implements IModel<Workout> {
    private String id;
    private String name;
    private int prepareDuration;
    private String workDescription;
    private int workDuration;
    private String restDescription;
    private int restDuration;
    private int cyclesCount;
    private int setsCount;
    private int restBetweenSetsCount;
    private int coolDownDuration;

    public Workout(String id) {
        this.setId(id);
    }

    public String getId() {
        return id;
    }

    private void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public Workout setName(String name) {
        this.name = name;
        return this;
    }

    public int getPrepareDuration() {
        return prepareDuration;
    }

    public Workout setPrepareDuration(int prepareDuration) {
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

    public int getWorkDuration() {
        return workDuration;
    }

    public Workout setWorkDuration(int workDuration) {
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

    public int getRestDuration() {
        return restDuration;
    }

    public Workout setRestDuration(int restDuration) {
        this.restDuration = restDuration;
        return this;
    }

    public int getCyclesCount() {
        return cyclesCount;
    }

    public Workout setCyclesCount(int cyclesCount) {
        this.cyclesCount = cyclesCount;
        return this;
    }

    public int getSetsCount() {
        return setsCount;
    }

    public Workout setSetsCount(int setsCount) {
        this.setsCount = setsCount;
        return this;
    }

    public int getRestBetweenSetsCount() {
        return restBetweenSetsCount;
    }

    public Workout setRestBetweenSetsCount(int restBetweenSetsCount) {
        this.restBetweenSetsCount = restBetweenSetsCount;
        return this;
    }

    public int getCoolDownDuration() {
        return coolDownDuration;
    }

    public Workout setCoolDownDuration(int coolDownDuration) {
        this.coolDownDuration = coolDownDuration;
        return this;
    }

    /**
     * Update all workout properties from referenced workout
     * @param object referenced workout
     */
    @Override
    public void update(Workout object) {
        Workout workout;
        workout = object;
        this.setId(workout.getId());
        this.setName(workout.getName());
        this.setPrepareDuration(workout.getPrepareDuration());
        this.setWorkDescription(workout.getWorkDescription());
        this.setWorkDuration(workout.getWorkDuration());
        this.setRestDescription(workout.getRestDescription());
        this.setRestDuration(workout.getRestDuration());
        this.setCyclesCount(workout.getCyclesCount());
        this.setSetsCount(workout.getSetsCount());
        this.setRestBetweenSetsCount(workout.getRestBetweenSetsCount());
        this.setCoolDownDuration(workout.getCoolDownDuration());
    }

    @Override
    public String getPrimaryKey() {
        return this.getId();
    }
}
