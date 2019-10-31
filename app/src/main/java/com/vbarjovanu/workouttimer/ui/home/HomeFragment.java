package com.vbarjovanu.workouttimer.ui.home;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.vbarjovanu.workouttimer.IMainActivityViewModel;
import com.vbarjovanu.workouttimer.R;
import com.vbarjovanu.workouttimer.databinding.FragmentHomeBinding;
import com.vbarjovanu.workouttimer.ui.generic.viewmodels.CustomViewModelFactory;
import com.vbarjovanu.workouttimer.ui.workouts.list.WorkoutsFragment;

import java.util.Objects;

public class HomeFragment extends Fragment implements IHomeFragmentClickListeners {

    private IHomeViewModel homeViewModel;
    private IMainActivityViewModel mainActivityViewModel;
    private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        Objects.requireNonNull(this.getActivity(), "Activity must be not null");
        this.homeViewModel = ViewModelProviders.of(this, CustomViewModelFactory.getInstance(this.getActivity().getApplication())).get(IHomeViewModel.class);
        this.mainActivityViewModel = ViewModelProviders.of(this.getActivity(), CustomViewModelFactory.getInstance(this.getActivity().getApplication())).get(IMainActivityViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        this.binding = FragmentHomeBinding.bind(root);
        this.binding.setClickListners(this);
        homeViewModel.getHomeModel().observe(this, HomeFragment.this::onHomeModelChanged);
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.mainActivityViewModel.showSaveEntityButton(false);
        this.mainActivityViewModel.showNewEntityButton(false);
        this.homeViewModel.loadData();
    }

    private void onHomeModelChanged(HomeModel homeModel) {
        this.binding.setHomeModel(homeModel);
    }

    @Override
    public void onWorkoutsClick(View view) {
        Objects.requireNonNull(HomeFragment.this.getActivity(), "Activity must be not null");
        NavController navController = Navigation.findNavController(HomeFragment.this.getActivity(), R.id.nav_host_fragment);
        navController.navigate(R.id.action_nav_home_to_nav_workouts);
    }

    @Override
    public void onSequencesClick(View view) {
        //TODO implement navigation to sequences
        Toast.makeText(getContext(), "Go to sequences", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onWelcomeTextClick(View view) {
        Objects.requireNonNull(HomeFragment.this.getActivity(), "Activity must be not null");
        NavController navController = Navigation.findNavController(HomeFragment.this.getActivity(), R.id.nav_host_fragment);
        navController.navigate(R.id.action_nav_home_to_nav_userprofiles);
    }
}