package com.littlebits.sensorapp.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DateTimeFormatter {

    public static String getCurrentDateFormatted() {
        return new SimpleDateFormat("EEEE, dd", Locale.getDefault()).format(new Date());
    }

    public static String getCurrentMonthYearFormatted() {
        return new SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(new Date());
    }

    public static String getWorkoutDuration(long startMillis, long endMillis) {
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH.mm", Locale.US);
        timeFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        return timeFormat.format(new Date(startMillis)) + "-" + timeFormat.format(new Date(endMillis));
    }
}
