package com.aueb.hermes.models;

import java.time.LocalDateTime;
import javax.persistence.Entity;
import javax.persistence.EmbeddedId;
import javax.persistence.Column;
import javax.persistence.ManyToOne;
import javax.persistence.JoinColumn;

@Entity
public class TimeSlot {
    @EmbeddedId
    private TimeSlotId id;
    private LocalDateTime fromTime;

    @Column(name = "network_usage", nullable = true)
    private long networkUsage;

    @Column(name = "network_battery_consumption", nullable = true)
    private long networkBatteryConsumption;

    @ManyToOne
    @JoinColumn(name = "device_uuid")
    private Device device;

    @ManyToOne
    @JoinColumn(name = "application_name")
    private Application application;

    public TimeSlot() {
        
    }

    public TimeSlot(String uuid, String name, LocalDateTime fromTime) {
        id = new TimeSlotId(uuid, name);
        this.fromTime = fromTime;
    }

    public TimeSlot(String uuid, String name, LocalDateTime fromTime, 
            long networkUsage, long networkBatteryConsumption) {
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
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }

    public Application getApplication() {
        return application;
    }

    public void setApplication(Application application) {
        this.application = application;
    }
}
