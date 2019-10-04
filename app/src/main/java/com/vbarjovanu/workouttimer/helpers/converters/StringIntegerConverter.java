package com.vbarjovanu.workouttimer.helpers.converters;

import androidx.databinding.InverseMethod;

public class StringIntegerConverter {
    @InverseMethod("stringToInt")
    public static String intToString(int value) {
        return Integer.toString(value);
    }

    public static int stringToInt(String value) {
        if (value == null || value.length() == 0) {
            return 0;
        }
        return Integer.parseInt(value);
    }

    @InverseMethod("stringToInteger")
    public static String integerToString(Integer value) {
        if (value == null) {
            return "";
        }
        return Integer.toString(value);
    }

    public static Integer stringToInteger(String value) {
        if (value == null || value.length() == 0) {
            return null;
        }
        return Integer.valueOf(value);
    }
}
