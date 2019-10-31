package com.vbarjovanu.workouttimer.ui.workouts.training;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.vbarjovanu.workouttimer.IMainActivityViewModel;
import com.vbarjovanu.workouttimer.MainActivityActionData;
import com.vbarjovanu.workouttimer.R;
import com.vbarjovanu.workouttimer.databinding.FragmentWorkoutTrainingBinding;
import com.vbarjovanu.workouttimer.helpers.vibration.VibrationHelper;
import com.vbarjovanu.workouttimer.ui.generic.events.EventContent;
import com.vbarjovanu.workouttimer.ui.generic.viewmodels.CustomViewModelFactory;
import com.vbarjovanu.workouttimer.ui.workouts.training.actions.DurationChangeActionData;
import com.vbarjovanu.workouttimer.ui.workouts.training.actions.WorkoutTrainingActionData;
import com.vbarjovanu.workouttimer.ui.workouts.training.models.WorkoutTrainingModel;

public class WorkoutTrainingFragment extends Fragment implements WorkoutTrainingFragmentClickListners {

    private IWorkoutTrainingViewModel viewModel;
    private IMainActivityViewModel mainActivityViewModel;
    private FragmentWorkoutTrainingBinding binding;
    private Observer<? super WorkoutTrainingModel> workoutObserver;
    private Observer<? super WorkoutTrainingActionData> actionObserver;
    private Observer<? super EventContent<MainActivityActionData>> mainActivityActionObserver;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = null;

        if (this.getActivity() != null) {
            this.viewModel = ViewModelProviders.of(this, CustomViewModelFactory.getInstance(this.getActivity().getApplication())).get(IWorkoutTrainingViewModel.class);
            this.addWorkoutTrainingModelObserver();
            this.addViewModelActionObserver();
            this.mainActivityViewModel = ViewModelProviders.of(this.getActivity(), CustomViewModelFactory.getInstance(this.getActivity().getApplication())).get(IMainActivityViewModel.class);
            this.mainActivityViewModel.showNewEntityButton(false);
            this.mainActivityViewModel.showSaveEntityButton(false);
            this.addMainActivityViewModelActionObserver();

            root = inflater.inflate(R.layout.fragment_workout_training, container, false);
            this.binding = FragmentWorkoutTrainingBinding.bind(root);
            this.binding.setClickListners(this);
            this.binding.setWorkoutTrainingItemColorProvider(this.viewModel.getWorkoutTrainingItemColorProvider());
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
        this.removeWorkoutTrainingModelObserver();
        this.removeViewModelActionObserver();
        this.removeMainActivityViewModelActionObserver();
    }

    private void removeWorkoutTrainingModelObserver() {
        if (this.viewModel != null && this.workoutObserver != null) {
            this.viewModel.getWorkoutTrainingModel().removeObserver(this.workoutObserver);
            this.workoutObserver = null;
        }
    }

    private void addWorkoutTrainingModelObserver() {
        this.workoutObserver = (Observer<WorkoutTrainingModel>) WorkoutTrainingFragment.this::onWorkoutChanged;
        this.viewModel.getWorkoutTrainingModel().observe(this, this.workoutObserver);
    }

    private void onWorkoutChanged(WorkoutTrainingModel workoutTrainingModel) {
        this.binding.setWorkoutTrainingModel(workoutTrainingModel);
        //when it's restored from saved instance, .inTraining property is true so that's why the timer won't start
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

    private void removeViewModelActionObserver() {
        if (this.viewModel != null && this.actionObserver != null) {
            this.viewModel.getAction().removeObserver(this.actionObserver);
            this.actionObserver = null;
        }
    }

    private void addViewModelActionObserver() {
        this.actionObserver = (Observer<WorkoutTrainingActionData>) this::onActionChanged;
        this.viewModel.getAction().observe(this, this.actionObserver);
    }

    private void removeMainActivityViewModelActionObserver() {
        if (this.mainActivityViewModel != null && this.mainActivityActionObserver != null) {
            this.mainActivityViewModel.getAction().removeObserver(this.mainActivityActionObserver);
            this.mainActivityActionObserver = null;
        }
    }

    private void addMainActivityViewModelActionObserver() {
        this.mainActivityActionObserver = (Observer<EventContent<MainActivityActionData>>) this::onMainActivityActionChanged;
        this.mainActivityViewModel.getAction().observe(this, this.mainActivityActionObserver);
    }

    private void onMainActivityActionChanged(@NonNull EventContent<MainActivityActionData> eventContent) {
//        MainActivityActionData mainActivityActionData = eventContent.getContent();
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
}
