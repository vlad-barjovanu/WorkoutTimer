package com.vbarjovanu.workouttimer;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.vbarjovanu.workouttimer.business.services.generic.IFileRepositorySettings;
import com.vbarjovanu.workouttimer.ui.SingleLiveEvent;

public abstract class IMainActivityViewModel extends AndroidViewModel {
    IMainActivityViewModel(@NonNull Application application) {
        super(application);
    }

    /**
     * Initialises the user profile - loads the last used user profile, creates a default one
     * or sends the user to the user profiles fragment to choose one
     */
    abstract void initUserProfile();

    abstract SingleLiveEvent<MainActivityAction> getAction();
}
