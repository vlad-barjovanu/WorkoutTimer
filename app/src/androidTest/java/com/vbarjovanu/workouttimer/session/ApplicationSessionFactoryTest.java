package com.vbarjovanu.workouttimer.session;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Assert;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.isA;
import static org.junit.Assert.*;

public class ApplicationSessionFactoryTest {

    @Test
    public void getApplicationSession() {
        Context ctx = InstrumentationRegistry.getInstrumentation().getContext();

        IApplicationSession applicationSession = ApplicationSessionFactory.getApplicationSession(ctx);
        Assert.assertNotNull(applicationSession);
        Assert.assertThat(applicationSession, isA(IApplicationSession.class));
    }
}