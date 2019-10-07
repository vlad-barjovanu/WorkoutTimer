package com.vbarjovanu.workouttimer.ui.home;

import android.app.Application;
import android.graphics.Bitmap;

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

public class HomeViewModel extends IHomeViewModel {

    private final MutableLiveData<HomeModel> homeModel;
    private final IWorkoutsService workoutsService;
    private final IUserProfilesService userProfilesService;
    private final IApplicationSession applicationSession;
    private final IUserProfilesImagesService userProfilesImagesService;

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
        String workoutsText, sequencesText;
        String welcomeText;
        int workoutsCount, sequencesCount;
        UserProfile userProfile;
        String userProfileName = "";
        Bitmap userImage = null;

        userProfile = this.userProfilesService.loadModel(this.applicationSession.getUserProfileId());
        if (userProfile != null) {
            userProfileName = userProfile.getName();
            userImage = this.userProfilesImagesService.getUserImage(userProfile);
        }
        welcomeText = getApplication().getResources().getString(R.string.message_home_fragment_welcome, userProfileName);
        workoutsCount = this.workoutsService.getWorkoutsCount(this.applicationSession.getUserProfileId());
        //TODO: initialize later properly!
        sequencesCount = 0;
        if (workoutsCount > 0) {
            workoutsText = getApplication().getResources().getString(R.string.message_home_fragment_workouts, workoutsCount);
        } else {
            workoutsText = getApplication().getResources().getString(R.string.message_home_fragment_no_workouts);
        }
        //noinspection ConstantConditions
        if (sequencesCount > 0) {
            sequencesText = getApplication().getResources().getString(R.string.message_home_fragment_sequences, sequencesCount);
        } else {
            sequencesText = getApplication().getResources().getString(R.string.message_home_fragment_no_sequences);
        }

        this.homeModel.setValue(new HomeModel(workoutsCount, workoutsText, sequencesCount, sequencesText, welcomeText, userImage));
    }
}