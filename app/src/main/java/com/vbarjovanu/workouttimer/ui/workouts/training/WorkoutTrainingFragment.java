package com.vbarjovanu.workouttimer.ui.workouts.training;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.Observable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.vbarjovanu.workouttimer.IMainActivityViewModel;
import com.vbarjovanu.workouttimer.R;
import com.vbarjovanu.workouttimer.databinding.FragmentWorkoutTrainingBinding;
import com.vbarjovanu.workouttimer.helpers.vibration.VibrationHelper;
import com.vbarjovanu.workouttimer.ui.generic.mediaplayer.MediaPlayerQueue;
import com.vbarjovanu.workouttimer.ui.generic.recyclerview.RecyclerViewItemActionData;
import com.vbarjovanu.workouttimer.ui.generic.viewmodels.CustomViewModelFactory;
import com.vbarjovanu.workouttimer.ui.workouts.training.actions.DurationChangeActionData;
import com.vbarjovanu.workouttimer.ui.workouts.training.actions.WorkoutTrainingActionData;
import com.vbarjovanu.workouttimer.ui.workouts.training.models.WorkoutTrainingItemModelsList;
import com.vbarjovanu.workouttimer.ui.workouts.training.models.WorkoutTrainingModel;

import java.util.ArrayList;
import java.util.List;

public class WorkoutTrainingFragment extends Fragment implements WorkoutTrainingFragmentClickListners {

    private IWorkoutTrainingViewModel viewModel;
    private IMainActivityViewModel mainActivityViewModel;
    private FragmentWorkoutTrainingBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = null;
        WorkoutItemsRecyclerViewAdapter workoutItemsAdapter;

        if (this.getActivity() != null) {
            this.viewModel = ViewModelProviders.of(this, CustomViewModelFactory.getInstance(this.getActivity().getApplication())).get(IWorkoutTrainingViewModel.class);
            this.viewModel.getWorkoutTrainingModel().observe(this, this::onWorkoutChanged);
            this.viewModel.getAction().observe(this, this::onActionChanged);
            this.mainActivityViewModel = ViewModelProviders.of(this.getActivity(), CustomViewModelFactory.getInstance(this.getActivity().getApplication())).get(IMainActivityViewModel.class);
            this.mainActivityViewModel.showNewEntityButton(false);
            this.mainActivityViewModel.showSaveEntityButton(false);

            root = inflater.inflate(R.layout.fragment_workout_training, container, false);
            this.binding = FragmentWorkoutTrainingBinding.bind(root);
            this.binding.setClickListners(this);
            this.binding.setWorkoutTrainingItemColorProvider(this.viewModel.getWorkoutTrainingItemColorProvider());
            workoutItemsAdapter = new WorkoutItemsRecyclerViewAdapter(new WorkoutTrainingItemModelsList());
            workoutItemsAdapter.getItemAction().observe(this, this::onRecyclerViewItemAction);
            this.binding.setRecyclerViewAdapter(workoutItemsAdapter);
            this.binding.setLayoutManager(new LinearLayoutManager(getContext()));
        }
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        String workoutId = null;
        if (this.getArguments() != null) {
            workoutId = this.getArguments().getString("workoutId", null);
        }
        this.loadWorkout(workoutId, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (this.getActivity() != null && !this.getActivity().isChangingConfigurations()) {
            //if it's changing configurations don't stop the training
            this.viewModel.stopWorkoutTraining();
            this.viewModel.close();

            if (this.binding.getRecyclerViewAdapter() != null) {
                this.binding.getRecyclerViewAdapter().getItemAction().removeObserver(this::onRecyclerViewItemAction);
                this.binding.setRecyclerViewAdapter(null);
            }
            this.keepScreenOn(false);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (this.viewModel.isInitialised()) {
            outState.putSerializable("workoutTrainingModel", this.viewModel.getWorkoutTrainingModel().getValue());
        }
    }

    private WorkoutTrainingModel getSavedWorkoutTrainingModel(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null && savedInstanceState.containsKey("workoutTrainingModel")) {
            return (WorkoutTrainingModel) savedInstanceState.getSerializable("workoutTrainingModel");
        }
        return null;
    }

