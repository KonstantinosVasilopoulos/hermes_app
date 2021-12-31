package com.aueb.hermes.utils;

import java.io.Serializable;
import java.time.LocalDateTime;

public class TimeSlotDataPackage implements Serializable {
    private final LocalDateTime time;
    private final String app;
    private final long bytes;
    private final double consumption;  // Battery consumption

    public TimeSlotDataPackage(LocalDateTime time, String app, long bytes, double consumption) {
        this.time = time;
        this.app = app;
        this.bytes = bytes;
        this.consumption = consumption;
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

    public double getConsumption() {
        return consumption;
    }
}
