package com.vbarjovanu.workouttimer.business.models.generic;

public interface IModel<T extends IModel> extends Identifiable, Updatable<T> {
}
