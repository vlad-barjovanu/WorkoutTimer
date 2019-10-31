package com.vbarjovanu.workouttimer;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.vbarjovanu.workouttimer.ui.generic.events.Event;
import com.vbarjovanu.workouttimer.ui.generic.events.EventContent;
import com.vbarjovanu.workouttimer.ui.generic.viewmodels.ISynchronizable;

public abstract class IMainActivityViewModel extends ViewModel implements ISynchronizable {
    IMainActivityViewModel() {
        super();
    }

    /**
     * Initialises the user profile - loads the last used user profile, creates a default one
     * or sends the user to the user profiles fragment to choose one
     *
     * @param navigateHome true, if after the user profile initialisation an action to navigate HOME will be triggered
     */
    abstract void initUserProfile(boolean navigateHome);

    abstract public Event<EventContent<MainActivityActionData>> getAction();

    abstract public void initModel(MainActivityModel mainActivityModel);

    abstract public LiveData<MainActivityModel> getModel();

    abstract public void showNewEntityButton(boolean visible);

    abstract public void showSaveEntityButton(boolean visible);

    /**
     * A new entity should be created, by user action
     */
    public abstract void newEntity();

    /**
     * The entity should be saved, by user action
     */
    public abstract void saveEntity();

    /**
     * The entity edit should be canceled, by user action
     */
    public abstract void cancelEntity();
}
