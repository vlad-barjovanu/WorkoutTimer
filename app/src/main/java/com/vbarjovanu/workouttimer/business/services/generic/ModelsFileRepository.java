package com.vbarjovanu.workouttimer.business.services.generic;

import com.google.gson.Gson;
import com.vbarjovanu.workouttimer.business.models.generic.IModel;
import com.vbarjovanu.workouttimer.helpers.files.TextFileReader;
import com.vbarjovanu.workouttimer.helpers.files.TextFileWriter;

import java.io.IOException;

public class ModelsFileRepository<T extends IModel<T>> implements IModelsFileRepository<T> {

    private Class<T> classT;
    private Class<T[]> classArrayOfT;

    /**
     *
     * @param classT
     * @param classArrayOfT
     */
    public ModelsFileRepository(Class<T> classT, Class<T[]> classArrayOfT) {
        this.classT = classT;
        this.classArrayOfT = classArrayOfT;
    }

    @Override
    public T[] loadModelsFromFile(String filePath) throws IOException {
        T[] models = null;
        Gson gson;

        TextFileReader reader = new TextFileReader();
        StringBuilder stringBuilder = reader.readFile(filePath);
        String content = stringBuilder.substring(0);
        gson = new Gson();
        models = gson.fromJson(content, this.classArrayOfT);

        return models;
    }

    @Override
    public void saveModelsToFile(String filePath, T[] models) throws IOException {
        String content;
        Gson gson;
        TextFileWriter textFileWriter = new TextFileWriter();
        gson = new Gson();
        content = gson.toJson(models);
        textFileWriter.writeFile(filePath, content);
    }
}
