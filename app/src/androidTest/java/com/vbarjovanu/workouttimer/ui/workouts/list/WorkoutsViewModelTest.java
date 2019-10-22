package com.vbarjovanu.workouttimer.ui.workouts.list;

import android.hardware.camera2.CameraCaptureSession;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import com.vbarjovanu.workouttimer.business.models.workouts.Workout;
import com.vbarjovanu.workouttimer.business.models.workouts.WorkoutsList;
import com.vbarjovanu.workouttimer.business.services.workouts.IWorkoutsService;
import com.vbarjovanu.workouttimer.ui.generic.events.SingleLiveEvent;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.CountDownLatch;

import static junit.framework.TestCase.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class WorkoutsViewModelTest {

    @Mock
    private IWorkoutsService workoutsService;
    private IWorkoutsViewModel workoutsViewModel;
    private CountDownLatch countDownLatch;
    private Observer<WorkoutsList> workoutsObserver;
    private Observer<WorkoutsFragmentActionData> actionObserver;

    @Rule
    public InstantTaskExecutorRule rule = new InstantTaskExecutorRule();

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Before
    public void setup() {
        this.workoutsService = mock(IWorkoutsService.class);
        this.workoutsViewModel = new WorkoutsViewModel(this.workoutsService);
        this.setupWorkoutsObserver();
        this.setupActionObserver();
        this.setupCountDownLatch();
    }

    private void setupCountDownLatch() {
        this.countDownLatch = new CountDownLatch(1);
        this.workoutsViewModel.setCountDownLatch(this.countDownLatch);
    }

    private void setupWorkoutsObserver() {
        if (this.workoutsObserver != null) {
            this.workoutsViewModel.getWorkouts().removeObserver(this.workoutsObserver);
        }
        //noinspection unchecked
        this.workoutsObserver = mock(Observer.class);
        this.workoutsViewModel.getWorkouts().observeForever(this.workoutsObserver);
    }

    private void setupActionObserver() {
        if (this.actionObserver != null) {
            this.workoutsViewModel.getActionData().removeObserver(this.actionObserver);
        }
        //noinspection unchecked
        this.actionObserver = mock(Observer.class);
        this.workoutsViewModel.getActionData().observeForever(this.actionObserver);
    }

    private void setupWorkoutsService(String profileId) {
        Mockito.when(this.workoutsService.loadModels(anyString())).thenAnswer(new Answer<WorkoutsList>() {
            @Override
            public WorkoutsList answer(InvocationOnMock invocation) {
                WorkoutsList workoutsListMocked = new WorkoutsList();
                String userProfileId = invocation.getArgument(0);
                if (userProfileId != null && userProfileId.equals("profile123")) {
                    workoutsListMocked.add(new Workout("123"));
                    workoutsListMocked.add(new Workout("456"));
                }
                return workoutsListMocked;
            }
        });
        Mockito.when(this.workoutsService.deleteModel(anyString(), anyString())).thenAnswer(new Answer<Boolean>() {
            @Override
            public Boolean answer(InvocationOnMock invocation) {
                String profileId = invocation.getArgument(0);
                String workoutId = invocation.getArgument(1);
                return profileId.equals("profile123") && (workoutId.equals("123") || workoutId.equals("456"));
            }
        });
        Mockito.when(this.workoutsService.deleteModel(anyString(), any(Workout.class))).thenAnswer(new Answer<Boolean>() {
            @Override
            public Boolean answer(InvocationOnMock invocation) {
                String profileId = invocation.getArgument(0);
                Workout workout = invocation.getArgument(1);
                String workoutId = "";
                if (workout != null) {
                    workoutId = workout.getId();
                }
                return profileId.equals("profile123") && (workoutId.equals("123") || workoutId.equals("456"));
            }
        });
    }

    @Test
    public void loadWorkouts() throws InterruptedException {
        /*
         * call loadWorkouts() with a known user profile ID
         * expects: workouts list changes
         * expects: workouts list contains all the workouts for the user profile
         * expects: no action is triggered
         */
        String profileId = "profile123";
        ArgumentCaptor<WorkoutsList> workoutsCaptor = ArgumentCaptor.forClass(WorkoutsList.class);
        ArgumentCaptor<WorkoutsFragmentActionData> actionCaptor = ArgumentCaptor.forClass(WorkoutsFragmentActionData.class);
        ArgumentCaptor<String> profileIdCaptor = ArgumentCaptor.forClass(String.class);

        this.setupWorkoutsService(profileId);
        this.workoutsViewModel.loadWorkouts(profileId);
        countDownLatch.await();
        //check workouts change
        verify(this.workoutsObserver, times(1)).onChanged(workoutsCaptor.capture());
        //assert that expected workouts were loaded
        WorkoutsList workoutsList = workoutsCaptor.getValue();
        assertNotNull(workoutsList);
        assertEquals(2, workoutsList.size());
        assertEquals("123", workoutsList.get(0).getId());
        assertEquals("456", workoutsList.get(1).getId());
        verify(this.actionObserver, times(0)).onChanged(actionCaptor.capture());
        verify(this.workoutsService, times(1)).loadModels(profileIdCaptor.capture());
        assertEquals(profileId, profileIdCaptor.getValue());
    }

    @Test
    public void loadWorkoutsUnknownProfileId() throws InterruptedException {
        /*
         * call loadWorkouts() with an unknown user profile ID
         * expects: workouts list changes
         * expects: workouts list is empty
         * expects: no action is triggered
         */
        String profileId = "profile123";
        String unknownProfileId = "profile12";
        ArgumentCaptor<WorkoutsList> workoutsCaptor = ArgumentCaptor.forClass(WorkoutsList.class);
        ArgumentCaptor<WorkoutsFragmentActionData> actionCaptor = ArgumentCaptor.forClass(WorkoutsFragmentActionData.class);
        ArgumentCaptor<String> profileIdCaptor = ArgumentCaptor.forClass(String.class);

        this.setupWorkoutsService(profileId);
        this.workoutsViewModel.loadWorkouts(unknownProfileId);
        countDownLatch.await();
        //check workouts change
        Mockito.verify(this.workoutsObserver, times(1)).onChanged(workoutsCaptor.capture());
        WorkoutsList workoutsList = workoutsCaptor.getValue();
        Assert.assertNotNull(workoutsList);
        Assert.assertEquals(0, workoutsList.size());
        //check no action triggered
        Mockito.verify(this.actionObserver, times(0)).onChanged(actionCaptor.capture());
        verify(this.workoutsService, times(1)).loadModels(profileIdCaptor.capture());
        assertEquals(unknownProfileId, profileIdCaptor.getValue());
    }

    @Test
    public void getWorkouts() {
        /*
         * by default getWorkouts() returns a LiveData object with no content
         */
        LiveData<WorkoutsList> workoutsLiveData = workoutsViewModel.getWorkouts();
        Assert.assertNotNull(workoutsLiveData);
        Assert.assertNull(workoutsLiveData.getValue());
    }

    @Test
    public void getActionData() {
        /*
         * by default getActionData() returns a SingleLiveEvent object with no content
         */
        SingleLiveEvent<WorkoutsFragmentActionData> actionData = workoutsViewModel.getActionData();
        Assert.assertNotNull(actionData);
        Assert.assertNull(actionData.getValue());
    }

    private void newWorkoutImplementation(String profileId) {
        ArgumentCaptor<WorkoutsList> workoutsCaptor = ArgumentCaptor.forClass(WorkoutsList.class);
        ArgumentCaptor<WorkoutsFragmentActionData> actionCaptor = ArgumentCaptor.forClass(WorkoutsFragmentActionData.class);
        this.workoutsViewModel.newWorkout(profileId);
        //check workouts no change
        verify(this.workoutsObserver, times(0)).onChanged(workoutsCaptor.capture());
        //check action triggered
        verify(this.actionObserver, times(1)).onChanged(actionCaptor.capture());
        WorkoutsFragmentActionData actionData = actionCaptor.getValue();
        assertNotNull(actionData);
        assertNotNull(actionData.getAction());
        assertEquals(WorkoutsFragmentAction.GOTO_WORKOUT_NEW, actionData.getAction());
        assertEquals(profileId, actionData.getProfileId());
        assertNull(actionData.getWorkoutId());
    }

    @Test
    public void newWorkout() {
        /*
         * call newWorkout() method with a known user profile
         * expects: action is triggered GOTO_WORKOUT_NEW
         * expects: workouts list doesn't change
         */

        String profileId = "profile123";
        this.newWorkoutImplementation(profileId);
    }

    @Test
    public void newWorkoutUnknownUserProfile() {
        /*
         * call newWorkout() method with an unknown user profile
         * expects: action is triggered GOTO_WORKOUT_NEW
         * expects: workouts list doesn't change
         */

        String profileId = "profile12";
        this.newWorkoutImplementation(profileId);
    }

    private void editWorkoutImplementation(String profileId, String workoutId, int actionChangesCount) throws InterruptedException {
        ArgumentCaptor<WorkoutsList> workoutsCaptor = ArgumentCaptor.forClass(WorkoutsList.class);
        ArgumentCaptor<WorkoutsFragmentActionData> actionCaptor = ArgumentCaptor.forClass(WorkoutsFragmentActionData.class);
        this.workoutsViewModel.loadWorkouts(profileId);
        countDownLatch.await();
        //check workouts change
        verify(this.workoutsObserver, times(1)).onChanged(workoutsCaptor.capture());
        this.workoutsViewModel.editWorkout(profileId, workoutId);
        //check workouts no change
        verify(this.workoutsObserver, times(1)).onChanged(workoutsCaptor.capture());
        //check action triggered
        verify(this.actionObserver, times(actionChangesCount)).onChanged(actionCaptor.capture());
        if (actionChangesCount > 0) {
            WorkoutsFragmentActionData actionData = actionCaptor.getValue();
            assertNotNull(actionData);
            assertNotNull(actionData.getAction());
            assertEquals(WorkoutsFragmentAction.GOTO_WORKOUT_EDIT, actionData.getAction());
            assertEquals(profileId, actionData.getProfileId());
            assertEquals(workoutId, actionData.getWorkoutId());
        }
    }

    @Test
    public void editWorkout() throws InterruptedException {
        /*
         * call editWorkout() method with a valid user profile and a valid workout ID
         * expects: action is triggered GOTO_WORKOUT_EDIT
         * expects: action data points to the valid user profile and the valid workout ID
         * expects: workouts list doesn't change
         */

        String profileId = "profile123";
        String workoutId = "123";
        this.setupWorkoutsService(profileId);
        this.editWorkoutImplementation(profileId, workoutId, 1);
    }

    @Test
    public void editWorkoutInvalidWorkout() throws InterruptedException {
        /*
         * call editWorkout() method with a valid user profile and an invalid workout ID
         * expects: no action is triggered
         * expects: workouts list doesn't change
         */

        String profileId = "profile123";
        String workoutId = "12";
        this.setupWorkoutsService(profileId);
        this.editWorkoutImplementation(profileId, workoutId, 0);
    }

    @Test
    public void editWorkoutInvalidUserProfile() throws InterruptedException {
        /*
         * call editWorkout() method with an invalid user profile and a valid workout ID
         * expects: no action is triggered
         * expects: workouts list doesn't change
         */

        String validProfileId = "profile123";
        String invalidProfileId = "profile12";
        String workoutId = "123";
        this.setupWorkoutsService(validProfileId);
        this.editWorkoutImplementation(invalidProfileId, workoutId, 0);
    }

    @Test
    public void deleteWorkout() throws InterruptedException {
        /*
         * call editWorkout() method with a valid user profile and a valid workout ID
         * expects: action is triggered GOTO_WORKOUT_EDIT
         * expects: action data points to the valid user profile and the valid workout ID
         * expects: workouts list doesn't change
         */

        String profileId = "profile123";
        String workoutId = "123";
        this.setupWorkoutsService(profileId);
        ArgumentCaptor<WorkoutsList> workoutsCaptor = ArgumentCaptor.forClass(WorkoutsList.class);
        ArgumentCaptor<WorkoutsFragmentActionData> actionCaptor = ArgumentCaptor.forClass(WorkoutsFragmentActionData.class);
        ArgumentCaptor<Workout> workoutCaptor = ArgumentCaptor.forClass(Workout.class);
        ArgumentCaptor<String> profileIdCaptor = ArgumentCaptor.forClass(String.class);

        this.workoutsViewModel.loadWorkouts(profileId);
        countDownLatch.await();
        verify(this.workoutsObserver, times(1)).onChanged(workoutsCaptor.capture());
        this.setupCountDownLatch();
        this.workoutsViewModel.deleteWorkout(profileId, workoutId);
        countDownLatch.await();
        //check workouts changed
        verify(this.workoutsObserver, times(2)).onChanged(workoutsCaptor.capture());
        //check action not triggered
        verify(this.actionObserver, times(0)).onChanged(actionCaptor.capture());
        //check workoutsService.deleteModel was called with correct parameters
        verify(this.workoutsService, times(1)).deleteModel(profileIdCaptor.capture(), workoutCaptor.capture());
        assertEquals(profileId, profileIdCaptor.getValue());
        assertEquals(workoutId, workoutCaptor.getValue().getId());
    }

    @Test
    public void deleteWorkoutInvalidWorkout() throws InterruptedException {
        /*
         * call editWorkout() method with a valid user profile and an invalid workout ID
         * expects: no action is triggered
         * expects: workouts list doesn't change
         */

        String profileId = "profile123";
        String workoutId = "12";
        this.setupWorkoutsService(profileId);
        ArgumentCaptor<WorkoutsList> workoutsCaptor = ArgumentCaptor.forClass(WorkoutsList.class);
        ArgumentCaptor<WorkoutsFragmentActionData> actionCaptor = ArgumentCaptor.forClass(WorkoutsFragmentActionData.class);

        this.workoutsViewModel.loadWorkouts(profileId);
        countDownLatch.await();
        verify(this.workoutsObserver, times(1)).onChanged(workoutsCaptor.capture());
        this.setupCountDownLatch();
        this.workoutsViewModel.deleteWorkout(profileId, workoutId);
        countDownLatch.await();
        //check workouts changed
        verify(this.workoutsObserver, times(1)).onChanged(workoutsCaptor.capture());
        //check action triggered
        verify(this.actionObserver, times(1)).onChanged(actionCaptor.capture());
        assertEquals(WorkoutsFragmentAction.DISPLAY_WORKOUT_DELETE_FAILED, actionCaptor.getValue().getAction());
        assertEquals(profileId, actionCaptor.getValue().getProfileId());
        assertEquals(workoutId, actionCaptor.getValue().getWorkoutId());
        //check workoutsService.deleteModel was not called at all
        verify(this.workoutsService, times(0)).deleteModel(anyString(), any(Workout.class));
    }

    @Test
    public void deleteWorkoutInvalidUserProfile() throws InterruptedException {
        /*
         * call editWorkout() method with an invalid user profile and a valid workout ID
         * expects: no action is triggered
         * expects: workouts list doesn't change
         */

        String validProfileId = "profile123";
        String profileId = "profile12";
        String workoutId = "123";
        this.setupWorkoutsService(validProfileId);
        ArgumentCaptor<WorkoutsList> workoutsCaptor = ArgumentCaptor.forClass(WorkoutsList.class);
        ArgumentCaptor<WorkoutsFragmentActionData> actionCaptor = ArgumentCaptor.forClass(WorkoutsFragmentActionData.class);

        this.workoutsViewModel.loadWorkouts(profileId);
        countDownLatch.await();
        verify(this.workoutsObserver, times(1)).onChanged(workoutsCaptor.capture());
        this.setupCountDownLatch();
        this.workoutsViewModel.deleteWorkout(profileId, workoutId);
        countDownLatch.await();
        //check workouts changed
        verify(this.workoutsObserver, times(1)).onChanged(workoutsCaptor.capture());
        //check action triggered
        verify(this.actionObserver, times(1)).onChanged(actionCaptor.capture());
        assertEquals(WorkoutsFragmentAction.DISPLAY_WORKOUT_DELETE_FAILED, actionCaptor.getValue().getAction());
        assertEquals(profileId, actionCaptor.getValue().getProfileId());
        assertEquals(workoutId, actionCaptor.getValue().getWorkoutId());
        //check workoutsService.deleteModel was not called at all
        verify(this.workoutsService, times(0)).deleteModel(anyString(), any(Workout.class));
    }
}