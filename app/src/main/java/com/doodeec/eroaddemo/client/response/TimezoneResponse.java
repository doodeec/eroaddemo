package com.doodeec.eroaddemo.client.response;

import com.google.gson.annotations.SerializedName;

/**
 * @author Dusan Bartos
 */
public class TimezoneResponse {
    @SerializedName("timeZoneId")
    private String mTimeZoneId;

    public String getTimeZoneId() {
        return mTimeZoneId;
    }
}
