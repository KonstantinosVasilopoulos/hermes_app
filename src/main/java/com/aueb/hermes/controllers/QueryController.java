package com.aueb.hermes.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.aueb.hermes.models.Application;
import com.aueb.hermes.models.TimeSlot;
import com.aueb.hermes.models.Device;
import com.aueb.hermes.repositories.ApplicationRepository;
import com.aueb.hermes.repositories.DeviceRepository;
import com.aueb.hermes.repositories.TimeSlotRepository;
import com.aueb.hermes.utils.HTTP_METHODS;

@RestController
@RequestMapping("api")
public class QueryController {

    @Autowired
    private ApplicationRepository applicationRepo;
    @Autowired
    private DeviceRepository deviceRepo;
    @Autowired
    private TimeSlotRepository timeSlotRepo;

    // Constants
    private final int TIME_SLOT_SIZE = 4; // hours
    private final String DATE_TIME_STRING_FORMAT = "dd-MM-yyyy-HH";

    // Note: The app's dots should be replaced with hyphes in order to fit URL standards
    // Hence all hyphens from the app string should be replaced again with dots
    @GetMapping("/battery-average/{start}/{slots}/{app}")
    public ResponseEntity<?> getBatteryAverage(@PathVariable String start, @PathVariable int slots, @PathVariable String app) {
        return getAverageStatistics(start, slots, app, false);
    }

    @GetMapping("/network-average/{start}/{slots}/{app}")
    public ResponseEntity<?> getNetworkAverage(@PathVariable String start, @PathVariable int slots, @PathVariable String app) {
        return getAverageStatistics(start, slots, app, true);
    }

    @GetMapping("/battery/{start}/{slots}/{uuid}/{app}")
    public ResponseEntity<?> getBattery(@PathVariable String start, @PathVariable int slots, @PathVariable String uuid, @PathVariable String app) {
        return getStatistics(start, slots, uuid, app, false);
    }

    @GetMapping("/network/{start}/{slots}/{uuid}/{app}")
    public ResponseEntity<?> getNetwork(@PathVariable String start, @PathVariable int slots, @PathVariable String uuid, @PathVariable String app) {
        return getStatistics(start, slots, uuid, app, true);
    }

    // Helper methods
    // forNetwork: Determines whether the function will query network or battery statistics
    private ResponseEntity<?> getAverageStatistics(String start, int slots, String app, boolean forNetwork) {
        InitializationMessageController.logConnection("/network-average", HTTP_METHODS.GET,
                (List<String>) List.of(start, String.valueOf(slots), app));

        // Replace hyphens with dots in the app string
        app = app.replaceAll("-", ".");

        // Make sure the requested app exists
        Optional<Application> application = applicationRepo.findById(app);
        if (!application.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        // Parse the datetime of the initial time slot
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_TIME_STRING_FORMAT);
        LocalDateTime startTime = LocalDateTime.parse(start, formatter);

        // Data structures
        Map<LocalDateTime, Long> result = new HashMap<>();
        Map<LocalDateTime, Integer> devices = new HashMap<>();
        LocalDateTime s = startTime;
        for (int i = 0; i < slots; i++) {
            // Zero is the default value
            result.put(s, 0L);
            devices.put(s, 0);

            // Go to the next slot
            s = s.plusHours(TIME_SLOT_SIZE);
        }

        // Find the sum of all consumption per time slot and the number devices having the requested app
        LocalDateTime current = startTime;
        for (TimeSlot slot : timeSlotRepo.findByApplication(application.get())) {
            // Filter out time slots that are outside the range of the starting time + slots
            if ((slot.getFromTime().isAfter(current) || slot.getFromTime().isEqual(current))
                    && slot.getFromTime().isBefore(current.plusHours(slots * TIME_SLOT_SIZE))) {
                result.put(slot.getFromTime(), forNetwork ? slot.getNetworkUsage() : slot.getNetworkBatteryConsumption());
                devices.put(slot.getFromTime(), devices.get(slot.getFromTime()) + 1);
            }
        }

        // Calculate the average network consumption per time slot
        s = startTime;
        for (int i = 0; i < slots; i++) {
            if (devices.get(s) != 0) {
                result.put(s, result.get(s) / devices.get(s));
            }

            // Same as above
            s = s.plusHours(TIME_SLOT_SIZE);
        }

        // Convert LocalDateTime instances to strings
        Map<String, Long> body = new HashMap<>();
        for (LocalDateTime time : result.keySet()) {
            body.put(time.format(formatter), result.get(time));
        }

        return ResponseEntity.status(HttpStatus.OK).body(body);
    }

    private ResponseEntity<?> getStatistics(String start, int slots, String uuid, String app, boolean forNetwork) {
        InitializationMessageController.logConnection("/battery", HTTP_METHODS.GET,
                (List<String>) List.of(start, String.valueOf(slots), uuid, app));

        // Replace hyphens with dots in the app string
        app = app.replaceAll("-", ".");

        // Make sure the requested app exists
        Optional<Application> application = applicationRepo.findById(app);
        if (!application.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        // Parse the datetime of the initial time slot
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_TIME_STRING_FORMAT);
        LocalDateTime startTime = LocalDateTime.parse(start, formatter);

        // Get the device while making sure such a device exists
        Optional<Device> device = deviceRepo.findById(uuid);
        if (!device.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        // Data structures
        Map<LocalDateTime, Long> result = new HashMap<>();
        LocalDateTime s = startTime;
        for (int i = 0; i < slots; i++) {
            // Zero is the default value
            result.put(s, 0L);

            // Go to the next slot
            s = s.plusHours(TIME_SLOT_SIZE);
        }

        // Iterate over the time slots and get the values for that specific device
        LocalDateTime current = startTime;
        for (TimeSlot slot : timeSlotRepo.findByApplicationAndDevice(application.get(), device.get())) {
            // Filter out time slots that are outside the range of the starting time + slots
            if ((slot.getFromTime().isAfter(current) || slot.getFromTime().isEqual(current))
                    && slot.getFromTime().isBefore(current.plusHours(slots * TIME_SLOT_SIZE))) {
                result.put(slot.getFromTime(), forNetwork ? slot.getNetworkUsage() : slot.getNetworkBatteryConsumption());
            }
        }

        // Convert LocalDateTime instances to strings
        Map<String, Long> body = new HashMap<>();
        for (LocalDateTime time : result.keySet()) {
            body.put(time.format(formatter), result.get(time));
        }

        // Return query results
        return ResponseEntity.status(HttpStatus.OK).body(body);
    }
}
