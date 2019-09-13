package com.vbarjovanu.workouttimer.business.models.userprofiles;

import com.vbarjovanu.workouttimer.business.models.generic.IModel;

public class UserProfile implements IModel<UserProfile> {
    private String id;
    private String name;

    public UserProfile(String id) {
        this.setId(id);
    }

    public String getId() {
        return this.id;
    }

    private UserProfile setId(String id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return this.name;
    }

    public UserProfile setName(String name) {
        this.name = name;
        return this;
    }

    @Override
    public String getPrimaryKey() {
        return this.getId();
    }

    @Override
    public void update(UserProfile object) {
        UserProfile userProfile = object;
        this.setId(userProfile.getId());
        this.setName(userProfile.getName());
    }
}
