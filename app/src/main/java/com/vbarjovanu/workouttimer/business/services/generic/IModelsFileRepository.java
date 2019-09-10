package com.vbarjovanu.workouttimer.business.services.generic;

import com.vbarjovanu.workouttimer.business.models.generic.IModel;

import java.io.IOException;

public interface IModelsFileRepository<T extends IModel<T>> {
    /**
     * @param filePath path of the file that contains the models in JSON format
     * @return all the models stored in the file
     * @throws IOException
     */
    T[] loadModelsFromFile(String filePath) throws IOException;

    /**
     * @param filePath path of the file to contain the models in JSON format
     * @param models array of models objects to be saved in the file
     * @throws IOException
     */
    void saveModelsToFile(String filePath, T[] models) throws IOException;
}
