package com.vbarjovanu.workouttimer.ui.home;

import android.app.Application;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.test.platform.app.InstrumentationRegistry;

import com.vbarjovanu.workouttimer.R;
import com.vbarjovanu.workouttimer.business.models.userprofiles.UserProfile;
import com.vbarjovanu.workouttimer.business.services.userprofiles.IUserProfilesService;
import com.vbarjovanu.workouttimer.business.services.workouts.IWorkoutsService;
import com.vbarjovanu.workouttimer.session.IApplicationSession;
import com.vbarjovanu.workouttimer.ui.userprofiles.images.IUserProfilesImagesService;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.concurrent.CountDownLatch;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class HomeViewModelTest {
    private CountDownLatch countDownLatch;
    @Rule
    public InstantTaskExecutorRule rule = new InstantTaskExecutorRule();
    @Mock
    private IUserProfilesService userProfilesService;
    @Mock
    private IApplicationSession applicationSession;

    private IHomeViewModel homeViewModel;
    @Mock
    private IWorkoutsService workoutsService;
    @Mock
    private IUserProfilesImagesService userProfilesImagesService;

    private Observer<HomeModel> observer;

    private Application application;

    @Before
    public void setUp() {
        application = (Application) InstrumentationRegistry.getInstrumentation().getTargetContext().getApplicationContext();
        applicationSession = mock(IApplicationSession.class);
        workoutsService = mock(IWorkoutsService.class);
        userProfilesService = mock(IUserProfilesService.class);
        userProfilesImagesService = mock(IUserProfilesImagesService.class);

        this.homeViewModel = new HomeViewModel(application, applicationSession, workoutsService, userProfilesService, userProfilesImagesService);
        this.countDownLatch = new CountDownLatch(1);
        this.homeViewModel.setCountDownLatch(this.countDownLatch);
        //noinspection unchecked
        this.observer = mock(Observer.class);
        this.homeViewModel.getHomeModel().observeForever(this.observer);
    }

    @Test
    public void getHomeModel() {
        LiveData<HomeModel> homeModel;
        homeModel = this.homeViewModel.getHomeModel();
        assertNotNull(homeModel);
        assertNull(homeModel.getValue());
    }

    @Test
    public void loadDataWhenNoUserProfileInSession() throws InterruptedException {
        HomeModel homeModel;
        ArgumentCaptor<HomeModel> captor = ArgumentCaptor.forClass(HomeModel.class);
        when(this.applicationSession.getUserProfileId()).thenReturn(null);
        this.homeViewModel.loadData();
        this.countDownLatch.await();
        verify(this.observer, Mockito.times(1)).onChanged(captor.capture());
        homeModel = captor.getValue();
        assertNotNull(homeModel);
        assertEquals(0, homeModel.getWorkoutsCount());
        assertEquals(0, homeModel.getSequencesCount());
        assertEquals(this.application.getResources().getString(R.string.message_home_fragment_no_workouts), homeModel.getWorkoutsCountText());
        assertEquals(this.application.getResources().getString(R.string.message_home_fragment_no_sequences), homeModel.getSequencesCountText());
    }

    @Test
    public void loadDataWhenValidUserProfileInSession() throws InterruptedException {
        HomeModel homeModel;
        ArgumentCaptor<HomeModel> captor = ArgumentCaptor.forClass(HomeModel.class);
        when(this.applicationSession.getUserProfileId()).thenReturn("123");
        when(this.userProfilesService.loadModel("123")).thenReturn(new UserProfile("123"));
        when(this.workoutsService.getWorkoutsCount("123")).thenReturn(2);

        this.homeViewModel.loadData();
        this.countDownLatch.await();
        verify(this.observer, Mockito.times(1)).onChanged(captor.capture());
        homeModel = captor.getValue();
        assertNotNull(homeModel);
        assertEquals(2, homeModel.getWorkoutsCount());
        assertEquals(0, homeModel.getSequencesCount());
        assertEquals(this.application.getResources().getString(R.string.message_home_fragment_workouts, 2), homeModel.getWorkoutsCountText());
        assertEquals(this.application.getResources().getString(R.string.message_home_fragment_no_sequences), homeModel.getSequencesCountText());
    }
}