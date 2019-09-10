package com.vbarjovanu.workouttimer.business.models.generic;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collection;

public class ModelsList<T extends IModel> extends ArrayList<T> {
    /**
     * Creates a models list from an array of models
     *
     * @param models models array
     * @return a models list with no duplicates models
     */
    public static <Z extends IModel, X extends ModelsList<Z>> X fromArray(Z[] models, Class<X> classX) throws InstantiationException, IllegalAccessException {
        X modelsList = classX.newInstance();
        if (models != null && models.length > 0) {
            modelsList.ensureCapacity(models.length);
            for (Z workout : models) {
                //noinspection UseBulkOperation
                modelsList.add(workout);
            }
        }
        return modelsList;
    }

    /**
     * @param primaryKey ID of the model to find
     * @return the found model
     */
    public T find(String primaryKey) {
        T foundModel = null;
        for (T model : this) {
            if (model.getPrimaryKey().equals(primaryKey)) {
                foundModel = model;
                break;
            }
        }
        return foundModel;
    }

    /**
     * Updates the model in collection, if it already exists, based on the model ID
     *
     * @param model model to be added to the collection
     * @return true if the model already existed in collection and was updated
     */
    private boolean updateIfExists(T model) {
        T existingModel;
        if (model == null) {
            return false;
        }
        existingModel = this.find(model.getPrimaryKey());
        if (existingModel != null) {
            existingModel.update(model);
            return true;
        }
        return false;
    }

    /**
     * Adds a new model to collection or updates it in the collection if the model already exists
     *
     * @param model model to be added
     * @return
     */
    @Override
    public boolean add(T model) {
        if (this.updateIfExists(model)) {
            return true;
        } else {
            return super.add(model);
        }
    }

    /**
     * Adds a new model at a specific index into collection or updates it in the collection if the model already exists
     *
     * @param index   index in collection where the new model is going to be added
     * @param model model to be added
     */
    @Override
    public void add(int index, T model) {
        if (!this.updateIfExists(model)) {
            super.add(index, model);
        }
    }

    @Override
    public boolean addAll(@NonNull Collection<? extends T> c) {
        if (c.size() > 0) {
            for (T model : c) {
                this.add(model);
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean addAll(int index, @NonNull Collection<? extends T> c) {
        if (c.size() > 0) {
            for (T model : c) {
                this.add(index++, model);
            }
            return true;
        }
        return false;
    }}
