package com.aueb.hermes.models;

import java.util.Set;
import java.util.HashSet;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
public class Application {
    @Id
    private String name;

    @OneToMany(mappedBy = "id.application", fetch = FetchType.EAGER)
    private Set<TimeSlot> timeSlots;

    public Application() {
        timeSlots = new HashSet<>();
    }

    public Application(String name) {
        this();
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void addTimeSlot(TimeSlot timeSlot) {
        timeSlots.add(timeSlot);
    }

    public String toString() {
        return name;
    }
}
