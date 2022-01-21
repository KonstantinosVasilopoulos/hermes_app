package com.aueb.hermes.models;

import java.io.Serializable;
import java.time.LocalDateTime;
import javax.persistence.Embeddable;
import javax.persistence.Column;
import javax.persistence.ManyToOne;
import javax.persistence.JoinColumn;

@Embeddable
public class TimeSlotId implements Serializable {
    @ManyToOne
    @JoinColumn(name = "device_uuid", nullable = false)
    private Device device;

    @ManyToOne
    @JoinColumn(name = "application_name", nullable = false)
    private Application application;

    @Column(name = "from_time", nullable = false)
    private LocalDateTime fromTime;

    public TimeSlotId() {
        
    }

    public TimeSlotId(Device device, Application application, LocalDateTime fromTime) {
        this.device = device;
        this.application = application;
        this.fromTime = fromTime;
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

    public LocalDateTime getFromTime() {
        return fromTime;
    }

    public void setFromTime(LocalDateTime fromTime) {
        this.fromTime = fromTime;
    }
}
