package com.vbarjovanu.workouttimer.ui.generic.events;

import androidx.annotation.Nullable;

import java.util.concurrent.atomic.AtomicBoolean;

public class EventContent<T> {
    private T content;
    private final AtomicBoolean hasBeenHandled;

    public EventContent(T content) {
        this.content = content;
        this.hasBeenHandled = new AtomicBoolean(false);
    }

    @SuppressWarnings("WeakerAccess")
    public boolean hasBeenHandled() {
        return hasBeenHandled.get();
    }

    public void setHandled() {
        this.hasBeenHandled.set(true);
    }

    public @Nullable
    T getContent() {
        if (!this.hasBeenHandled()) {
            return this.content;
        }
        return null;
    }
}
