package com.vbarjovanu.workouttimer.business.models.generic;

public interface Updatable<T extends Updatable> {
    public void update(T object);
}
