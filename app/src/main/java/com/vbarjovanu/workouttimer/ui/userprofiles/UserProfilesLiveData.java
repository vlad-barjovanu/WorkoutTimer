package com.vbarjovanu.workouttimer.ui.userprofiles;

import android.os.AsyncTask;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.vbarjovanu.workouttimer.business.models.userprofiles.UserProfilesList;
import com.vbarjovanu.workouttimer.business.services.generic.IFileRepositorySettings;
import com.vbarjovanu.workouttimer.business.services.userprofiles.IUserProfilesService;
import com.vbarjovanu.workouttimer.business.services.userprofiles.UserProfilesFactory;
import com.vbarjovanu.workouttimer.ui.generic.viewmodels.ISynchronizable;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class UserProfilesLiveData extends MutableLiveData<UserProfilesList> implements ISynchronizable {
    private final IFileRepositorySettings fileRepositorySettings;
    private CountDownLatch countDownLatch;

    public UserProfilesLiveData(IFileRepositorySettings fileRepositorySettings) {
        this.fileRepositorySettings = fileRepositorySettings;
    }

    @Override
    protected void onActive() {
        super.onActive();
    }

    @Override
    protected void onInactive() {
        super.onInactive();
    }

    public void loadUserProfiles() {
        int corePoolSize = 60;
        int maximumPoolSize = 80;
        int keepAliveTime = 10;
        WorkoutsLiveDataAsyncTask asyncTask;

        BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<Runnable>(maximumPoolSize);
        Executor threadPoolExecutor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.SECONDS, workQueue);

        asyncTask = new WorkoutsLiveDataAsyncTask(this);
        asyncTask.executeOnExecutor(threadPoolExecutor);
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


    public static class WorkoutsLiveDataAsyncTask extends AsyncTask<Void, Void, UserProfilesList> {
        UserProfilesLiveData userProfilesLiveData;

        public WorkoutsLiveDataAsyncTask(UserProfilesLiveData userProfilesLiveData) {
            this.userProfilesLiveData = userProfilesLiveData;
        }

        @Override
        protected void onPreExecute() {
            Log.v("loaddata", "preexecute");
            super.onPreExecute();
        }

        @Override
        protected UserProfilesList doInBackground(Void... voids) {
            UserProfilesList data = null;
            String profileId;

            Log.v("loaddata", "doInBackground");
            IUserProfilesService userProfilesService = UserProfilesFactory.getUserProfilesService(this.userProfilesLiveData.fileRepositorySettings);
            data = userProfilesService.loadModels();
            return data;
        }

        @Override
        protected void onPostExecute(UserProfilesList data) {
            Log.v("loaddata", "onPostExecute");
            this.userProfilesLiveData.setValue(data);
            this.userProfilesLiveData.decreaseCountDownLatch();
        }
    }
}
