package com.aueb.hermes.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.aueb.hermes.models.Application;
import com.aueb.hermes.models.TimeSlot;
import com.aueb.hermes.models.TimeSlotId;

import java.util.List;

 public interface TimeSlotRepository extends JpaRepository<TimeSlot, TimeSlotId> {
    List<TimeSlot> findByApplication(Application application);
}
