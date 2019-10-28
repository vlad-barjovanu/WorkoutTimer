package com.vbarjovanu.workouttimer.ui.workouts.training;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.vbarjovanu.workouttimer.IMainActivityViewModel;
import com.vbarjovanu.workouttimer.MainActivityActionData;
import com.vbarjovanu.workouttimer.R;
import com.vbarjovanu.workouttimer.databinding.FragmentWorkoutTrainingBinding;
import com.vbarjovanu.workouttimer.ui.generic.events.EventContent;
import com.vbarjovanu.workouttimer.ui.generic.viewmodels.CustomViewModelFactory;
import com.vbarjovanu.workouttimer.ui.workouts.edit.WorkoutEditFragmentAction;
import com.vbarjovanu.workouttimer.ui.workouts.training.models.WorkoutTrainingModel;

public class WorkoutTrainingFragment extends Fragment implements WorkoutTrainingFragmentClickListners {

    private IWorkoutTrainingViewModel workoutEditViewModel;
    private IMainActivityViewModel mainActivityViewModel;
    private FragmentWorkoutTrainingBinding binding;
    private Observer<? super WorkoutTrainingModel> workoutObserver;
    private Observer<? super WorkoutEditFragmentAction> actionObserver;
    private Observer<? super EventContent<MainActivityActionData>> mainActivityActionObserver;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = null;

        if (this.getActivity() != null) {
            this.workoutEditViewModel = ViewModelProviders.of(this, CustomViewModelFactory.getInstance(this.getActivity().getApplication())).get(IWorkoutTrainingViewModel.class);
            this.addWorkoutTrainingModelObserver();
//            this.addViewModelActionObserver();
            this.mainActivityViewModel = ViewModelProviders.of(this.getActivity(), CustomViewModelFactory.getInstance(this.getActivity().getApplication())).get(IMainActivityViewModel.class);
            this.mainActivityViewModel.showNewEntityButton(false);
            this.mainActivityViewModel.showSaveEntityButton(false);
            this.addMainActivityViewModelActionObserver();

            root = inflater.inflate(R.layout.fragment_workout_training, container, false);
            this.binding = FragmentWorkoutTrainingBinding.bind(root);
            this.binding.setClickListners(this);

            this.loadWorkout();
        }
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        this.workoutEditViewModel.stopWorkoutTraining();
        this.workoutEditViewModel.close();
    }

    private void loadWorkout() {
        if (this.getArguments() != null && this.getArguments().containsKey("workoutId")) {
            String workoutId = this.getArguments().getString("workoutId");
            if (workoutId != null) {
                this.workoutEditViewModel.loadWorkout(workoutId);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.removeWorkoutTrainingModelObserver();
//        this.removeViewModelActionObserver();
        this.removeMainActivityViewModelActionObserver();
    }

    private void removeWorkoutTrainingModelObserver() {
        if (this.workoutEditViewModel != null && this.workoutObserver != null) {
            this.workoutEditViewModel.getWorkoutTrainingModel().removeObserver(this.workoutObserver);
            this.workoutObserver = null;
        }
    }

    private void addWorkoutTrainingModelObserver() {
        this.workoutObserver = (Observer<WorkoutTrainingModel>) WorkoutTrainingFragment.this::onWorkoutChanged;
        this.workoutEditViewModel.getWorkoutTrainingModel().observe(this, this.workoutObserver);
    }

    private void onWorkoutChanged(WorkoutTrainingModel workoutTrainingModel) {
        this.binding.setWorkoutTrainingModel(workoutTrainingModel);
    }

//
//    private void removeViewModelActionObserver() {
//        if (this.workoutEditViewModel != null && this.actionObserver != null) {
//            this.workoutEditViewModel.getAction().removeObserver(this.actionObserver);
//            this.actionObserver = null;
//        }
//    }

//    private void addViewModelActionObserver() {
//        this.actionObserver = new Observer<WorkoutEditFragmentAction>() {
//            @Override
//            public void onChanged(WorkoutEditFragmentAction workoutEditFragmentAction) {
//                onActionChanged(workoutEditFragmentAction);
//            }
//        };
//        this.workoutEditViewModel.getAction().observe(this, this.actionObserver);
//    }

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
    public void onPauseClick(View view) {
        this.workoutEditViewModel.pauseWorkoutTraining();
    }

    @Override
    public void onStartClick(View view) {
        this.workoutEditViewModel.startWorkoutTraining();
    }

    @Override
    public void onNextWorkoutItemClick(View view) {
        this.workoutEditViewModel.nextWorkoutTrainingItem();
    }

    @Override
    public void onPreviousWorkoutItemClick(View view) {
        this.workoutEditViewModel.previousWorkoutTrainingItem();
    }
}
