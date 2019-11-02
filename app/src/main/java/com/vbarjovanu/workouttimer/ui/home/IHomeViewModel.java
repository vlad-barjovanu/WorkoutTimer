package com.vbarjovanu.workouttimer.ui.home;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.vbarjovanu.workouttimer.ui.generic.viewmodels.ISynchronizable;

public abstract class IHomeViewModel extends AndroidViewModel implements ISynchronizable {

    IHomeViewModel(@NonNull Application application) {
        super(application);
    }

    public abstract LiveData<HomeModel> getHomeModel();

    public abstract void loadData();
}
