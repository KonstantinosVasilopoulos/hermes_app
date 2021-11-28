package com.aueb.hermes.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.aueb.hermes.models.Device;

public interface DeviceRepository extends JpaRepository<Device, String> {
    
}
