package com.vbarjovanu.workouttimer.ui.userprofiles.list;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.vbarjovanu.workouttimer.R;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.vbarjovanu.workouttimer.business.models.userprofiles.UserProfile;
import com.vbarjovanu.workouttimer.business.models.userprofiles.UserProfilesList;

import java.io.File;

public class UserProfilesAdapter extends RecyclerView.Adapter<UserProfilesAdapter.MyViewHolder> {
    private final UserProfilesList userProfilesList;

    static class MyViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView textViewTitle;
        TextView textViewDescription;
        ImageView imageView;

        MyViewHolder(@NonNull View itemView) {
            super(itemView);
            this.cardView = itemView.findViewById(R.id.recyclerview_cardView);
            this.textViewTitle = itemView.findViewById(R.id.recyclerview_cardView_title);
            this.textViewDescription = itemView.findViewById(R.id.recyclerview_cardView_description);
            this.imageView = itemView.findViewById(R.id.recyclerview_cardView_imageView);
        }
    }

    UserProfilesAdapter(UserProfilesList userProfilesList) {
        this.userProfilesList = userProfilesList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recyclerview_cardview, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        UserProfile userProfile = this.userProfilesList.get(position);
        String title = "";
        String description = "";
        Integer resourceId = R.drawable.userprofile;

        if (userProfile != null) {
            title = (userProfile.getName() != null) ? userProfile.getName() : "";
            description = (userProfile.getDescription() != null) ? userProfile.getDescription() : "";
            if (userProfile.getImageFilePath() != null) {
                File imgFile = new File(userProfile.getImageFilePath());
                if (imgFile.exists()) {
                    Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                    holder.imageView.setImageBitmap(myBitmap);
                    resourceId = null;
                }
            }
        }
        holder.textViewTitle.setText(title);
        holder.textViewDescription.setText(description);
        if (resourceId != null) {
            holder.imageView.setImageResource(resourceId);
        }
    }

    @Override
    public int getItemCount() {
        return this.userProfilesList.size();
    }
}
