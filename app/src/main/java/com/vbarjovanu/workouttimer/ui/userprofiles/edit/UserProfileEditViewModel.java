package com.vbarjovanu.workouttimer.ui.userprofiles.edit;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.vbarjovanu.workouttimer.business.models.userprofiles.UserProfile;
import com.vbarjovanu.workouttimer.business.services.userprofiles.IUserProfilesService;
import com.vbarjovanu.workouttimer.helpers.files.BitmapFileWriter;
import com.vbarjovanu.workouttimer.ui.generic.events.SingleLiveEvent;
import com.vbarjovanu.workouttimer.ui.userprofiles.images.IUserProfilesImagesService;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

public class UserProfileEditViewModel extends IUserProfileEditViewModel {
    private MutableLiveData<UserProfileModel> userProfileModel;
    private final IUserProfilesService userProfilesService;
    private final IUserProfilesImagesService userProfilesImagesService;
    private SingleLiveEvent<UserProfileEditFragmentAction> action;
    private CountDownLatch countDownLatch;

    public UserProfileEditViewModel(IUserProfilesService userProfilesService, IUserProfilesImagesService userProfilesImagesService) {
        super();
        this.userProfilesService = userProfilesService;
        this.userProfileModel = new MutableLiveData<>();
        this.action = new SingleLiveEvent<>();
        this.userProfilesImagesService = userProfilesImagesService;
    }

    @Override
    void loadUserProfile(String userProfileId) {
        LoadAsyncTask asyncTask;

        asyncTask = new LoadAsyncTask(this);
        asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, userProfileId);
    }

    @Override
    void newUserProfile() {
        UserProfile userProfile;
        try {
            userProfile = this.userProfilesService.createModel();
            this.decreaseCountDownLatch();
            this.userProfileModel.setValue(new UserProfileModel(userProfile, this.userProfilesImagesService.getUserImage(userProfile)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    LiveData<UserProfileModel> getUserProfileModel() {
        return this.userProfileModel;
    }

    @Override
    SingleLiveEvent<UserProfileEditFragmentAction> getAction() {
        return this.action;
    }

    @Override
    void saveUserProfile(UserProfileModel userProfileModelToSave) {
        UserProfileModel userProfileModel;
        SaveAsyncTask asyncTask;

        userProfileModel = this.userProfileModel.getValue();
        if (userProfileModel != null) {
            this.userProfilesImagesService.setUserImage(userProfileModel.getUserProfile(), userProfileModel.getUserImage());
            userProfileModel.getUserProfile().update(userProfileModelToSave.getUserProfile());
            asyncTask = new SaveAsyncTask(this);
            asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, userProfileModel.getUserProfile());
        }
    }

    @Override
    void cancelUserProfileEdit() {
        this.userProfileModel.setValue(null);
        this.action.setValue(UserProfileEditFragmentAction.GOTO_USERPROFILES);
    }

    @Override
    public void setCountDownLatch(CountDownLatch countDownLatch) {
        this.countDownLatch = countDownLatch;
    }

    @Override
    public void deleteUserImage() {
        UserProfileModel userProfileModel;
        userProfileModel = this.userProfileModel.getValue();
        if (userProfileModel != null) {
            userProfileModel.getUserProfile().setImageFilePath(null);
            userProfileModel.setUserImage(this.userProfilesImagesService.getUserImage(userProfileModel.getUserProfile()));
            this.userProfileModel.setValue(userProfileModel);
        }
    }

    private void decreaseCountDownLatch() {
        if (this.countDownLatch != null) {
            this.countDownLatch.countDown();
        }
    }

    static class LoadAsyncTask extends AsyncTask<String, Void, UserProfile> {
        UserProfileEditViewModel userProfileEditViewModel;

        LoadAsyncTask(UserProfileEditViewModel userProfileEditViewModel) {
            this.userProfileEditViewModel = userProfileEditViewModel;
        }

        @Override
        protected UserProfile doInBackground(String... strings) {
            UserProfile data;
            String userProfileId = strings[0];

            Log.v("loaddata", "doInBackground");
            data = this.userProfileEditViewModel.userProfilesService.loadModel(userProfileId);
            return data;
        }

        @Override
        protected void onPostExecute(UserProfile data) {
            Log.v("loaddata", "onPostExecute");
            this.userProfileEditViewModel.userProfileModel.setValue(new UserProfileModel(data, this.userProfileEditViewModel.userProfilesImagesService.getUserImage(data)));
            this.userProfileEditViewModel.decreaseCountDownLatch();
        }
    }

    static class SaveAsyncTask extends AsyncTask<UserProfile, Void, UserProfile> {
        private final UserProfileEditViewModel userProfileEditViewModel;

        SaveAsyncTask(UserProfileEditViewModel userProfileEditViewModel) {
            this.userProfileEditViewModel = userProfileEditViewModel;
        }

        @Override
        protected UserProfile doInBackground(UserProfile... data) {
            UserProfile userProfile;

            Log.v("savedata", "doInBackground");
            userProfile = data[0];
            this.userProfileEditViewModel.userProfilesService.saveModel(userProfile);
            return userProfile;
        }

        @Override
        protected void onPostExecute(UserProfile data) {
            Log.v("savedata", "onPostExecute");
            this.userProfileEditViewModel.decreaseCountDownLatch();
            this.userProfileEditViewModel.userProfileModel.setValue(new UserProfileModel(data, this.userProfileEditViewModel.userProfilesImagesService.getUserImage(data)));
            this.userProfileEditViewModel.action.setValue(UserProfileEditFragmentAction.GOTO_USERPROFILES);
        }
    }

}
