package com.aueb.hermes.models;

import java.util.Set;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
public class Device {
    @Id
    private String uuid;
    private float antennaBatteryUsage;

    @OneToMany(mappedBy = "device")
    private Set<TimeSlot> timeSlots;

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

    public void addTimeSlot(TimeSlot timeSlot) {
        timeSlots.add(timeSlot);
    }
}
