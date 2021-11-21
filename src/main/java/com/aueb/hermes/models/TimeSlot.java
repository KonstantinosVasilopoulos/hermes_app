package com.aueb.hermes.models;

import java.time.LocalDateTime;
import javax.persistence.Entity;
import javax.persistence.EmbeddedId;
import javax.persistence.Column;

@Entity
public class TimeSlot {
    @EmbeddedId
    private TimeSlotId id;
    private LocalDateTime fromTime;

    @Column(name = "network_usage", nullable = true)
    private float networkUsage;

    @Column(name = "network_battery_consumption", nullable = true)
    private float networkBatteryConsumption;

    public TimeSlot(String uuid, String name, LocalDateTime fromTime) {
        id = new TimeSlotId(uuid, name);
        this.fromTime = fromTime;
    }

    public TimeSlot(String uuid, String name, LocalDateTime fromTime, 
            float networkUsage, float networkBatteryConsumption) {
        this(uuid, name, fromTime);
        this.networkUsage = networkUsage;
        this.networkBatteryConsumption = networkBatteryConsumption;
    }

    // Getters & setters
    public TimeSlotId getId() {
        return id;
    }

    public LocalDateTime getFromTime() {
        return fromTime;
    }

    public float getNetworkUsage() {
        return networkUsage;
    }

    public void setNetworkUsage(float networkUsage) {
        this.networkUsage = networkUsage;
    }

    public float getNetworkBatteryConsumption() {
        return this.networkBatteryConsumption = networkBatteryConsumption;
    }

    public void setNetworkBatteryConsumption(float networkBatteryConsumption) {
        this.networkBatteryConsumption = networkBatteryConsumption;
    }
}
