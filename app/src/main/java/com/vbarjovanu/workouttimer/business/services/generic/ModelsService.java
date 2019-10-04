package com.vbarjovanu.workouttimer.business.services.generic;

import com.vbarjovanu.workouttimer.business.models.generic.IModel;
import com.vbarjovanu.workouttimer.business.models.generic.ModelsList;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.UUID;

public class ModelsService<T extends IModel<T>, Z extends ModelsList<T>> implements IModelsService<T, Z> {
    private final IModelsFileRepository<T> modelsFileRepository;
    protected final IFileRepositorySettings modelsFileRepositorySettings;
    private final Class<T> classT;
    private final Class<Z> classZ;

    public ModelsService(IModelsFileRepository<T> modelsFileRepository, IFileRepositorySettings modelsFileRepositorySettings, Class<T> classT, Class<Z> classZ) {
        this.modelsFileRepository = modelsFileRepository;
        this.modelsFileRepositorySettings = modelsFileRepositorySettings;
        this.classT = classT;
        this.classZ = classZ;
    }

    protected String getModelPluralName() {
        return this.classT.getSimpleName() + "s";
    }

    protected String getFilePath(String profileId) {
        return this.modelsFileRepositorySettings.getFolderPath() + "/" + this.getModelPluralName() + "-" + profileId + ".json";
    }

    @Override
    public Z loadModels(String profileId) {
        Z modelsList = null;
        T[] modelsArray;
        try {
            modelsArray = this.modelsFileRepository.loadModelsFromFile(this.getFilePath(profileId));
            if (modelsArray != null) {
                modelsList = ModelsList.fromArray(modelsArray, this.classZ);
            }
        } catch (Exception e) {
            e.printStackTrace();
            try {
                modelsList = this.classZ.newInstance();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return modelsList;
    }

    @Override
    public T loadModel(String profileId, String primaryKey) {
        T foundWorkout = null;
        Z modelsList = this.loadModels(profileId);
        if (modelsList != null) {
            foundWorkout = modelsList.find(primaryKey);
        }

        return foundWorkout;
    }

    @Override
    public boolean saveModel(String profileId, T model) {
        Z models = this.loadModels(profileId);
        models.add(model);
        try {
            //noinspection unchecked
            this.modelsFileRepository.saveModelsToFile(this.getFilePath(profileId), models.toArray((T[]) Array.newInstance(this.classT, 0)));
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public boolean deleteModel(String profileId, T model) {
        if (model != null) {
            return this.deleteModel(profileId, model.getPrimaryKey());
        }
        return false;
    }

    @Override
    public boolean deleteModel(String profileId, String primaryKey) {
        Z models = this.loadModels(profileId);
        T model = models.find(primaryKey);
        if (model != null && models.remove(model)) {
            try {
                //noinspection unchecked
                this.modelsFileRepository.saveModelsToFile(this.getFilePath(profileId), models.toArray((T[]) Array.newInstance(this.classT, 0)));
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

    @Override
    public T createModel() throws InstantiationException, IllegalAccessException, InvocationTargetException {
        T model;
        Constructor<T> constructor = null;
        String primaryKey;
        try {
            constructor = this.classT.getConstructor(String.class);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        primaryKey = UUID.randomUUID().toString();
        if (constructor == null) {
            model = this.classT.newInstance();
            model.setPrimaryKey(primaryKey);
        } else {
            model = constructor.newInstance(primaryKey);
        }
        return model;
    }
}
