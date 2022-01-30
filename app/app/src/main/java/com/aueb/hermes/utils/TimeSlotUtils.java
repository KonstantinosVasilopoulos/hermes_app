package com.aueb.hermes.utils;

import java.time.LocalDateTime;

public class TimeSlotUtils {
    public static LocalDateTime getTimeSlotHour(LocalDateTime query){
        if((query.getHour() % 4) != 0){
            return query.plusHours(4 - (query.getHour() % 4));
        }
        return query;
    }
}
