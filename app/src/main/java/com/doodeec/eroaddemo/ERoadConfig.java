package com.doodeec.eroaddemo;

/**
 * @author Dusan Bartos
 */
public class ERoadConfig {

    // disable logs for release version
    public static final boolean ENABLE_LOGS = true;

    // api base for Timezone API
    public static final String API_BASE = "https://maps.googleapis.com/maps/api/timezone";
//    public static final String API_BASE = "https://maps.googleapis.com/maps/api/timezone/json?location=39.6034810,-119.6822510&key=AIzaSyCUihz4stotp-TvvMltMCy2jtAt6fQKa6E";

    // EROAD office location
    public static final double EROAD_OFFICE_LOCATION_LAT = -36.7221948;
    public static final double EROAD_OFFICE_LOCATION_LON = 174.7061665;
}
