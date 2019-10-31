package com.vbarjovanu.workouttimer.ui.home;

import android.app.Application;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.vbarjovanu.workouttimer.R;
import com.vbarjovanu.workouttimer.business.models.userprofiles.UserProfile;
import com.vbarjovanu.workouttimer.business.services.userprofiles.IUserProfilesService;
import com.vbarjovanu.workouttimer.business.services.workouts.IWorkoutsService;
import com.vbarjovanu.workouttimer.helpers.images.BitmapHelper;
import com.vbarjovanu.workouttimer.session.IApplicationSession;
import com.vbarjovanu.workouttimer.ui.userprofiles.images.IUserProfilesImagesService;
import com.vbarjovanu.workouttimer.ui.userprofiles.images.UserProfilesImagesService;

import java.util.concurrent.CountDownLatch;

public class HomeViewModel extends IHomeViewModel {

    private final MutableLiveData<HomeModel> homeModel;
    private final IWorkoutsService workoutsService;
    private final IUserProfilesService userProfilesService;
    private final IApplicationSession applicationSession;
    private final IUserProfilesImagesService userProfilesImagesService;
    private CountDownLatch countDownLatch;

    public HomeViewModel(@NonNull Application application, IApplicationSession applicationSession, IWorkoutsService workoutsService, IUserProfilesService userProfilesService, IUserProfilesImagesService userProfilesImagesService) {
        super(application);
        this.workoutsService = workoutsService;
        this.userProfilesService = userProfilesService;
        this.homeModel = new MutableLiveData<>();
        this.applicationSession = applicationSession;
        this.userProfilesImagesService = userProfilesImagesService;
    }

    @Override
    public MutableLiveData<HomeModel> getHomeModel() {
        return this.homeModel;
    }

    @Override
    public void loadData() {
        HomeViewModel.LoadAsyncTask loadAsyncTask;
        String userProfileId;

        userProfileId = this.applicationSession.getUserProfileId();
        loadAsyncTask = new HomeViewModel.LoadAsyncTask(this, this.workoutsService, this.userProfilesService, this.userProfilesImagesService, this.getApplication().getResources());
        loadAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, userProfileId);
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

    static class LoadAsyncTask extends AsyncTask<String, Void, HomeModel> {
        private final HomeViewModel viewModel;
        private final IWorkoutsService workoutsService;
        private final IUserProfilesService userProfilesService;
        private final IUserProfilesImagesService userProfilesImagesService;
        private final Resources resources;

        LoadAsyncTask(HomeViewModel viewModel, IWorkoutsService workoutsService, IUserProfilesService userProfilesService, IUserProfilesImagesService userProfilesImagesService, Resources resources) {
            this.viewModel = viewModel;
            this.workoutsService = workoutsService;
            this.userProfilesService = userProfilesService;
            this.userProfilesImagesService = userProfilesImagesService;
            this.resources = resources;
        }

        @Override
        protected HomeModel doInBackground(String... strings) {
            HomeModel homeModel;
            UserProfile userProfile;
            String workoutsText, sequencesText;
            String welcomeText;
            int workoutsCount, sequencesCount;
            String userProfileName = "";
            Bitmap userImage = null;
            String userProfileId = strings[0];

            userProfile = this.userProfilesService.loadModel(userProfileId);
            if (userProfile != null) {
                userProfileName = userProfile.getName();
                userImage = this.userProfilesImagesService.getUserImage(userProfile);
            }
            welcomeText = resources.getString(R.string.message_home_fragment_welcome, userProfileName);
            workoutsCount = this.workoutsService.getWorkoutsCount(userProfileId);
            //TODO: initialize later properly!
            sequencesCount = 0;
            if (workoutsCount > 0) {
                workoutsText = resources.getString(R.string.message_home_fragment_workouts, workoutsCount);
            } else {
                workoutsText = resources.getString(R.string.message_home_fragment_no_workouts);
            }
            //noinspection ConstantConditions
            if (sequencesCount > 0) {
                sequencesText = resources.getString(R.string.message_home_fragment_sequences, sequencesCount);
            } else {
                sequencesText = resources.getString(R.string.message_home_fragment_no_sequences);
            }

            homeModel = new HomeModel(workoutsCount, workoutsText, sequencesCount, sequencesText, welcomeText, userImage);

            return homeModel;
        }

        @Override
        protected void onPostExecute(HomeModel homeModel) {
            this.viewModel.homeModel.postValue(homeModel);
            this.viewModel.decreaseCountDownLatch();
        }
    }
}