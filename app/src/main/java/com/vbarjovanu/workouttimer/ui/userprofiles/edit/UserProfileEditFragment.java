package com.vbarjovanu.workouttimer.ui.userprofiles.edit;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;

import com.vbarjovanu.workouttimer.R;
import com.vbarjovanu.workouttimer.business.models.userprofiles.UserProfile;
import com.vbarjovanu.workouttimer.ui.generic.viewmodels.CustomViewModelFactory;

import java.io.File;
import java.util.Objects;

public class UserProfileEditFragment extends Fragment implements Observer<UserProfile>, View.OnClickListener {
    private IUserProfileEditViewModel userProfileEditViewModel;
    private EditText editTextDescription;
    private EditText editTextName;
    private ImageView imageView;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private Button buttonCancel;
    private Button buttonSave;
    private Bitmap newImageBitmap;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        Objects.requireNonNull(this.getActivity(), "Activity must be not null");
        this.userProfileEditViewModel = ViewModelProviders.of(this, CustomViewModelFactory.getInstance(this.getActivity().getApplication())).get(IUserProfileEditViewModel.class);
        this.userProfileEditViewModel.getUserProfile().observe(this, this);
        this.userProfileEditViewModel.getAction().observe(this, new Observer<UserProfileEditFragmentAction>() {
            @Override
            public void onChanged(UserProfileEditFragmentAction userProfileEditFragmentAction) {
                NavController navController = Navigation.findNavController(UserProfileEditFragment.this.getActivity(), R.id.nav_host_fragment);
                //noinspection SwitchStatementWithTooFewBranches
                switch (userProfileEditFragmentAction) {
                    case GOTO_USERPROFILES:
                        navController.popBackStack(R.id.nav_userprofiles, false);
                        break;
                }
            }
        });
        View root = inflater.inflate(R.layout.fragment_userprofile_edit, container, false);
        this.editTextName = root.findViewById(R.id.fragment_userprofile_edit_name);
        this.editTextDescription = root.findViewById(R.id.fragment_userprofile_edit_description);
        this.imageView = root.findViewById(R.id.fragment_userprofile_edit_image);
        this.buttonSave = root.findViewById(R.id.fragment_userprofile_edit_save);
        this.buttonCancel = root.findViewById(R.id.fragment_userprofile_edit_cancel);
        this.imageView.setOnClickListener(this);
        this.buttonSave.setOnClickListener(this);
        this.buttonCancel.setOnClickListener(this);
        if (this.getArguments() != null && this.getArguments().containsKey("userProfileId")) {
            String userProfileId = this.getArguments().getString("userProfileId");
            this.userProfileEditViewModel.loadUserProfile(userProfileId);
        }
        return root;
    }

    @Override
    public void onChanged(UserProfile userProfile) {
        String name = "";
        String description = "";
        Integer resourceId = R.drawable.userprofile;

        if (userProfile != null) {
            name = userProfile.getName();
            description = userProfile.getDescription();
            if (userProfile.getImageFilePath() != null) {
                File imgFile = new File(userProfile.getImageFilePath());
                if (imgFile.exists()) {
                    Bitmap imageBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                    this.imageView.setImageBitmap(imageBitmap);
                    resourceId = null;
                }

            }
        }
        this.editTextName.setText(name);
        this.editTextDescription.setText(description);
        if (resourceId != null) {
            this.imageView.setImageResource(resourceId);
        }
    }

    @Override
    public void onClick(View view) {
        if (view.equals(this.imageView)) {
            this.dispatchTakePictureIntent();
        }
        if (view.equals(this.buttonSave)) {
            String name = this.editTextName.getText().toString();
            String description = this.editTextDescription.getText().toString();
            this.userProfileEditViewModel.saveUserProfile(name, description, this.newImageBitmap);
        }
        if (view.equals(this.buttonCancel)) {
            this.userProfileEditViewModel.cancelUserProfileEdit();
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (this.getActivity() != null && takePictureIntent.resolveActivity(this.getActivity().getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK && data != null) {
            Bundle extras = data.getExtras();
            if (extras != null) {
                this.newImageBitmap = (Bitmap) extras.get("data");
                this.imageView.setImageBitmap(this.newImageBitmap);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
