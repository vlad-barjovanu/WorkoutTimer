package com.vbarjovanu.workouttimer.ui.generic.viewmodels;

import java.util.concurrent.CountDownLatch;

public interface ISynchronizable {
    public void setCountDownLatch(CountDownLatch countDownLatch);
}
