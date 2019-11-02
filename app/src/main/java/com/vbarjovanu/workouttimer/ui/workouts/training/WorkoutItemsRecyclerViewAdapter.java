package com.vbarjovanu.workouttimer.ui.workouts.training;

import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

import com.vbarjovanu.workouttimer.BR;
import com.vbarjovanu.workouttimer.R;
import com.vbarjovanu.workouttimer.business.models.workouts.Workout;
import com.vbarjovanu.workouttimer.business.models.workouts.WorkoutsList;
import com.vbarjovanu.workouttimer.ui.generic.recyclerview.ItemModel;
import com.vbarjovanu.workouttimer.ui.generic.recyclerview.RecyclerViewAdapter;
import com.vbarjovanu.workouttimer.ui.workouts.list.WorkoutsRecyclerViewItemAction;
import com.vbarjovanu.workouttimer.ui.workouts.training.models.WorkoutTrainingItemModel;
import com.vbarjovanu.workouttimer.ui.workouts.training.models.WorkoutTrainingItemModelsList;

import java.util.Locale;

public class WorkoutItemsRecyclerViewAdapter extends RecyclerViewAdapter<WorkoutTrainingItemModel, WorkoutItemsRecyclerViewItemAction> {

    WorkoutItemsRecyclerViewAdapter(WorkoutTrainingItemModelsList workoutTrainingItemModels) {
        super(workoutTrainingItemModels, BR.item);
    }

    @Override
    protected ItemModel createItemModel(WorkoutTrainingItemModel model) {
        String title;
        String description;
        Locale locale;

        locale = Locale.forLanguageTag("en-US");
        if (locale == null) {
            locale = Locale.getDefault();
        }

        title = String.format(locale, "%d. %s", model.getTotalIndex() + 1, model.getType());
        description = String.format(locale, "C%d S%d - %s", model.getCycleIndex(), model.getSetIndex(), model.getDescription());
        return new ItemModel(model.getPrimaryKey(), title, description, android.R.color.transparent);
    }

    @Override
    protected void onItemClick(View view, final ItemModel itemModel, ViewHolder holder, int position) {
        if (view.getId() == R.id.recyclerview_cardView) {
            this.triggerItemAction(WorkoutItemsRecyclerViewItemAction.WORKOUT_ITEM_SELECT, itemModel.getId());
        }
    }

    @Override
    protected int getLayoutIdForPosition(int position) {
        return R.layout.recyclerview_workout_training_items;
    }
}
