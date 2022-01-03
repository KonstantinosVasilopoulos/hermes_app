package com.aueb.hermes.utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.time.LocalDateTime;

public class TimeSlotDataPackage implements Serializable {
    private final LocalDateTime time;
    private final String app;
    private final long bytes;
    private final long consumption;  // Battery consumption

    public TimeSlotDataPackage(LocalDateTime time, String app, long bytes, long consumption) {
        this.time = time;
        this.app = app;
        this.bytes = bytes;
        this.consumption = consumption;
    }

    // Convert to JSON object
    public JSONObject getJSONObject() {
        JSONObject obj = new JSONObject();
        try {
            obj.put("time", time);
            obj.put("app", app);
            obj.put("bytes", bytes);
            obj.put("consumption", consumption);

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

        return obj;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public String getApp() {
        return app;
    }

    public long getBytes() {
        return bytes;
    }

    public long getConsumption() {
        return consumption;
    }
}
