package com.vbarjovanu.workouttimer.ui.generic.events;

import androidx.lifecycle.MutableLiveData;

import com.vbarjovanu.workouttimer.ui.generic.viewmodels.ISynchronizable;

import java.util.concurrent.CountDownLatch;

public class Event<T extends EventContent> extends MutableLiveData<T> implements ISynchronizable {
    private CountDownLatch countDownLatch;

    public Event() {
    }

    public Event(T value) {
        super(value);
    }

    @Override
    public void setValue(T value) {
        super.setValue(value);
        this.decreaseCountDownLatch();
    }

    @Override
    public void setCountDownLatch(CountDownLatch countDownLatch) {
        this.countDownLatch = countDownLatch;
    }

    private void decreaseCountDownLatch() {
        if (this.countDownLatch != null) {
            this.countDownLatch.countDown();
        }
    }
}
