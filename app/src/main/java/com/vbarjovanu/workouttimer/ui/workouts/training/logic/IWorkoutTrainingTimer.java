package com.vbarjovanu.workouttimer.ui.workouts.training.logic;

import com.vbarjovanu.workouttimer.ui.workouts.training.models.WorkoutTrainingModel;

public interface IWorkoutTrainingTimer {
    /**
     * Loads the workoutTrainingModel that should execute
     *
     * @param workoutTrainingModel the workoutTrainingModel to train
     */
    void loadWorkout(WorkoutTrainingModel workoutTrainingModel);

    /**
     * Starts the training
     *
     * @return true if start was successful
     */
    boolean start();

    /**
     * Stops the training
     *
     * @return true if stop was successful
     */
    boolean stop();

    /**
     * Pauses the training
     * @return true if pause was successful
     */
    boolean pause();
}
