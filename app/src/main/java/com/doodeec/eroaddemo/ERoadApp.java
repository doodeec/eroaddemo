package com.doodeec.eroaddemo;

import android.app.Application;
import android.location.Location;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Logger;
import com.google.android.gms.analytics.Tracker;

/**
 * @author Dusan Bartos
 */
public class ERoadApp extends Application {

    //EROAD office location
    public static Location sEROADOfficeLocation;

    @Override
    public void onCreate() {
        super.onCreate();

        GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);

        // Global GA Settings
        // <!-- Google Analytics SDK V4 BUG20141213 Using a GA global xml freezes the app! Do config by coding. -->
        analytics.setDryRun(false);

        analytics.getLogger().setLogLevel(Logger.LogLevel.ERROR);
        analytics.setLocalDispatchPeriod(1000);

        // Create a new tracker
        Tracker tracker = analytics.newTracker(getString(R.string.google_analytics_id));
        tracker.setSessionTimeout(300);
        tracker.enableAutoActivityTracking(true);
        tracker.enableExceptionReporting(true);

        sEROADOfficeLocation = new Location("EROADApp");
        sEROADOfficeLocation.setLatitude(ERoadConfig.EROAD_OFFICE_LOCATION_LAT);
        sEROADOfficeLocation.setLongitude(ERoadConfig.EROAD_OFFICE_LOCATION_LON);
    }
}
