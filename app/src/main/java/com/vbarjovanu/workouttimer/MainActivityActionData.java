package com.vbarjovanu.workouttimer;

public class MainActivityActionData {
    private MainActivityAction action;

    MainActivityActionData(MainActivityAction action) {
        this.setAction(action);
    }

    public MainActivityAction getAction() {
        return action;
    }

    protected MainActivityActionData setAction(MainActivityAction action) {
        this.action = action;
        return this;
    }
}
