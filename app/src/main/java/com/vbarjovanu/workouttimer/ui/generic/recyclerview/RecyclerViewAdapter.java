package com.vbarjovanu.workouttimer.ui.generic.recyclerview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import com.vbarjovanu.workouttimer.business.models.generic.IModel;
import com.vbarjovanu.workouttimer.business.models.generic.ModelsList;
import com.vbarjovanu.workouttimer.ui.generic.events.SingleLiveEvent;

public abstract class RecyclerViewAdapter<T extends IModel, Z extends Enum> extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private final SingleLiveEvent<RecyclerViewItemActionData<Z>> itemAction;

    private ModelsList<T> modelsList;

    private final int dataBindingItemModelVariableId;

    protected RecyclerViewAdapter(@NonNull ModelsList<T> modelsList, int dataBindingItemModelVariableId) {
        this.modelsList = modelsList;
        this.itemAction = new SingleLiveEvent<>();
        this.dataBindingItemModelVariableId = dataBindingItemModelVariableId;
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
}
