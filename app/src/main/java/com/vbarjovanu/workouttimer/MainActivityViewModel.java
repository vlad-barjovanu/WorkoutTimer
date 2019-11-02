package com.vbarjovanu.workouttimer;

import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.vbarjovanu.workouttimer.business.models.userprofiles.UserProfile;
import com.vbarjovanu.workouttimer.business.models.userprofiles.UserProfilesList;
import com.vbarjovanu.workouttimer.business.services.userprofiles.IUserProfilesService;
import com.vbarjovanu.workouttimer.session.IApplicationSession;
import com.vbarjovanu.workouttimer.ui.generic.events.Event;
import com.vbarjovanu.workouttimer.ui.generic.events.EventContent;

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

    @Override
    void initUserProfile(boolean navigateHome) {
        new MainActivityViewModel.LoadAsyncTask(this, this.applicationSession, this.userProfilesService).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, navigateHome);
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

    static class LoadAsyncTask extends AsyncTask<Object, Void, Object[]> {
        @NonNull
        private final MainActivityViewModel viewModel;
        @NonNull
        private final IApplicationSession applicationSession;
        @NonNull
        private final IUserProfilesService userProfilesService;

        LoadAsyncTask(@NonNull MainActivityViewModel viewModel, @NonNull IApplicationSession applicationSession, @NonNull IUserProfilesService userProfilesService) {
            this.viewModel = viewModel;
            this.applicationSession = applicationSession;
            this.userProfilesService = userProfilesService;
        }

        private UserProfile loadUserProfileFromSession() {
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
        protected Object[] doInBackground(Object... objects) {
            UserProfile userProfile;
            UserProfilesList userProfilesList;
            boolean allowNavigateHome;
            MainActivityAction action;

            /*
             * when a user profile ID is stored in session and the user profile exists, simply redirect to HOME without taking other actions
             * when no user profile ID is stored in session or an ID of a non-existing profile is stored, than:
             * - if there are no user profiles available, create a default one, register it in session and navigate HOME
             * - if there is one user profile available, register it in session and navigate HOME
             * - if there is more than one user profile available, navigate to user profiles
             */

            allowNavigateHome = (boolean) objects[0];
            userProfile = this.loadUserProfileFromSession();
            if (userProfile != null) {
                action = MainActivityAction.GOTO_HOME;
                userProfile = null; //set the user profile to null, so it's not stored again in session (it's already there)
            } else {
                userProfilesList = this.userProfilesService.loadModels();
                switch (userProfilesList.size()) {
                    case 0:
                        userProfile = this.createDefaultUserProfile();
                        action = (userProfile == null ? MainActivityAction.EXIT : MainActivityAction.GOTO_HOME); //if creating a default user profile failed, than exit the app
                        break;
                    case 1:
                        userProfile = userProfilesList.get(0);
                        action = MainActivityAction.GOTO_HOME;
                        break;
                    default:
                        userProfile = null;
                        action = MainActivityAction.GOTO_USERPROFILES;
                        break;
                }
            }
            if (!allowNavigateHome && action == MainActivityAction.GOTO_HOME) {
                //if navigate home is not allowed
                action = null;
            }
            return new Object[]{userProfile, action};
        }

        @Override
        protected void onPostExecute(Object[] objects) {
            UserProfile userProfile;
            MainActivityAction action;

            userProfile = (UserProfile) objects[0];
            action = (MainActivityAction) objects[1];

            this.setUserProfileInSession(userProfile);
            if (action != null) {
                this.viewModel.action.postValue(new EventContent<>(new MainActivityActionData(action)));
            }
            if (this.viewModel.model.getValue() != null) {
                this.viewModel.model.getValue().setUserProfileInitialised(true);
                this.viewModel.model.postValue(this.viewModel.model.getValue());
            }
            this.viewModel.decreaseCountDownLatch();
        }
    }
}
