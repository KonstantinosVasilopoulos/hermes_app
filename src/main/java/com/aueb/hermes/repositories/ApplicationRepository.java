package com.aueb.hermes.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.aueb.hermes.models.Application;

interface ApplicationRepository extends JpaRepository<Application, String> {
    
}
