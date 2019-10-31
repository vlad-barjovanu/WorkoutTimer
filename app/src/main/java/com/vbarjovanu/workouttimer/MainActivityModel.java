package com.vbarjovanu.workouttimer;

import androidx.databinding.BaseObservable;

import java.io.Serializable;

public class MainActivityModel extends BaseObservable implements Serializable {
    private boolean newEntityButtonVisible;
    private boolean saveEntityButtonVisible;
    private boolean userProfileInitialised;

    MainActivityModel() {
        this(false, false);
        this.userProfileInitialised=false;
    }

    @SuppressWarnings("WeakerAccess")
    MainActivityModel(boolean newEntityButtonVisible, boolean saveEntityButtonVisible) {
        this.newEntityButtonVisible = newEntityButtonVisible;
        this.saveEntityButtonVisible = saveEntityButtonVisible;
    }

    public boolean isNewEntityButtonVisible() {
        return newEntityButtonVisible;
    }

    MainActivityModel setNewEntityButtonVisible(boolean newEntityButtonVisible) {
        this.newEntityButtonVisible = newEntityButtonVisible;
        return this;
    }

    MainActivityModel setSaveEntityButtonVisible(boolean saveEntityButtonVisible) {
        this.saveEntityButtonVisible = saveEntityButtonVisible;
        return this;
    }

    @SuppressWarnings("WeakerAccess")
    public boolean isSaveEntityButtonVisible() {
        return saveEntityButtonVisible;
    }

    boolean isUserProfileInitialised() {
        return userProfileInitialised;
    }

    MainActivityModel setUserProfileInitialised(boolean initialised) {
        this.userProfileInitialised = initialised;
        return this;
    }
}
