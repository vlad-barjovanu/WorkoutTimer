package com.vbarjovanu.workouttimer.helpers.formatters;

import java.util.Locale;

public class DurationFormatter {
    public static String formatSeconds(int seconds) {
        Locale locale;
        int h, m;
        String formattedValue;

        locale = Locale.forLanguageTag("en-US");
        if (locale == null) {
            locale = Locale.getDefault();
        }

        h = seconds / 3600;
        seconds -= (h * 3600);
        m = seconds / 60;
        seconds -= (m * 60);
        formattedValue = String.format(locale, "%02d", seconds);

        if (m > 0 || h > 0) {
            formattedValue = String.format(locale, "%02d:", m) + formattedValue;
        }
        if (h > 0) {
            formattedValue = String.format(locale, "%02d:", h) + formattedValue;
        }

        return formattedValue;
    }
}
