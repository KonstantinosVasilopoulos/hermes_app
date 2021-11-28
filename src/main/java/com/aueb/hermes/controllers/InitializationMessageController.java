package com.aueb.hermes.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import java.time.LocalDateTime;

import com.aueb.hermes.utils.InitializationMessageRequestBody;
import com.aueb.hermes.models.Application;
import com.aueb.hermes.models.TimeSlot;
import com.aueb.hermes.repositories.ApplicationRepository;
import com.aueb.hermes.repositories.DeviceRepository;
import com.aueb.hermes.repositories.TimeSlotRepository;

@RestController
public class InitializationMessageController {
    
    @Autowired
    private ApplicationRepository applicationRepo;
    @Autowired
    private DeviceRepository deviceRepo;
    @Autowired
    private TimeSlotRepository timeSlotRepo; 

    //to do : register device
    
    @PostMapping(path = "/init-data", consumes = "application/json", produces = "application/json")
    public ResponseEntity<String> sendInitializationData(@RequestBody InitializationMessageRequestBody data) {
        for (LocalDateTime hour : data.getNetworkUsage().keySet()){
            for (String app : data.getNetworkUsage().get(hour).keySet()){
                if(applicationRepo.findById(app) == null){
                    Application newApp = new Application(app);
                    applicationRepo.save(newApp); 
                }
                TimeSlot timeSlot = new TimeSlot(data.getUuid(), app, hour);
                timeSlotRepo.save(timeSlot);
            }
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
