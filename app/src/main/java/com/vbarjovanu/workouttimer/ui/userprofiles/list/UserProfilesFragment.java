package com.vbarjovanu.workouttimer.ui.userprofiles.list;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.vbarjovanu.workouttimer.R;
import com.vbarjovanu.workouttimer.business.models.userprofiles.UserProfile;
import com.vbarjovanu.workouttimer.business.models.userprofiles.UserProfilesList;
import com.vbarjovanu.workouttimer.ui.generic.recyclerview.CustomRVItemTouchListener;
import com.vbarjovanu.workouttimer.ui.generic.recyclerview.RecyclerViewItemClickListener;
import com.vbarjovanu.workouttimer.ui.generic.viewmodels.CustomViewModelFactory;

public class UserProfilesFragment extends Fragment implements Observer<UserProfilesList> {

    private UserProfilesAdapter userProfilesAdapter;
    private IUserProfilesViewModel userProfilesViewModel;
    private RecyclerView recyclerView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        userProfilesViewModel = ViewModelProviders.of(this, CustomViewModelFactory.getInstance(this.getActivity().getApplication())).get(IUserProfilesViewModel.class);
        View root = inflater.inflate(R.layout.fragment_userprofiles, container, false);
        final TextView textView = root.findViewById(R.id.text_userprofiles);
        textView.setText("User profiles");
        this.recyclerView = root.findViewById(R.id.recyclerview_userprofiles);
        this.recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this.getContext());
        this.recyclerView.setLayoutManager(layoutManager);
        this.recyclerView.addOnItemTouchListener(new CustomRVItemTouchListener(this.getContext(), recyclerView, new RecyclerViewItemClickListener() {
            @Override
            public void onClick(View view, int position) {
                UserProfilesList userProfilesList = UserProfilesFragment.this.userProfilesViewModel.getUserProfiles().getValue();
                if (userProfilesList != null) {
                    UserProfile userProfile = userProfilesList.get(position);
                    UserProfilesFragment.this.userProfilesViewModel.setSelectedUserProfileId(userProfile.getId());
                }
            }

            @Override
            public void onLongClick(View view, int position) {
                UserProfilesList userProfilesList = UserProfilesFragment.this.userProfilesViewModel.getUserProfiles().getValue();
                if (userProfilesList != null) {
                    UserProfile userProfile = userProfilesList.get(position);
                    UserProfilesFragment.this.userProfilesViewModel.editUserProfileId(userProfile.getId());
                }
            }
        }));

        userProfilesViewModel.getUserProfiles().observe(this, this);
        userProfilesViewModel.getActionData().observe(this, new Observer<UserProfilesFragmentActionData>() {
            @Override
            public void onChanged(UserProfilesFragmentActionData userProfilesFragmentActionData) {
                NavController navController = Navigation.findNavController(UserProfilesFragment.this.getActivity(), R.id.nav_host_fragment);
                switch (userProfilesFragmentActionData.getAction()) {
                    case GOTO_HOME:
                        navController.popBackStack(R.id.nav_home, false);
                        break;
                    case GOTO_USERPROFILE_EDIT:
                        Bundle args = new Bundle(1);
                        args.putString("userProfileId", userProfilesFragmentActionData.getUserProfileId());
                        navController.navigate(R.id.action_nav_userprofiles_to_nav_userprofile_edit, args);
                        break;
                }
            }
        });
        userProfilesViewModel.loadUserProfiles();
        return root;
    }

    @Override
    public void onChanged(UserProfilesList userProfiles) {
        this.userProfilesAdapter = new UserProfilesAdapter(userProfiles);
        this.recyclerView.setAdapter(userProfilesAdapter);
    }
}