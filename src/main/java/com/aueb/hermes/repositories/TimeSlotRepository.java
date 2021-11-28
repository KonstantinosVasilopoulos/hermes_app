package com.aueb.hermes.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.aueb.hermes.models.TimeSlot;
import com.aueb.hermes.models.TimeSlotId;

 public interface TimeSlotRepository extends JpaRepository<TimeSlot, TimeSlotId> {
    
}