    private void loadWorkout(String workoutId, @Nullable Bundle savedInstanceState) {
        if (workoutId != null) {
            this.viewModel.loadWorkout(workoutId, this.getSavedWorkoutTrainingModel(savedInstanceState));
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (this.viewModel != null) {
            this.viewModel.getWorkoutTrainingModel().removeObserver(this::onWorkoutChanged);
            this.viewModel.getAction().removeObserver(this::onActionChanged);
        }
    }

    private void onWorkoutChanged(WorkoutTrainingModel workoutTrainingModel) {
        this.binding.setWorkoutTrainingModel(workoutTrainingModel);
        this.observerWorkoutTrainingModelPropertiesChanges(workoutTrainingModel);
    }

    /**
     * Scrolls the current workout training item into visible area of recycler view
     *
     * @param workoutTrainingModel workout training model
     */
    private void observerWorkoutTrainingModelPropertiesChanges(WorkoutTrainingModel workoutTrainingModel) {
        workoutTrainingModel.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                if (WorkoutTrainingFragment.this.getActivity() != null) {
                    WorkoutTrainingFragment.this.getActivity().runOnUiThread(() -> {
                        switch (propertyId) {
                            case com.vbarjovanu.workouttimer.BR.currentWorkoutTrainingItem:
                                WorkoutTrainingFragment.this.scrollTrainingItemIntoView(workoutTrainingModel);
                                break;
                            case com.vbarjovanu.workouttimer.BR.inTraining:
                                WorkoutTrainingFragment.this.keepScreenOn(workoutTrainingModel.isInTraining());
                                break;
                        }
                    });
                }
            }
        });
    }

    /**
     * If enabled, keeps the screen always on
     *
     * @param screenOn true in order to keep the screen always on. False otherwise
     */
    private void keepScreenOn(boolean screenOn) {
        if (this.getActivity() != null && this.getActivity().getWindow() != null) {
            if (screenOn)
                this.getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            else
                this.getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }

    /**
     * Brings the current training item into the visible area of the recycler view
     *
     * @param workoutTrainingModel workout training model
     */
    private void scrollTrainingItemIntoView(WorkoutTrainingModel workoutTrainingModel) {
        LinearLayoutManager layoutManager;
        layoutManager = ((LinearLayoutManager) WorkoutTrainingFragment.this.binding.getLayoutManager());
        if (layoutManager != null) {
            layoutManager.scrollToPositionWithOffset(workoutTrainingModel.getCurrentWorkoutTrainingItem().getTotalIndex(), 0);
            //TODO: add selection support to recyclerview
        }
    }

    /**
     * Eventhandler of viewmodel's actions
     *
     * @param actionData action's data
     */
    @SuppressWarnings("ConstantConditions")
    private <T extends WorkoutTrainingActionData> void onActionChanged(T actionData) {
        DurationChangeActionData durationChangeActionData;
        List<Integer> soundIds = new ArrayList<>();
        float speed = 1.0f;
        if (actionData != null) {
            if (actionData instanceof DurationChangeActionData) {
                durationChangeActionData = (DurationChangeActionData) actionData;
                switch (durationChangeActionData.getAction()) {
                    case MARK_START_WORK:
                        soundIds.add(R.raw.start_work_sound);
                        break;
                    case MARK_START_REST:
                        soundIds.add(R.raw.start_rest_sound);
                        break;
                    case MARK_DURATION_CHANGE:
                        soundIds.add(R.raw.duration_sound);
                        break;
                    case MARK_TRAINING_COMPLETE:
                        speed = 2.0f;
                        soundIds.add(R.raw.start_rest_sound);
                        soundIds.add(R.raw.start_rest_sound);
                        break;
                }

                if (!soundIds.isEmpty() && durationChangeActionData.isPlaySound()) {
                    this.playSound(soundIds, speed);
                }
                if (durationChangeActionData.isVibrate()) {
                    VibrationHelper.vibrate(this.getContext(), 200);
                }
            }
        }
    }

    /**
     * Plays a certain sound resource
     *
     * @param soundIds array of IDs of the sound resources to be played
     * @param speed the speed at which sound will be played
     */
    private void playSound(List<Integer> soundIds, float speed) {
        MediaPlayerQueue mediaPlayerQueue;
        mediaPlayerQueue = new MediaPlayerQueue(this.getContext());
        mediaPlayerQueue.addSoundResource(soundIds, speed);
        mediaPlayerQueue.play();
    }

    @Override
    public void onSoundClick(View view) {
        this.viewModel.toggleSound();
    }

    @Override
    public void onVibrateClick(View view) {
        this.viewModel.toggleVibrate();
    }

    @Override
    public void onLockClick(View view) {
        this.viewModel.toggleLock();
    }

    @Override
    public void onPauseClick(View view) {
        this.viewModel.pauseWorkoutTraining();
    }

    @Override
    public void onStartClick(View view) {
        this.viewModel.startWorkoutTraining();
    }

    @Override
    public void onNextWorkoutItemClick(View view) {
        this.viewModel.nextWorkoutTrainingItem();
    }

    @Override
    public void onPreviousWorkoutItemClick(View view) {
        this.viewModel.previousWorkoutTrainingItem();
    }

    @Override
    public void onDurationClick(View view) {
        this.viewModel.toggleDisplayRemainingDuration();
    }

    private void onRecyclerViewItemAction(RecyclerViewItemActionData<WorkoutItemsRecyclerViewItemAction> itemActionData) {
        if (itemActionData.getAction() == WorkoutItemsRecyclerViewItemAction.WORKOUT_ITEM_SELECT) {
            this.viewModel.gotoWorkoutTrainingItem(Integer.parseInt(itemActionData.getId()));
        }
    }

}
