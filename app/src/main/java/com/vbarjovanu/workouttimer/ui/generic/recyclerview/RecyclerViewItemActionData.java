package com.vbarjovanu.workouttimer.ui.generic.recyclerview;

public class RecyclerViewItemActionData<T extends Enum> {
    private final T action;
    private final String id;

    RecyclerViewItemActionData(T action, String id) {
        this.action = action;
        this.id = id;
    }

    public T getAction() {
        return action;
    }

    public String getId() {
        return id;
    }
}
