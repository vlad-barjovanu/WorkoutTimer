package com.vbarjovanu.workouttimer.ui.generic.viewmodels;

import android.app.Application;
import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;

import com.vbarjovanu.workouttimer.IMainActivityViewModel;
import com.vbarjovanu.workouttimer.ui.colors.IColorsPickerViewModel;
import com.vbarjovanu.workouttimer.ui.home.IHomeViewModel;
import com.vbarjovanu.workouttimer.ui.userprofiles.edit.IUserProfileEditViewModel;
import com.vbarjovanu.workouttimer.ui.userprofiles.images.IUserProfilesImagesService;
import com.vbarjovanu.workouttimer.ui.userprofiles.list.IUserProfilesViewModel;
import com.vbarjovanu.workouttimer.ui.workouts.edit.IWorkoutEditViewModel;
import com.vbarjovanu.workouttimer.ui.workouts.list.IWorkoutsViewModel;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.isA;
import static org.junit.Assert.*;

public class CustomViewModelFactoryTest {

    private CustomViewModelFactory factory;

    @Before
    public void setUp() {
        this.factory = CustomViewModelFactory.getInstance((Application) InstrumentationRegistry.getInstrumentation().getTargetContext().getApplicationContext());
    }

    @After
    public void tearDown() {
    }

    @Test
    public void getInstance() {
        assertNotNull(this.factory);
        Assert.assertThat(this.factory, isA(CustomViewModelFactory.class));
    }

    @Test
    public void getUserProfilesImagesService() {
        IUserProfilesImagesService userProfilesImagesService = this.factory.getUserProfilesImagesService();
        assertNotNull(userProfilesImagesService);
        assertThat(userProfilesImagesService, isA(IUserProfilesImagesService.class));
    }

    @Test
    public void createIMainActivityViewModel() {
        IMainActivityViewModel model = this.factory.create(IMainActivityViewModel.class);
        assertNotNull(model);
        assertThat(model, isA(IMainActivityViewModel.class));
    }

    @Test
    public void createIWorkoutsViewModel() {
        IWorkoutsViewModel model = this.factory.create(IWorkoutsViewModel.class);
        assertNotNull(model);
        assertThat(model, isA(IWorkoutsViewModel.class));
    }

    @Test
    public void createIWorkoutEditViewModel() {
        IWorkoutEditViewModel model = this.factory.create(IWorkoutEditViewModel.class);
        assertNotNull(model);
        assertThat(model, isA(IWorkoutEditViewModel.class));
    }

    @Test
    public void createIUserProfilesViewModel() {
        IUserProfilesViewModel model = this.factory.create(IUserProfilesViewModel.class);
        assertNotNull(model);
        assertThat(model, isA(IUserProfilesViewModel.class));
    }

    @Test
    public void createIUserProfileEditViewModel() {
        IUserProfileEditViewModel model = this.factory.create(IUserProfileEditViewModel.class);
        assertNotNull(model);
        assertThat(model, isA(IUserProfileEditViewModel.class));
    }

    @Test
    public void createIColorsPickerViewModel() {
        IColorsPickerViewModel model = this.factory.create(IColorsPickerViewModel.class);
        assertNotNull(model);
        assertThat(model, isA(IColorsPickerViewModel.class));
    }

    @Test
    public void createIHomeViewModel() {
        IHomeViewModel model = this.factory.create(IHomeViewModel.class);
        assertNotNull(model);
        assertThat(model, isA(IHomeViewModel.class));
    }
}