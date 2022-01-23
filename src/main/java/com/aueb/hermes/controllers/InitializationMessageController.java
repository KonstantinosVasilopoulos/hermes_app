package com.aueb.hermes.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

import java.util.List;
import java.util.UUID;
import java.util.ArrayList;
import java.util.Arrays;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;

import com.aueb.hermes.utils.RegisterDeviceRequestBody;
import com.aueb.hermes.models.Application;
import com.aueb.hermes.models.Device;
import com.aueb.hermes.models.TimeSlot;
import com.aueb.hermes.repositories.ApplicationRepository;
import com.aueb.hermes.repositories.DeviceRepository;
import com.aueb.hermes.repositories.TimeSlotRepository;
import com.aueb.hermes.utils.HTTP_METHODS;

@RestController
public class InitializationMessageController {
    
    @Autowired
    private ApplicationRepository applicationRepo;
    @Autowired
    private DeviceRepository deviceRepo;
    @Autowired
    private TimeSlotRepository timeSlotRepo;

    // Initial registration request
    @PostMapping(path = "/register-device", consumes = "application/json")
    public ResponseEntity<String> registerDevice(@RequestBody RegisterDeviceRequestBody data) {
        logConnection("/register-device", HTTP_METHODS.POST, null);
        // Create a new device
        Device device = deviceRepo.findByUuid(data.getUuid());
        if(device == null){
            device= new Device(data.getUuid(), data.getAntennaBatteryConsumption());
            deviceRepo.save(device);
        }

        // Send a confirmation response
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PostMapping(path = "/init-data/{uuid}", consumes = "application/json")
    public ResponseEntity<String> sendInitializationData(@PathVariable String uuid, @RequestBody String body) {
        logConnection("init-data", HTTP_METHODS.POST, new ArrayList<String>(Arrays.asList(uuid)));

        // Parse and save the data
        JSONArray data = new JSONArray(body);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        JSONObject obj;
        LocalDateTime time;
        String app;
        long bytes, consumption;
        for (int i = 0; i < data.length(); i++) {
            obj = data.getJSONObject(i);

            try {
                // Retrieve the time
                time = LocalDateTime.parse(obj.getString("time"), formatter);

                // Get the app's name and check whether it exists
                app = obj.getString("app");
                if (!applicationRepo.findById(app).isPresent()) {
                    // Create a new application
                    Application application = new Application(app);
                    applicationRepo.save(application);
                }

                // Get the rest of the data
                bytes = obj.getLong("bytes");
                consumption = obj.getLong("consumption");

                // Create a new time slot and wire it with the other models
                Device device = deviceRepo.findByUuid(uuid);
                Application application = applicationRepo.findById(app).get();
                TimeSlot timeSlot = new TimeSlot(device, application, time, bytes, consumption);
                timeSlotRepo.save(timeSlot);
                device.addTimeSlot(timeSlot);
                application.addTimeSlot(timeSlot);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        // Return a confirmation response
        return new ResponseEntity<>(HttpStatus.OK);
    }

    // Helper methods
    // Print console logs for incoming connections
    public static void logConnection(String url, HTTP_METHODS method, List<String> arguments) {
        // Add a starting '/' if not present
        if (!url.startsWith("/")) {
            url = "/" + url;
        }

        // Form console message
        String message = method.name() + " " + url;
        if (arguments != null) {
            for (String arg : arguments) {
                message += " " + arg;
            }
        }

        System.out.println(message);
    }
}
