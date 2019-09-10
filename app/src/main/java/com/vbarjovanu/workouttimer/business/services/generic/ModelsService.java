package com.vbarjovanu.workouttimer.business.services.generic;

import com.vbarjovanu.workouttimer.business.models.generic.IModel;
import com.vbarjovanu.workouttimer.business.models.generic.ModelsList;

import java.io.IOException;
import java.lang.reflect.Array;

public class ModelsService<T extends IModel<T>, Z extends ModelsList<T>> implements IModelsService<T, Z> {
    private final IModelsFileRepository<T> modelsFileRepository;
    private final IFileRepositorySettings modelsFileRepositorySettings;
    private final Class<T> classT;
    private final Class<Z> classZ;

    public ModelsService(IModelsFileRepository<T> modelsFileRepository, IFileRepositorySettings modelsFileRepositorySettings, Class<T> classT, Class<Z> classZ) {
        this.modelsFileRepository = modelsFileRepository;
        this.modelsFileRepositorySettings = modelsFileRepositorySettings;
        this.classT = classT;
        this.classZ = classZ;
    }

    private String getModelPluralName() {
        return this.classT.getSimpleName() + "s";
    }

    private String getFilePath(String profileId) {
        return this.modelsFileRepositorySettings.getFolderPath() + "/" + this.getModelPluralName() + "-" + profileId + ".json";
    }

    @Override
    public Z loadModels(String profileId) {
        Z modelsList = null;
        T[] modelsArray;
        try {
            modelsArray = this.modelsFileRepository.loadModelsFromFile(this.getFilePath(profileId));
            if (modelsArray != null) {
                modelsList = (Z) ModelsList.fromArray(modelsArray, this.classZ);
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
        Z workoutsList = this.loadModels(profileId);
        if (workoutsList != null) {
            foundWorkout = workoutsList.find(primaryKey);
        }

        return foundWorkout;
    }

    @Override
    public boolean saveModel(String profileId, T model) {
        Z workouts = this.loadModels(profileId);
        workouts.add(model);
        try {
            this.modelsFileRepository.saveModelsToFile(this.getFilePath(profileId), (T[]) Array.newInstance(this.classT, 0));
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
