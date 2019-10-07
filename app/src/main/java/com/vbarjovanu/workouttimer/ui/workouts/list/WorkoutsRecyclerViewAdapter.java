package com.vbarjovanu.workouttimer.ui.workouts.list;

import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

import com.vbarjovanu.workouttimer.BR;
import com.vbarjovanu.workouttimer.R;
import com.vbarjovanu.workouttimer.business.models.workouts.Workout;
import com.vbarjovanu.workouttimer.business.models.workouts.WorkoutsList;
import com.vbarjovanu.workouttimer.ui.generic.recyclerview.ItemModel;
import com.vbarjovanu.workouttimer.ui.generic.recyclerview.RecyclerViewAdapter;

public class WorkoutsRecyclerViewAdapter extends RecyclerViewAdapter<Workout, WorkoutsRecyclerViewItemAction> {

    WorkoutsRecyclerViewAdapter(WorkoutsList workoutsList) {
        super(workoutsList, BR.item);
    }

    @Override
    protected ItemModel createItemModel(Workout model) {
        return new ItemModel(model.getId(), model.getName(), model.getDescription(), model.getColor());
    }

    @Override
    protected void onItemClick(View view, final ItemModel itemModel, RecyclerViewAdapter.ViewHolder holder, int position) {
        switch (view.getId()) {
            case R.id.recyclerview_cardView:
                this.triggerItemAction(WorkoutsRecyclerViewItemAction.WORKOUT_EDIT, itemModel.getId());
                break;
            case R.id.recyclerview_workouts_button_play:
                this.triggerItemAction(WorkoutsRecyclerViewItemAction.WORKOUT_PLAY, itemModel.getId());
                break;
            case R.id.recyclerview_workouts_button_menu:
                PopupMenu popup = new PopupMenu(view.getContext(), view);
                popup.inflate(R.menu.workouts_options_menu);
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        return WorkoutsRecyclerViewAdapter.this.onMenuItemClick(item, itemModel);
                    }
                });
                //displaying the popup
                popup.show();
                break;
        }
    }

    private boolean onMenuItemClick(MenuItem item, ItemModel itemModel) {
        WorkoutsRecyclerViewItemAction action = null;
        switch (item.getItemId()) {
            case R.id.workout_edit:
                action = WorkoutsRecyclerViewItemAction.WORKOUT_EDIT;
                break;
            case R.id.workout_delete:
                action = WorkoutsRecyclerViewItemAction.WORKOUT_DELETE;
                break;
        }
        triggerItemAction(action, itemModel.getId());
        return true;
    }

    @Override
    protected int getLayoutIdForPosition(int position) {
        return R.layout.recyclerview_workouts;
    }
}
