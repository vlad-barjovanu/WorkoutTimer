package com.vbarjovanu.workouttimer.ui.home;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

public class HomeModel extends BaseObservable {
    private int workoutsCount;
    private int sequencesCount;
    private String workoutsCountText;
    private String sequencesCountText;
    private String welcomeText;

    @SuppressWarnings("WeakerAccess")
    public HomeModel(int workoutsCount, String workoutsCountText, int sequencesCount, String sequencesCountText, String welcomeText) {
        this.workoutsCount = workoutsCount;
        this.sequencesCount = sequencesCount;
        this.workoutsCountText = workoutsCountText;
        this.sequencesCountText = sequencesCountText;
        this.welcomeText = welcomeText;
    }

    @Bindable
    public String getWorkoutsCountText() {
        return workoutsCountText;
    }

    @Bindable
    public String getSequencesCountText() {
        return sequencesCountText;
    }

    @Bindable
    public String getWelcomeText() {
        return welcomeText;
    }

    @Bindable
    public int getWorkoutsCount() {
        return workoutsCount;
    }

    @Bindable
    public int getSequencesCount() {
        return sequencesCount;
    }
}
