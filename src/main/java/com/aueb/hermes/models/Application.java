package com.aueb.hermes.models;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Application {
    @Id
    private String name;

    public Application(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
