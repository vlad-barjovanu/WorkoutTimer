package com.vbarjovanu.workouttimer.ui.generic.recyclerview;

import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import com.vbarjovanu.workouttimer.R;
import com.vbarjovanu.workouttimer.business.models.generic.IModel;
import com.vbarjovanu.workouttimer.business.models.generic.ModelsList;
import com.vbarjovanu.workouttimer.ui.generic.events.SingleLiveEvent;

public abstract class RecyclerViewAdapter<T extends IModel, Z extends Enum> extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private final SingleLiveEvent<RecyclerViewItemActionData<Z>> itemAction;

    private ModelsList<T> modelsList;

    private final int dataBindingItemModelVariableId;

    private Integer selectedItemPosition;

    private Integer itemBackgroundColor;

    private Integer itemTextColor;

    private Integer selectedItemBackgroundColor;

    private Integer selectedItemTextColor;

    protected RecyclerViewAdapter(@NonNull ModelsList<T> modelsList, int dataBindingItemModelVariableId) {
        this.modelsList = modelsList;
        this.itemAction = new SingleLiveEvent<>();
        this.dataBindingItemModelVariableId = dataBindingItemModelVariableId;
        this.selectedItemPosition = null;
        this.itemBackgroundColor = null;
        this.itemTextColor = null;
        this.selectedItemBackgroundColor = null;
        this.selectedItemTextColor = null;
    }

    @Override
    @NonNull
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View root = layoutInflater.inflate(viewType, parent, false);
        ViewDataBinding binding = DataBindingUtil.bind(root);
        return new ViewHolder(root, binding, this.dataBindingItemModelVariableId);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerViewAdapter.ViewHolder holder, final int position) {
        ItemModel itemModel = this.createItemModel(this.getModelForPosition(position));
        itemModel.setOnItemClickListener((itemModel1, view) -> onItemClick(view, itemModel1, holder, position));
        if (this.selectedItemPosition != null && this.selectedItemPosition.equals(position)) {
            itemModel.setColor(this.selectedItemBackgroundColor);
            itemModel.setTextColor(this.selectedItemTextColor);
        }
        holder.bind(itemModel);
    }

    @Override
    public int getItemViewType(int position) {
        return getLayoutIdForPosition(position);
    }

    @SuppressWarnings("WeakerAccess")
    protected T getModelForPosition(int position) {
        return this.modelsList.get(position);
    }

    @Override
    public int getItemCount() {
        return this.modelsList.size();
    }

    public RecyclerViewAdapter<T, Z> setModelsList(ModelsList<T> modelsList) {
        if (modelsList != null) {
            this.modelsList = modelsList;
            this.notifyDataSetChanged();
        }
        return this;
    }

    public SingleLiveEvent<RecyclerViewItemActionData<Z>> getItemAction() {
        return itemAction;
    }

    protected void triggerItemAction(Z action, String id) {
        this.itemAction.setValue(new RecyclerViewItemActionData<>(action, id));
    }

    protected abstract ItemModel createItemModel(T model);

    protected abstract void onItemClick(View view, ItemModel itemModel, final RecyclerViewAdapter.ViewHolder holder, int position);

    protected abstract int getLayoutIdForPosition(int position);

    protected static class ViewHolder extends RecyclerView.ViewHolder {
        private ViewDataBinding itemBinding;
        private int variableId;

        ViewHolder(@NonNull View itemView, ViewDataBinding itemBinding, int variableId) {
            super(itemView);
            this.itemBinding = itemBinding;
            this.variableId = variableId;
        }

        void bind(ItemModel itemModel) {
            if (this.itemBinding != null) {
                this.itemBinding.setVariable(this.variableId, itemModel);
                this.itemBinding.executePendingBindings();
            }
        }
    }

    public RecyclerViewAdapter<T, Z> setSelectedItemPosition(Integer selectedItemPosition) {
        Integer prevSelectedPosition = null;
        if (this.selectedItemPosition != null) {
            prevSelectedPosition = this.selectedItemPosition;
        }
        this.selectedItemPosition = selectedItemPosition;
        if (prevSelectedPosition != null) {
            this.notifyItemChanged(prevSelectedPosition);
        }
        this.notifyItemChanged(selectedItemPosition);
        return this;
    }

    @SuppressWarnings("WeakerAccess")
    public Integer getItemBackgroundColor() {
        return itemBackgroundColor;
    }

    @SuppressWarnings({"WeakerAccess", "UnusedReturnValue"})
    public RecyclerViewAdapter<T, Z> setItemBackgroundColor(Integer itemBackgroundColor) {
        this.itemBackgroundColor = itemBackgroundColor;
        return this;
    }

    @SuppressWarnings("WeakerAccess")
    public Integer getItemTextColor() {
        return itemTextColor;
    }

    @SuppressWarnings({"WeakerAccess", "UnusedReturnValue"})
    public RecyclerViewAdapter<T, Z> setItemTextColor(Integer itemTextColor) {
        this.itemTextColor = itemTextColor;
        return this;
    }

    public Integer getSelectedItemBackgroundColor() {
        return selectedItemBackgroundColor;
    }

    @SuppressWarnings({"UnusedReturnValue", "WeakerAccess"})
    public RecyclerViewAdapter<T, Z> setSelectedItemBackgroundColor(Integer selectedItemBackgroundColor) {
        this.selectedItemBackgroundColor = selectedItemBackgroundColor;
        return this;
    }

    public Integer getSelectedItemTextColor() {
        return selectedItemTextColor;
    }

    @SuppressWarnings({"UnusedReturnValue", "WeakerAccess"})
    public RecyclerViewAdapter<T, Z> setSelectedItemTextColor(Integer selectedItemTextColor) {
        this.selectedItemTextColor = selectedItemTextColor;
        return this;
    }

    public void setDefaultColors(Resources resources) {
        if (resources != null) {
            this.setItemTextColor(resources.getColor(R.color.recyclerViewItemTextColor, null));
            this.setItemBackgroundColor(resources.getColor(R.color.recyclerViewItemBackgroundColor, null));
            this.setSelectedItemTextColor(resources.getColor(R.color.recyclerViewSelectedItemTextColor, null));
            this.setSelectedItemBackgroundColor(resources.getColor(R.color.recyclerViewSelectedItemBackgroundColor, null));
        }
    }
}
