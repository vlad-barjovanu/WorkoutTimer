package com.vbarjovanu.workouttimer.business.models.userprofiles;

import com.vbarjovanu.workouttimer.business.models.generic.IModel;

import java.io.Serializable;

public class UserProfile implements IModel<UserProfile>, Serializable {
    private String id;
    private String name;
    private String description;
    private String imageFilePath;

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

    public String getDescription() {
        return description;
    }

    public UserProfile setDescription(String description) {
        this.description = description;
        return this;
    }

    public String getImageFilePath() {
        return imageFilePath;
    }

    public UserProfile setImageFilePath(String imageFilePath) {
        this.imageFilePath = imageFilePath;
        return this;
    }

    @Override
    public String getPrimaryKey() {
        return this.getId();
    }

    @Override
    public void setPrimaryKey(String primaryKey) {
        this.setId(primaryKey);
    }

    @Override
    public void update(UserProfile object) {
        this.setId(object.getId())
                .setName(object.getName())
                .setDescription(object.getDescription())
                .setImageFilePath(object.getImageFilePath());
    }
}
