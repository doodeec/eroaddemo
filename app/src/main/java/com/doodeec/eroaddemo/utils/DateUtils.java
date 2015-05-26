package com.doodeec.eroaddemo.utils;

import java.text.DateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * @author Dusan Bartos
 */
public class DateUtils {

    public static String getLocalTime() {
        return DateFormat.getTimeInstance().format(new Date());
    }

    public static String getUTCtime() {
        DateFormat df = DateFormat.getTimeInstance();
        df.setTimeZone(TimeZone.getTimeZone("gmt"));
        return df.format(new Date());
    }
}
