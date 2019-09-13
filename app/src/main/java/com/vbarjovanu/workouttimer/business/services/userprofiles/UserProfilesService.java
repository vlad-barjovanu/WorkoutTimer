package com.vbarjovanu.workouttimer.business.services.userprofiles;

import com.vbarjovanu.workouttimer.business.models.userprofiles.UserProfile;
import com.vbarjovanu.workouttimer.business.models.userprofiles.UserProfilesList;
import com.vbarjovanu.workouttimer.business.services.generic.IFileRepositorySettings;
import com.vbarjovanu.workouttimer.business.services.generic.IModelsFileRepository;
import com.vbarjovanu.workouttimer.business.services.generic.ModelsService;

public class UserProfilesService extends ModelsService<UserProfile, UserProfilesList> implements IUserProfilesService {
    public UserProfilesService(IModelsFileRepository<UserProfile> modelsFileRepository, IFileRepositorySettings modelsFileRepositorySettings, Class<UserProfile> classT, Class<UserProfilesList> classZ) {
        super(modelsFileRepository, modelsFileRepositorySettings, classT, classZ);
    }

    @Override
    protected String getFilePath(String profileId) {
        return this.modelsFileRepositorySettings.getFolderPath() + "/" + this.getModelPluralName() + ".json";
    }

    @Override
    public UserProfilesList loadModels() {
        return this.loadModels(null);
    }

    @Override
    public UserProfile loadModel(String primaryKey) {
        return this.loadModel(null, primaryKey);
    }

    @Override
    public boolean saveModel(UserProfile userProfile) {
        return this.saveModel(null, userProfile);
    }
}
