package com.vbarjovanu.workouttimer.ui.generic.mediaplayer;

@SuppressWarnings("WeakerAccess")
public abstract class QueueData<T> {
    private final T data;
    private final float speed;

    public QueueData(T data, float speed) {
        this.data = data;
        this.speed = speed;
    }

    T getData() {
        return data;
    }

    public float getSpeed() {
        return speed;
    }
}
