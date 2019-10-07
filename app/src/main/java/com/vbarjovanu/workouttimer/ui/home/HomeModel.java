package com.vbarjovanu.workouttimer.ui.home;

import android.graphics.Bitmap;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

public class HomeModel extends BaseObservable {
    private int workoutsCount;
    private int sequencesCount;
    private String workoutsCountText;
    private String sequencesCountText;
    private String welcomeText;
    private Bitmap userImage;

    @SuppressWarnings("WeakerAccess")
    public HomeModel(int workoutsCount, String workoutsCountText, int sequencesCount, String sequencesCountText, String welcomeText, Bitmap userImage) {
        this.workoutsCount = workoutsCount;
        this.sequencesCount = sequencesCount;
        this.workoutsCountText = workoutsCountText;
        this.sequencesCountText = sequencesCountText;
        this.welcomeText = welcomeText;
        this.userImage = userImage;
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

    @Bindable
    public Bitmap getUserImage() {
        return userImage;
    }
}
