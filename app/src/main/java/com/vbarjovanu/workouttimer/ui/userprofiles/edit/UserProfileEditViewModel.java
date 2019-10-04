package com.vbarjovanu.workouttimer.ui.userprofiles.edit;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.vbarjovanu.workouttimer.business.models.userprofiles.UserProfile;
import com.vbarjovanu.workouttimer.business.services.userprofiles.IUserProfilesService;
import com.vbarjovanu.workouttimer.business.services.userprofiles.UserProfilesFactory;
import com.vbarjovanu.workouttimer.helpers.files.BitmapFileWriter;
import com.vbarjovanu.workouttimer.session.IApplicationSession;
import com.vbarjovanu.workouttimer.ui.generic.events.SingleLiveEvent;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

public class UserProfileEditViewModel extends IUserProfileEditViewModel {
    private MutableLiveData<UserProfile> userProfileEditLiveData;
    private final IUserProfilesService userProfilesService;
    private final IApplicationSession applicationSession;
    private SingleLiveEvent<UserProfileEditFragmentAction> action;
    private CountDownLatch countDownLatch;

    public UserProfileEditViewModel(@NonNull IApplicationSession applicationSession, IUserProfilesService userProfilesService) {
        super();
        this.applicationSession = applicationSession;
        this.userProfilesService = userProfilesService;
        this.userProfileEditLiveData = new MutableLiveData<>();
        this.action = new SingleLiveEvent<>();
    }

    @Override
    void loadUserProfile(String userProfileId) {
        LoadAsyncTask asyncTask;

        asyncTask = new LoadAsyncTask(this);
        asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, userProfileId);
    }

    @Override
    LiveData<UserProfile> getUserProfile() {
        return this.userProfileEditLiveData;
    }

    @Override
    SingleLiveEvent<UserProfileEditFragmentAction> getAction() {
        return this.action;
    }

    @Override
    void saveUserProfile(String name, String description, Bitmap newImageBitmap) {
        String filePath;
        UserProfile userProfile = this.userProfileEditLiveData.getValue();
        if (userProfile != null) {
            userProfile.setName(name);
            userProfile.setDescription(description);
            if (newImageBitmap != null) {
                BitmapFileWriter bitmapFileWriter = new BitmapFileWriter();
                filePath = this.userProfilesService.getImagesFolderPath() + userProfile.getId() + ".png";
                try {
                    bitmapFileWriter.writeFile(filePath, newImageBitmap, Bitmap.CompressFormat.PNG, 100);
                } catch (IOException e) {
                    filePath = null;
                }
                userProfile.setImageFilePath(filePath);
            }
            SaveAsyncTask asyncTask;
            asyncTask = new SaveAsyncTask(this);
            asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, userProfile);
        }
    }

    @Override
    void cancelUserProfileEdit() {
        this.userProfileEditLiveData.setValue(null);
        this.action.setValue(UserProfileEditFragmentAction.GOTO_USERPROFILES);
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
            this.userProfileEditViewModel.userProfileEditLiveData.setValue(data);
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
            this.userProfileEditViewModel.userProfileEditLiveData.setValue(data);
            this.userProfileEditViewModel.action.setValue(UserProfileEditFragmentAction.GOTO_USERPROFILES);
        }
    }

}
