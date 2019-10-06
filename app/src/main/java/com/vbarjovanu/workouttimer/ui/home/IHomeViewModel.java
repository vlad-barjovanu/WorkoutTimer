package com.vbarjovanu.workouttimer.ui.home;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

public abstract class IHomeViewModel extends AndroidViewModel {

    IHomeViewModel(@NonNull Application application) {
        super(application);
    }

    public abstract LiveData<HomeModel> getHomeModel();

    public abstract void loadData();
}
