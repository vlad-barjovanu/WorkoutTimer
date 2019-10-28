package com.vbarjovanu.workouttimer.ui.workouts.training;

import android.view.View;

public interface WorkoutTrainingFragmentClickListners {
    void onPauseClick(View view);
    void onStartClick(View view);
    void onNextWorkoutItemClick(View view);
    void onPreviousWorkoutItemClick(View view);
}
