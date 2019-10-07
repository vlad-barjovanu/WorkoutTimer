package com.vbarjovanu.workouttimer.ui.userprofiles.list;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import com.vbarjovanu.workouttimer.IMainActivityViewModel;
import com.vbarjovanu.workouttimer.MainActivityActionData;
import com.vbarjovanu.workouttimer.R;
import com.vbarjovanu.workouttimer.business.models.userprofiles.UserProfilesList;
import com.vbarjovanu.workouttimer.ui.generic.events.EventContent;
import com.vbarjovanu.workouttimer.ui.generic.recyclerview.RecyclerViewItemActionData;
import com.vbarjovanu.workouttimer.ui.generic.viewmodels.CustomViewModelFactory;
import com.vbarjovanu.workouttimer.ui.userprofiles.images.UserProfilesImagesService;

import java.util.Objects;

public class UserProfilesFragment extends Fragment {

    private IUserProfilesViewModel userProfilesViewModel;
    private IMainActivityViewModel mainActivityViewModel;
    private RecyclerView recyclerView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        Objects.requireNonNull(this.getActivity(), "Activity must not be null");

        View root = inflater.inflate(R.layout.fragment_userprofiles, container, false);
        final TextView textView = root.findViewById(R.id.text_userprofiles);
        textView.setText(getString(R.string.menu_userprofiles));
        this.recyclerView = root.findViewById(R.id.recyclerview_userprofiles);
        this.recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this.getContext());
        this.recyclerView.setLayoutManager(layoutManager);

        this.userProfilesViewModel = ViewModelProviders.of(this, CustomViewModelFactory.getInstance(this.getActivity().getApplication())).get(IUserProfilesViewModel.class);
        this.mainActivityViewModel = ViewModelProviders.of(this.getActivity(), CustomViewModelFactory.getInstance(this.getActivity().getApplication())).get(IMainActivityViewModel.class);
        this.userProfilesViewModel.getUserProfiles().observe(this, new Observer<UserProfilesList>() {
            @Override
            public void onChanged(UserProfilesList userProfiles) {
                onUserProfilesChanged(userProfiles);
            }
        });
        this.userProfilesViewModel.getActionData().observe(this, new Observer<UserProfilesFragmentActionData>() {
            @Override
            public void onChanged(UserProfilesFragmentActionData actionData) {
                onUserProfilesFragmentAction(actionData);
            }
        });
        this.mainActivityViewModel.getAction().observe(this, new Observer<EventContent<MainActivityActionData>>() {
            @Override
            public void onChanged(EventContent<MainActivityActionData> eventContent) {
                onMainActivityAction(eventContent);
            }
        });
        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        this.userProfilesViewModel.loadUserProfiles();
        this.mainActivityViewModel.showNewEntityButton(true);
        this.mainActivityViewModel.showSaveEntityButton(false);
    }

    private void onUserProfilesFragmentAction(UserProfilesFragmentActionData actionData) {
        Objects.requireNonNull(this.getActivity(), "Activity must not be null");

        NavController navController = Navigation.findNavController(this.getActivity(), R.id.nav_host_fragment);
        switch (actionData.getAction()) {
            case GOTO_HOME:
                navController.popBackStack(R.id.nav_home, false);
                break;
            case GOTO_USERPROFILE_NEW:
            case GOTO_USERPROFILE_EDIT:
                Bundle args;
                args = new Bundle(1);
                args.putString("userProfileId", actionData.getUserProfileId());
                navController.navigate(R.id.action_nav_userprofiles_to_nav_userprofile_edit, args);
                break;
        }
    }

    private void onUserProfilesRecyclerViewAdapterItemAction(RecyclerViewItemActionData<UserProfilesRecyclerViewItemAction> itemActionData) {
        switch (itemActionData.getAction()) {
            case USERPROFILE_EDIT:
                this.userProfilesViewModel.editUserProfile(itemActionData.getId());
                break;
            case USERPROFILE_SELECT:
                this.userProfilesViewModel.setSelectedUserProfileId(itemActionData.getId());
                break;
            case USERPROFILE_DELETE:
                break;
        }
    }

    private void onUserProfilesChanged(UserProfilesList userProfiles) {
        UserProfilesRecyclerViewAdapter userProfilesRecyclerViewAdapter;
        userProfilesRecyclerViewAdapter = new UserProfilesRecyclerViewAdapter(userProfiles, new UserProfilesImagesService(this.getContext()));
        userProfilesRecyclerViewAdapter.getItemAction().observe(this, new Observer<RecyclerViewItemActionData<UserProfilesRecyclerViewItemAction>>() {
            @Override
            public void onChanged(RecyclerViewItemActionData<UserProfilesRecyclerViewItemAction> itemActionData) {
                onUserProfilesRecyclerViewAdapterItemAction(itemActionData);
            }
        });
        this.recyclerView.setAdapter(userProfilesRecyclerViewAdapter);
    }

    private void onMainActivityAction(EventContent<MainActivityActionData> eventContent) {
        if (eventContent != null && eventContent.getContent() != null) {
            //noinspection SwitchStatementWithTooFewBranches
            switch (eventContent.getContent().getAction()) {
                case NEW_ENTITY_BUTTON_CLICKED:
                    eventContent.setHandled();
                    this.userProfilesViewModel.newUserProfile();
                    break;
            }
        }
    }

}