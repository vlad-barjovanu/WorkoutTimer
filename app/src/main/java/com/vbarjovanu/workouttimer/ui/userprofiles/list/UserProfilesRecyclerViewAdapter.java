package com.vbarjovanu.workouttimer.ui.userprofiles.list;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;

import com.vbarjovanu.workouttimer.BR;
import com.vbarjovanu.workouttimer.R;
import com.vbarjovanu.workouttimer.business.models.userprofiles.UserProfile;
import com.vbarjovanu.workouttimer.business.models.userprofiles.UserProfilesList;
import com.vbarjovanu.workouttimer.ui.generic.recyclerview.ItemModel;
import com.vbarjovanu.workouttimer.ui.generic.recyclerview.RecyclerViewAdapter;
import com.vbarjovanu.workouttimer.ui.workouts.list.WorkoutsRecyclerViewItemAction;

import java.io.File;

public class UserProfilesRecyclerViewAdapter extends RecyclerViewAdapter<UserProfile, UserProfilesRecyclerViewItemAction> {
    private final Bitmap defaultUserImage;

    UserProfilesRecyclerViewAdapter(@NonNull UserProfilesList userProfilesList, Bitmap defaultUserImage) {
        super(userProfilesList, BR.item);
        this.defaultUserImage = defaultUserImage;
    }

    @Override
    protected ItemModel createItemModel(UserProfile model) {
        Bitmap userImage = this.defaultUserImage;

        if (model.getImageFilePath() != null) {
            File imgFile = new File(model.getImageFilePath());
            if (imgFile.exists()) {
                userImage = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            }
        }

        return new UserProfileItemModel(model.getId(), model.getName(), model.getDescription(), android.R.color.white, userImage);
    }

    /**
     * User action click on a recycler view item
     * @param view clicked view of recycler view item layout
     * @param itemModel recycler view item model
     * @param holder recycler view viewholder
     * @param position recycler view clicked item position
     */
    @Override
    protected void onItemClick(View view, final ItemModel itemModel, ViewHolder holder, int position) {
        switch (view.getId()) {
            case R.id.recyclerview_userprofiles_cardView:
                this.triggerItemAction(UserProfilesRecyclerViewItemAction.USERPROFILE_SELECT, itemModel.getId());
                break;
            case R.id.recyclerview_userprofiles_button_menu:
                PopupMenu popup = new PopupMenu(view.getContext(), view);
                popup.inflate(R.menu.userprofiles_options_menu);
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        return UserProfilesRecyclerViewAdapter.this.onMenuItemClick(item, itemModel);
                    }
                });
                //displaying the popup
                popup.show();
                break;
        }
    }

    /**
     * User action click on a menu option of an item
     * @param item menu item
     * @param itemModel recycler view item model
     * @return true if handled
     */
    private boolean onMenuItemClick(MenuItem item, ItemModel itemModel) {
        UserProfilesRecyclerViewItemAction action = null;
        switch (item.getItemId()) {
            case R.id.userprofile_edit:
                action = UserProfilesRecyclerViewItemAction.USERPROFILE_EDIT;
                break;
            case R.id.userprofile_select:
                action = UserProfilesRecyclerViewItemAction.USERPROFILE_SELECT;
                break;
            case R.id.userprofile_delete:
                action = UserProfilesRecyclerViewItemAction.USERPROFILE_DELETE;
                break;
        }
        triggerItemAction(action, itemModel.getId());
        return true;
    }


        @Override
    protected int getLayoutIdForPosition(int position) {
        return R.layout.recyclerview_userprofiles;
    }
}
