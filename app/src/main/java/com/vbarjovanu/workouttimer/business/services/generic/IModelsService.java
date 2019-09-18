package com.vbarjovanu.workouttimer.business.services.generic;

import com.vbarjovanu.workouttimer.business.models.generic.IModel;
import com.vbarjovanu.workouttimer.business.models.generic.ModelsList;

import java.lang.reflect.InvocationTargetException;

public interface IModelsService<T extends IModel<T>, Z extends ModelsList<T>> {
    /**
     * @param profileId user's profile ID for which to load the models from repository
     * @return a list of models stored for the identified profile
     */
    Z loadModels(String profileId);

    /**
     * @param profileId user's profile ID
     * @param primaryKey models's ID
     * @return the model identified by the ID
     */
    T loadModel(String profileId, String primaryKey);

    /**
     * @param profileId user's profile ID
     * @param model   model to save
     * @return true if the model was saved successfully
     */
    boolean saveModel(String profileId, T model);

    /**
     * Creates a model and initializes the primary key
     * @return the created model
     */
    T createModel() throws InstantiationException, IllegalAccessException, InvocationTargetException;
}
