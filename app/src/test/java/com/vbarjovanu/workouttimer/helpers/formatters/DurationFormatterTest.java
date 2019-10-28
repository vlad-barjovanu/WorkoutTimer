package com.vbarjovanu.workouttimer.helpers.formatters;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class DurationFormatterTest {

    @Test
    public void formatSeconds() {
        assertEquals("59", DurationFormatter.formatSeconds(59));
        assertEquals("01:00", DurationFormatter.formatSeconds(60));
        assertEquals("01:01", DurationFormatter.formatSeconds(61));
        assertEquals("59:59", DurationFormatter.formatSeconds(3599));
        assertEquals("01:00:00", DurationFormatter.formatSeconds(3600));
        assertEquals("01:00:01", DurationFormatter.formatSeconds(3601));
        assertEquals("99:00:01", DurationFormatter.formatSeconds(3600*99+1));
    }
}