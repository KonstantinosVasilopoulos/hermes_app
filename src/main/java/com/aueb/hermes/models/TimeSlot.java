package com.aueb.hermes.models;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.persistence.Entity;
import javax.persistence.EmbeddedId;
import javax.persistence.Column;

@Entity
public class TimeSlot {
    @EmbeddedId
    private TimeSlotId id;

    @Column(name = "network_usage", nullable = true)
    private long networkUsage;

    @Column(name = "network_battery_consumption", nullable = true)
    private long networkBatteryConsumption;

    public TimeSlot() {
        
    }

    public TimeSlot(Device device, Application application, LocalDateTime fromTime) {
        id = new TimeSlotId(device, application, fromTime);
    }

    public TimeSlot(Device device, Application application, LocalDateTime fromTime, 
            long networkUsage, long networkBatteryConsumption) {
        this(device, application, fromTime);
        this.networkUsage = networkUsage;
        this.networkBatteryConsumption = networkBatteryConsumption;
    }

    // Getters & setters
    public TimeSlotId getId() {
        return id;
    }

    public LocalDateTime getFromTime() {
        return id.getFromTime();
    }

    public long getNetworkUsage() {
        return networkUsage;
    }

    public void setNetworkUsage(long networkUsage) {
        this.networkUsage = networkUsage;
    }

    public long getNetworkBatteryConsumption() {
        return networkBatteryConsumption;
    }

    public void setNetworkBatteryConsumption(long networkBatteryConsumption) {
        this.networkBatteryConsumption = networkBatteryConsumption;
    }

    public Device getDevice() {
        return id.getDevice();
    }

    public void setDevice(Device device) {
        id.setDevice(device);
    }

    public Application getApplication() {
        return id.getApplication();
    }

    public void setApplication(Application application) {
        id.setApplication(application);
    }

    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:00");
        return id.getFromTime().format(formatter);
    }
}
