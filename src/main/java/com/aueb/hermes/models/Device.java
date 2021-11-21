package com.aueb.hermes.models;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Device {
    @Id
    private String uuid;
    private float antennaBatteryUsage;

    public Device(String uuid, float antennaBatteryUsage) {
        this.uuid = uuid;
        this.antennaBatteryUsage = antennaBatteryUsage;
    }

    public String getUuid() {
        return uuid;
    }

    public float getAntennaBatteryUsage() {
        return antennaBatteryUsage;
    }
}
