package com.vbarjovanu.workouttimer.ui.workouts.training.logic;

import android.annotation.SuppressLint;

import com.vbarjovanu.workouttimer.ui.workouts.training.models.WorkoutTrainingModel;

import java.util.Timer;
import java.util.function.Supplier;

public class WorkoutTrainingTimer implements IWorkoutTrainingTimer {
    private WorkoutTrainingModel workoutTrainingModel;
    private Timer timer;
    private TimerTask timerTask;

    @Override
    public void loadWorkout(WorkoutTrainingModel workoutTrainingModel) {
        this.workoutTrainingModel = workoutTrainingModel;
    }

    private boolean template(Supplier<Boolean> condition, Supplier<Boolean> function) {
        boolean result = false;
        synchronized (this) {
            if (condition.get()) {
                result = function.get();
                this.workoutTrainingModel.setInTraining(!this.workoutTrainingModel.isInTraining());
            }
        }
        return result;
    }

    @Override
    public boolean start() {
        return this.template(() -> this.workoutTrainingModel != null && !this.workoutTrainingModel.isInTraining(), () -> (this.timerTask != null ? this.resumeTimer() : this.startTimer()));
    }

    @Override
    public boolean stop() {
        return this.template(() -> this.workoutTrainingModel != null && this.workoutTrainingModel.isInTraining(), this::stopTimer);
    }

    @Override
    public boolean pause() {
        return this.template(() -> this.workoutTrainingModel != null && this.workoutTrainingModel.isInTraining(), this::pauseTimer);
    }

    private boolean startTimer() {
        this.timer = new Timer();
        this.timerTask = new TimerTask(10, false);
        timer.schedule(this.timerTask, 0, 100);
        return true;
    }

    private boolean resumeTimer() {
        if (this.timerTask != null && this.timerTask.isPaused()) {
            this.timerTask.setPaused(false);
            return true;
        }
        return false;
    }

    private boolean stopTimer() {
        boolean result = false;
        if (this.timerTask != null) {
            result = this.timerTask.cancel();
            this.timerTask = null;
        }
        if (this.timer != null) {
            this.timer.cancel();
            this.timer = null;
        }
        return result;
    }

    private boolean pauseTimer() {
        if (this.timerTask != null) {
            this.timerTask.setPaused(true);
            return true;
        }
        return false;
    }

    private void run() {
        if (this.workoutTrainingModel.isInTraining()) {
            this.workoutTrainingModel.getCurrentWorkoutTrainingItem().alterDuration();
            if (this.workoutTrainingModel.getCurrentWorkoutTrainingItem().isComplete()) {
                if (this.workoutTrainingModel.nextTrainingItem()) {
                    this.workoutTrainingModel.getCurrentWorkoutTrainingItem().resetDuration();//to start fresh
                } else {
                    this.stop();
                }
            }
        }
    }

    private class TimerTask extends java.util.TimerTask {
        /**
         * Number of run executions, until run logic is actually called
         */
        private final int frequency;
        private int runCount;
        private boolean isPaused;

        TimerTask(int frequency, boolean isPaused) {
            this.frequency = frequency;
            this.isPaused = isPaused;
            this.resetFrequency();
        }

        @Override
        public void run() {
            if (!this.isPaused) {
                if (this.isFrequencyReached()) {
                    this.resetFrequency();
                    WorkoutTrainingTimer.this.run();
                }
                this.runCount++;
            }
        }

        private void resetFrequency() {
            this.runCount = 0;
        }

        /**
         * Checks if the execution count reached the required frequency
         *
         * @return true if frequency is reached
         */
        private boolean isFrequencyReached() {
            return (this.runCount >= this.frequency);
        }

        boolean isPaused() {
            return this.isPaused;
        }

        TimerTask setPaused(boolean paused) {
            this.isPaused = paused;
            return this;
        }
    }
}
