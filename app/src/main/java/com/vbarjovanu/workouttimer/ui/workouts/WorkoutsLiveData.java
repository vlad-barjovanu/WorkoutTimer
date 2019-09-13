package com.vbarjovanu.workouttimer.ui.workouts;

import android.os.AsyncTask;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.vbarjovanu.workouttimer.business.models.workouts.WorkoutsList;
import com.vbarjovanu.workouttimer.business.services.generic.IFileRepositorySettings;
import com.vbarjovanu.workouttimer.business.services.workouts.IWorkoutsService;
import com.vbarjovanu.workouttimer.business.services.workouts.WorkoutsFactory;
import com.vbarjovanu.workouttimer.ui.generic.viewmodels.ISynchronizable;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class WorkoutsLiveData extends MutableLiveData<WorkoutsList> implements ISynchronizable {
    private final IFileRepositorySettings fileRepositorySettings;
    private CountDownLatch countDownLatch;

    public WorkoutsLiveData(IFileRepositorySettings fileRepositorySettings) {
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

    public void loadWorkouts(final String profileId) {
        int corePoolSize = 60;
        int maximumPoolSize = 80;
        int keepAliveTime = 10;
        WorkoutsLiveDataAsyncTask asyncTask;

        BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<Runnable>(maximumPoolSize);
        Executor threadPoolExecutor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.SECONDS, workQueue);

        asyncTask = new WorkoutsLiveDataAsyncTask(this);
        asyncTask.executeOnExecutor(threadPoolExecutor, profileId);
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

    public static class WorkoutsLiveDataAsyncTask extends AsyncTask<String, Void, WorkoutsList> {
        WorkoutsLiveData workoutsLiveData;

        public WorkoutsLiveDataAsyncTask(WorkoutsLiveData workoutsLiveData) {
            this.workoutsLiveData = workoutsLiveData;
        }

        @Override
        protected void onPreExecute() {
            Log.v("loaddata", "preexecute");
            super.onPreExecute();
        }

        @Override
        protected WorkoutsList doInBackground(String... strings) {
            WorkoutsList data = null;
            String profileId;

            Log.v("loaddata", "doInBackground");
            profileId = strings[0];
            IWorkoutsService workoutsService = WorkoutsFactory.getWorkoutsService(this.workoutsLiveData.fileRepositorySettings);
            data = workoutsService.loadModels(profileId);
            return data;
        }

        @Override
        protected void onPostExecute(WorkoutsList data) {
            Log.v("loaddata", "onPostExecute");
            this.workoutsLiveData.setValue(data);
            this.workoutsLiveData.decreaseCountDownLatch();
        }
    }
}
