package com.vbarjovanu.workouttimer.helpers;

import org.mockito.Mockito;

public class MockitoUtils {
    public static <T> T mock(Class<T> tClass){
        return Mockito.mock(tClass);
    }
}
