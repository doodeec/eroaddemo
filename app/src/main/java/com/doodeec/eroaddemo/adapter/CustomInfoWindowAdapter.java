package com.doodeec.eroaddemo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.doodeec.eroaddemo.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * @author Dusan Bartos
 */
public class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    @InjectView(R.id.latitudeText)
    TextView mLatitude;
    @InjectView(R.id.longitudeText)
    TextView mLongitude;
    @InjectView(R.id.localTimeText)
    TextView mLocalTime;
    @InjectView(R.id.UTCTimeText)
    TextView mUTCTime;
    @InjectView(R.id.timezoneText)
    TextView mTimezone;

    private String mLatitudeValue;
    private String mLongitudeValue;
    private String mLocalTimeValue;
    private String mUTCTimeValue;
    private String mTimezoneValue;

    private Context mContext;
    private View mMarkerView;

    public CustomInfoWindowAdapter(Context context) {
        mContext = context;
    }

    public View getInfoWindow(Marker marker) {
        return null;
    }

    public View getInfoContents(Marker marker) {
        if (mMarkerView == null) {
            mMarkerView = LayoutInflater.from(mContext).inflate(R.layout.custom_info_window, null);
            ButterKnife.inject(this, mMarkerView);
        }

        mLatitude.setText(mLatitudeValue);
        mLongitude.setText(mLongitudeValue);
        mLocalTime.setText(mLocalTimeValue);
        mUTCTime.setText(mUTCTimeValue);
        mTimezone.setText(mTimezoneValue);
        return mMarkerView;
    }

    public void update(String latitude, String longitude, String localTime, String utcTime, String timezone) {
        mLatitudeValue = latitude;
        mLongitudeValue = longitude;
        mLocalTimeValue = localTime;
        mUTCTimeValue = utcTime;
        mTimezoneValue = timezone;
    }
}
