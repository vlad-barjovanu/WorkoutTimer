package com.vbarjovanu.workouttimer.preferences;

public interface IWorkoutTimerPreferences extends IBasePreferences{
    String getUserProfileId();

    void setUserProfileId(String userProfileId);

    IFileRepositoryPreferences getFileRepositoryPreferences();

    IWorkoutPreferences getWorkoutPreferences();

    IWorkoutTrainingPreferences getWorkoutTrainingPreferences();
}
