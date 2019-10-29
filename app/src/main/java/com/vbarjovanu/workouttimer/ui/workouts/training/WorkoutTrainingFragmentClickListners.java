package com.vbarjovanu.workouttimer.ui.workouts.training;

import android.view.View;

public interface WorkoutTrainingFragmentClickListners {
    void onSoundClick(View view);

    void onVibrateClick(View view);

    void onLockClick(View view);

    void onPauseClick(View view);

    void onStartClick(View view);

    void onNextWorkoutItemClick(View view);

    void onPreviousWorkoutItemClick(View view);
}
