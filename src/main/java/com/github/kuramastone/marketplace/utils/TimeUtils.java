package com.github.kuramastone.marketplace.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class TimeUtils {
    /**
     * Converts a timestamp in milliseconds to a readable date-time string.
     *
     * @param timestamp the timestamp in milliseconds to be converted
     * @return a human-readable string in the format "yyyy-MM-dd HH:mm:ss"
     */
    public static String convertTimeToReadable(long timestamp) {
        // Create a Date object using the timestamp
        Date date = new Date(timestamp);

        // Define the desired date format
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));

        // Format the Date object into a readable string
        return sdf.format(date) + " UTC";
    }

}
