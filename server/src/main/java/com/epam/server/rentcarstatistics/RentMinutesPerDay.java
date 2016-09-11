package com.epam.server.rentcarstatistics;

import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.util.EnumMap;

@Component
public class RentMinutesPerDay {
    private EnumMap<DayOfWeek, Long> rentMinutesPerDayEnumMap = new EnumMap<>(DayOfWeek.class);

    {
        for (DayOfWeek dayOfWeek : DayOfWeek.values()) {
            rentMinutesPerDayEnumMap.put(dayOfWeek, new Long(0));
        }
    }

    public RentMinutesPerDay addRentMinutesToDay(DayOfWeek dayOfWeek, long rentMinutes) {
        rentMinutesPerDayEnumMap.compute(dayOfWeek, (dayOfWeek1, oldRentMinutes) -> oldRentMinutes + rentMinutes);
        return this;
    }

    public long getAllRentMinutes() {
        return rentMinutesPerDayEnumMap.values().stream().mapToLong(value -> value).sum();
    }

    public Long getRentMinutesFor(DayOfWeek dayOfWeek) {
        return rentMinutesPerDayEnumMap.get(dayOfWeek);
    }

    @Override
    public String toString() {
        return "RentMinutesPerDay{" +
                "rentMinutesPerDayEnumMap=" + rentMinutesPerDayEnumMap +
                '}';
    }
}
