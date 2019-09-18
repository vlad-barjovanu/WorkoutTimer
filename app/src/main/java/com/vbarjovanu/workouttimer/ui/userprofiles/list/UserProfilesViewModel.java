package com.vbarjovanu.workouttimer.ui.userprofiles.list;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.vbarjovanu.workouttimer.business.models.userprofiles.UserProfile;
import com.vbarjovanu.workouttimer.business.models.userprofiles.UserProfilesList;
import com.vbarjovanu.workouttimer.business.services.generic.IFileRepositorySettings;
import com.vbarjovanu.workouttimer.business.services.userprofiles.IUserProfilesService;
import com.vbarjovanu.workouttimer.business.services.userprofiles.UserProfilesFactory;
import com.vbarjovanu.workouttimer.session.ApplicationSessionFactory;
import com.vbarjovanu.workouttimer.ui.SingleLiveEvent;

import java.util.concurrent.CountDownLatch;

public class UserProfilesViewModel extends IUserProfilesViewModel {

    private final IFileRepositorySettings fileRepositorySettings;
    private CountDownLatch countDownLatch;
    private MutableLiveData<UserProfilesList> userProfilesLiveData;
    private SingleLiveEvent<UserProfilesFragmentActionData> actionData;

    private String selectedUserProfileId;

    public UserProfilesViewModel(@NonNull Application application, IFileRepositorySettings fileRepositorySettings) {
        super(application);
        this.fileRepositorySettings = fileRepositorySettings;
        this.userProfilesLiveData = new MutableLiveData<>();
        this.actionData = new SingleLiveEvent<>();
    }

    @Override
    void loadUserProfiles() {
        new LoadAsyncTask(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    LiveData<UserProfilesList> getUserProfiles() {
        return this.userProfilesLiveData;
    }

    @Override
    boolean setSelectedUserProfileId(String id) {
        UserProfile userProfile = null;
        if (this.userProfilesLiveData.getValue() != null) {
            userProfile = this.userProfilesLiveData.getValue().find(id);
        }
        if (userProfile != null) {
            this.selectedUserProfileId = id;
            ApplicationSessionFactory.getApplicationSession(this.getApplication()).setUserProfileId(this.selectedUserProfileId);
            this.actionData.setValue(new UserProfilesFragmentActionData(UserProfilesFragmentAction.GOTO_HOME));
            return true;
        }
        return false;
    }

    @Override
    String getSelectedUserProfileId() {
        return this.selectedUserProfileId;
    }

    @Override
    SingleLiveEvent<UserProfilesFragmentActionData> getActionData() {
        return this.actionData;
    }

    @Override
    boolean editUserProfileId(String id) {
        UserProfile userProfile = null;
        if (this.userProfilesLiveData.getValue() != null) {
            userProfile = this.userProfilesLiveData.getValue().find(id);
        }

        if( userProfile != null){
            this.actionData.setValue(new UserProfilesFragmentActionData(UserProfilesFragmentAction.GOTO_USERPROFILE_EDIT, userProfile.getId()));
            return true;
        }
        return false;
    }

    @Override
    public void setCountDownLatch(CountDownLatch countDownLatch) {
        this.countDownLatch = countDownLatch;
    }

    private void decreaseCountDownLatch() {
        if(this.countDownLatch!=null) {
            this.countDownLatch.countDown();
        }
    }

    static class LoadAsyncTask extends AsyncTask<Void, Void, UserProfilesList> {
        UserProfilesViewModel userProfilesViewModel;

        LoadAsyncTask(UserProfilesViewModel userProfilesViewModel) {
            this.userProfilesViewModel = userProfilesViewModel;
        }

        @Override
        protected void onPreExecute() {
            Log.v("loaddata", "preexecute");
            super.onPreExecute();
        }

        @Override
        protected UserProfilesList doInBackground(Void... voids) {
            UserProfilesList data;
            String profileId;

            Log.v("loaddata", "doInBackground");
            IUserProfilesService userProfilesService = UserProfilesFactory.getUserProfilesService(this.userProfilesViewModel.fileRepositorySettings);
            data = userProfilesService.loadModels();
            return data;
        }

        @Override
        protected void onPostExecute(UserProfilesList data) {
            Log.v("loaddata", "onPostExecute");
            this.userProfilesViewModel.userProfilesLiveData.setValue(data);
            this.userProfilesViewModel.decreaseCountDownLatch();
        }
    }
}