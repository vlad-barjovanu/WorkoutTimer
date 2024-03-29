package com.vbarjovanu.workouttimer.ui.workouts.training;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import com.vbarjovanu.workouttimer.ui.generic.recyclerview.RecyclerViewItemActionData;
import com.vbarjovanu.workouttimer.ui.generic.viewmodels.CustomViewModelFactory;
import com.vbarjovanu.workouttimer.ui.workouts.training.actions.DurationChangeActionData;
import com.vbarjovanu.workouttimer.ui.workouts.training.actions.WorkoutTrainingActionData;
import com.vbarjovanu.workouttimer.ui.workouts.training.models.WorkoutTrainingItemModelsList;
import com.vbarjovanu.workouttimer.ui.workouts.training.models.WorkoutTrainingModel;

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
            workoutItemsAdapter.getItemAction().observe(this, this::onWorkoutItemsRecyclerViewItemActionDataChanged);
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
                this.binding.getRecyclerViewAdapter().getItemAction().removeObserver(this::onWorkoutItemsRecyclerViewItemActionDataChanged);
                this.binding.setRecyclerViewAdapter(null);
            }
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
        this.scrollWorkoutTrainingItemIntoView(workoutTrainingModel);
    }

    /**
     * Scrolls the current workout training item into visible area of recycler view
     *
     * @param workoutTrainingModel workout training model
     */
    private void scrollWorkoutTrainingItemIntoView(WorkoutTrainingModel workoutTrainingModel) {
        workoutTrainingModel.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                if (propertyId == com.vbarjovanu.workouttimer.BR.currentWorkoutTrainingItem) {
                    if (WorkoutTrainingFragment.this.getActivity() != null) {
                        WorkoutTrainingFragment.this.getActivity().runOnUiThread(() -> {
                            LinearLayoutManager layoutManager;
                            layoutManager = ((LinearLayoutManager) WorkoutTrainingFragment.this.binding.getLayoutManager());
                            if (layoutManager != null) {
                                layoutManager.scrollToPositionWithOffset(workoutTrainingModel.getCurrentWorkoutTrainingItem().getTotalIndex(), 0);
                                //TODO: add selection support to recyclerview
                            }
                        });
                    }
                }
            }
        });
    }

    @SuppressWarnings("ConstantConditions")
    private <T extends WorkoutTrainingActionData> void onActionChanged(T actionData) {
        DurationChangeActionData durationChangeActionData;
        Integer soundId = null;
        if (actionData != null) {
            if (actionData instanceof DurationChangeActionData) {
                durationChangeActionData = (DurationChangeActionData) actionData;
                switch (durationChangeActionData.getAction()) {
                    case MARK_START_WORK:
                        soundId = R.raw.start_work_sound;
                        break;
                    case MARK_START_REST:
                        soundId = R.raw.start_rest_sound;
                        break;
                    case MARK_DURATION_CHANGE:
                        soundId = R.raw.duration_sound;
                        break;
                }

                if (soundId != null && durationChangeActionData.isPlaySound()) {
                    this.playSound(soundId);
                }
                if (durationChangeActionData.isVibrate()) {
                    VibrationHelper.vibrate(this.getContext(), 300);
                }
            }
        }
    }

    private void playSound(int soundId) {
        MediaPlayer mediaPlayer = MediaPlayer.create(this.getContext(), soundId);
        mediaPlayer.start();
        //noinspection Convert2MethodRef
        mediaPlayer.setOnCompletionListener(mp -> mp.release());
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

    private void onWorkoutItemsRecyclerViewItemActionDataChanged(RecyclerViewItemActionData<WorkoutItemsRecyclerViewItemAction> itemActionData) {
        if (itemActionData.getAction() == WorkoutItemsRecyclerViewItemAction.WORKOUT_ITEM_SELECT) {
            this.viewModel.gotoWorkoutTrainingItem(Integer.parseInt(itemActionData.getId()));
        }
    }

}
