package com.vbarjovanu.workouttimer;

import android.app.Application;

import androidx.annotation.NonNull;

import com.vbarjovanu.workouttimer.business.models.userprofiles.UserProfile;
import com.vbarjovanu.workouttimer.business.models.userprofiles.UserProfilesList;
import com.vbarjovanu.workouttimer.business.services.generic.IFileRepositorySettings;
import com.vbarjovanu.workouttimer.business.services.userprofiles.IUserProfilesService;
import com.vbarjovanu.workouttimer.business.services.userprofiles.UserProfilesFactory;
import com.vbarjovanu.workouttimer.session.ApplicationSessionFactory;
import com.vbarjovanu.workouttimer.session.IApplicationSession;
import com.vbarjovanu.workouttimer.ui.SingleLiveEvent;

import java.util.UUID;

public class MainActivityViewModel extends IMainActivityViewModel {
    private IFileRepositorySettings fileRepositorySettings;
    private SingleLiveEvent<MainActivityAction> action;

    public MainActivityViewModel(@NonNull Application application, IFileRepositorySettings fileRepositorySettings) {
        super(application);
        this.setFileRepositorySettings(fileRepositorySettings);
        this.action = new SingleLiveEvent<>();
    }

    private void setFileRepositorySettings(IFileRepositorySettings fileRepositorySettings) {
        this.fileRepositorySettings = fileRepositorySettings;
    }

    private UserProfile loadLastUserProfile() {
        String userProfileId;
        UserProfile userProfile = null;
        IApplicationSession applicationSession = ApplicationSessionFactory.getApplicationSession(this.getApplication().getApplicationContext());
        userProfileId = applicationSession.getUserProfileId();
        if (userProfileId != null) {
            userProfile = UserProfilesFactory.getUserProfilesService(this.fileRepositorySettings).loadModel(userProfileId);
        }
        return userProfile;
    }

    private UserProfile createDefaultUserProfile() {
        UserProfile userProfile = null;
        IUserProfilesService userProfilesService;
        try {
            userProfilesService = UserProfilesFactory.getUserProfilesService(this.fileRepositorySettings);
            userProfile = userProfilesService.createDefaultModel();
            userProfilesService.saveModel(userProfile);
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
            IApplicationSession applicationSession = ApplicationSessionFactory.getApplicationSession(this.getApplication().getApplicationContext());
            applicationSession.setUserProfileId(userProfile.getId());
        }
    }

    void initUserProfile() {
        UserProfile userProfile;
        UserProfilesList userProfilesList;
        userProfile = this.loadLastUserProfile();
        if (userProfile == null) {
            //if there is no user profile there are 2 options
            userProfilesList = UserProfilesFactory.getUserProfilesService(this.fileRepositorySettings).loadModels();
            switch (userProfilesList.size()) {
                case 0:
                    //if it doesn't exist users profiles we create a default one
                    userProfile = this.createDefaultUserProfile();
                    if (userProfile != null) {
                        this.setUserProfileInSession(userProfile);
                        this.action.setValue(MainActivityAction.GOTO_HOME);
                    } else {
                        this.action.setValue(MainActivityAction.EXIT);
                    }
                    break;
                case 1:
                    this.setUserProfileInSession(userProfilesList.get(0));
                    this.action.setValue(MainActivityAction.GOTO_HOME);
                    break;
                default:
                    //if it exists users profiles, we redirect the user to choose one from
                    this.action.setValue(MainActivityAction.GOTO_USERPROFILES);
                    break;
            }
        } else {
            this.action.setValue(MainActivityAction.GOTO_HOME);
        }
    }

    @Override
    SingleLiveEvent<MainActivityAction> getAction() {
        return this.action;
    }
}
