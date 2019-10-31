package com.vbarjovanu.workouttimer;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.vbarjovanu.workouttimer.business.models.userprofiles.UserProfile;
import com.vbarjovanu.workouttimer.business.models.userprofiles.UserProfilesList;
import com.vbarjovanu.workouttimer.business.services.userprofiles.IUserProfilesService;
import com.vbarjovanu.workouttimer.session.IApplicationSession;
import com.vbarjovanu.workouttimer.ui.generic.events.Event;
import com.vbarjovanu.workouttimer.ui.generic.events.EventContent;
import com.vbarjovanu.workouttimer.ui.generic.viewmodels.ISynchronizable;

import java.util.concurrent.CountDownLatch;

public class MainActivityViewModel extends IMainActivityViewModel {
    private IUserProfilesService userProfilesService;
    private IApplicationSession applicationSession;
    private Event<EventContent<MainActivityActionData>> action;
    private MutableLiveData<MainActivityModel> model;
    private CountDownLatch countDownLatch;

    public MainActivityViewModel(@NonNull IApplicationSession applicationSession, @NonNull IUserProfilesService userProfilesService) {
        super();
        this.applicationSession = applicationSession;
        this.userProfilesService = userProfilesService;
        this.action = new Event<>();
        this.model = new MutableLiveData<>(new MainActivityModel());
    }

    private UserProfile loadLastUserProfile() {
        String userProfileId;
        UserProfile userProfile = null;
        userProfileId = this.applicationSession.getUserProfileId();
        if (userProfileId != null) {
            userProfile = this.userProfilesService.loadModel(userProfileId);
        }
        return userProfile;
    }

    private UserProfile createDefaultUserProfile() {
        UserProfile userProfile = null;
        try {
            userProfile = this.userProfilesService.createDefaultModel();
            this.userProfilesService.saveModel(userProfile);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return userProfile;
    }

    /**
     * Sets the user profile id in application session
     *
     * @param userProfile user profile
     */
    private void setUserProfileInSession(UserProfile userProfile) {
        if (userProfile != null) {
            this.applicationSession.setUserProfileId(userProfile.getId());
        }
    }

    @Override
    void initUserProfile(boolean navigateHome) {
        UserProfile userProfile;
        UserProfilesList userProfilesList;
        //noinspection ConstantConditions
        if (!this.model.getValue().isUserProfileInitialised()) {
            userProfile = this.loadLastUserProfile();
            if (userProfile == null) {
                //if there is no user profile there are 2 options
                userProfilesList = this.userProfilesService.loadModels();
                switch (userProfilesList.size()) {
                    case 0:
                        //if it doesn't exist users profiles we create a default one
                        userProfile = this.createDefaultUserProfile();
                        if (userProfile != null) {
                            this.setUserProfileInSession(userProfile);
                            if (navigateHome) {
                                this.action.setValue(new EventContent<>(new MainActivityActionData(MainActivityAction.GOTO_HOME)));
                            }
                        } else {
                            this.action.setValue(new EventContent<>(new MainActivityActionData(MainActivityAction.EXIT)));
                        }
                        break;
                    case 1:
                        this.setUserProfileInSession(userProfilesList.get(0));
                        if (navigateHome) {
                            this.action.setValue(new EventContent<>(new MainActivityActionData(MainActivityAction.GOTO_HOME)));
                        }
                        break;
                    default:
                        //if it exists users profiles, we redirect the user to choose one from
                        this.action.setValue(new EventContent<>(new MainActivityActionData(MainActivityAction.GOTO_USERPROFILES)));
                        break;
                }
            } else {
                if (navigateHome) {
                    this.action.setValue(new EventContent<>(new MainActivityActionData(MainActivityAction.GOTO_HOME)));
                }
            }
            this.model.getValue().setUserProfileInitialised(true);
            this.model.setValue(this.model.getValue());
        }
        this.decreaseCountDownLatch();
    }

    @Override
    public Event<EventContent<MainActivityActionData>> getAction() {
        return this.action;
    }

    @Override
    public void initModel(MainActivityModel mainActivityModel) {
        MainActivityModel model = this.model.getValue();
        if (model != null && mainActivityModel != null) {
            model.setSaveEntityButtonVisible(mainActivityModel.isSaveEntityButtonVisible());
            model.setNewEntityButtonVisible(mainActivityModel.isNewEntityButtonVisible());
        }
    }

    @Override
    public void showNewEntityButton(boolean visible) {
        if (this.model != null && this.model.getValue() != null) {
            this.model.getValue().setNewEntityButtonVisible(visible);
            this.model.setValue(this.model.getValue());
        }
    }

    @Override
    public void showSaveEntityButton(boolean visible) {
        if (this.model != null && this.model.getValue() != null) {
            this.model.getValue().setSaveEntityButtonVisible(visible);
            this.model.setValue(this.model.getValue());
        }
    }

    @Override
    public MutableLiveData<MainActivityModel> getModel() {
        return this.model;
    }

    @Override
    public void newEntity() {
        this.action.setValue(new EventContent<>(new MainActivityActionData(MainActivityAction.NEW_ENTITY_BUTTON_CLICKED)));
    }

    @Override
    public void saveEntity() {
        this.action.setValue(new EventContent<>(new MainActivityActionData(MainActivityAction.SAVE_ENTITY_BUTTON_CLICKED)));
    }

    @Override
    public void cancelEntity() {
        this.action.setValue(new EventContent<>(new MainActivityActionData(MainActivityAction.CANCEL_ENTITY_EDIT_BUTTON_CLICKED)));
    }

    @Override
    public void setCountDownLatch(CountDownLatch countDownLatch) {
        this.countDownLatch = countDownLatch;
    }

    private void decreaseCountDownLatch() {
        if (this.countDownLatch != null) {
            this.countDownLatch.countDown();
        }
    }

}
