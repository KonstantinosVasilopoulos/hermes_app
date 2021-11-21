package com.aueb.hermes.models;

import java.io.Serializable;
import javax.persistence.Embeddable;
import javax.persistence.Id;
import javax.persistence.Column;

@Embeddable
public class TimeSlotId implements Serializable {
    @Column(name = "uuid", nullable = false)
    private String uuid;

    @Column(name = "name", nullable = false)
    private String name;

    public TimeSlotId(String uuid, String name) {
        this.uuid = uuid;
        this.name = name;
    }

    public String getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }
}
