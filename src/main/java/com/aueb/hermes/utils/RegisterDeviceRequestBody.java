package com.aueb.hermes.utils;

import java.io.Serializable;

public class RegisterDeviceRequestBody implements Serializable {
    private String uuid;
    private float antennaBatteryConsumption;

    public RegisterDeviceRequestBody(){
        
    }

    public RegisterDeviceRequestBody(String uuid, float antennaBatteryConsumption) {
        this.uuid = uuid;
        this.antennaBatteryConsumption = antennaBatteryConsumption;
    }

    public String getUuid() {
        return uuid;
    }

    public float getAntennaBatteryConsumption() {
        return antennaBatteryConsumption;
    }
}
