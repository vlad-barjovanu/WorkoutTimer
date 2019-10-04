package com.vbarjovanu.workouttimer;

public class MainActivityModel {
    private final boolean newEntityButtonVisible;
    private final boolean saveEntityButtonVisible;

    MainActivityModel() {
        this(false, false);
    }

    MainActivityModel(boolean newEntityButtonVisible, boolean saveEntityButtonVisible) {
        this.newEntityButtonVisible = newEntityButtonVisible;
        this.saveEntityButtonVisible = saveEntityButtonVisible;
    }

    public boolean isNewEntityButtonVisible() {
        return newEntityButtonVisible;
    }

    @SuppressWarnings("WeakerAccess")
    public boolean isSaveEntityButtonVisible() {
        return saveEntityButtonVisible;
    }
}
