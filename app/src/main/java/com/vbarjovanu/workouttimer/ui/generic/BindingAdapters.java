package com.vbarjovanu.workouttimer.ui.generic;

import androidx.databinding.BindingAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.vbarjovanu.workouttimer.business.models.generic.ModelsList;
import com.vbarjovanu.workouttimer.ui.generic.recyclerview.RecyclerViewAdapter;

public class BindingAdapters {
    @BindingAdapter("data")
    public static void setRecyclerViewProperties(RecyclerView recyclerView, ModelsList modelsList) {
        if (recyclerView.getAdapter() instanceof RecyclerViewAdapter) {
            //noinspection unchecked
            ((RecyclerViewAdapter) recyclerView.getAdapter()).setModelsList(modelsList);
        }
    }
    @BindingAdapter("adapter")
    public static void setRecyclerViewProperties(RecyclerView recyclerView, RecyclerView.Adapter adapter) {
        recyclerView.setAdapter(adapter);
    }

    @BindingAdapter("hasFixedSize")
    public static void setRecyclerViewProperties(RecyclerView recyclerView, Boolean value) {
        recyclerView.setHasFixedSize(value);
    }
}
