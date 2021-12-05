package com.aueb.hermes.utils;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashMap;

public class InitializationMessageRequestBody implements Serializable {

    private String uuid;
    private HashMap<LocalDateTime, HashMap<String, Float>> networkUsage;
    private HashMap<LocalDateTime, HashMap<String, Float>> batteryConsumption; 

    public InitializationMessageRequestBody(String uuid, HashMap<LocalDateTime, HashMap<String, Float>> networkUsage, HashMap<LocalDateTime, HashMap<String, Float>> batteryConsumption){
        this.uuid = uuid;
        this.networkUsage = networkUsage;
        this.batteryConsumption = batteryConsumption;
    }

    public String getUuid(){
        return uuid;
    }

    public HashMap<LocalDateTime, HashMap<String, Float>> getNetworkUsage(){
        return networkUsage;
    }

    public HashMap<LocalDateTime, HashMap<String, Float>> getBatteryConsumption(){
        return batteryConsumption;
    } 

}