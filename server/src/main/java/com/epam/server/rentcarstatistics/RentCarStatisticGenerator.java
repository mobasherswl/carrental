package com.epam.server.rentcarstatistics;

import com.epam.server.model.RentCar;
import com.epam.server.model.RentalClass;

import java.math.BigDecimal;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;

public class RentCarStatisticGenerator {

    public HashMap<RentalClass, RentMinutesPerDay> getMapWithRentalClassAndMinutesOfRentForEachDay(List<RentCar> rentHistoryList) {
        HashMap<RentalClass, RentMinutesPerDay> rentalClassMap = new HashMap<>();

        rentHistoryList.stream().forEach(rentCar -> {
            RentalClass rentalClass = rentCar.getCar().getRentalClass();
            rentalClassMap.putIfAbsent(rentalClass, new RentMinutesPerDay());

            if (ChronoUnit.DAYS.between(rentCar.getEndDateTime(), rentCar.getStartDateTime()) == 0) {
                addRentMinutesToRentalClassMap(rentalClassMap, rentalClass, rentCar.getStartDateTime(), rentCar.getEndDateTime());
            } else {
                LocalDateTime localDateTimeTemporary = rentCar.getStartDateTime();
                while (ChronoUnit.DAYS.between(localDateTimeTemporary, rentCar.getEndDateTime()) > 0) {
                    addRentMinutesToRentalClassMap(rentalClassMap, rentalClass, localDateTimeTemporary, ZonedDateTime.of(localDateTimeTemporary.toLocalDate().plusDays(1), LocalTime.MIDNIGHT, ZoneId.systemDefault()).toLocalDateTime());
                    localDateTimeTemporary = localDateTimeTemporary.withHour(0).withMinute(00).withSecond(00).plusDays(1);
                }
                addRentMinutesToRentalClassMap(rentalClassMap, rentalClass, localDateTimeTemporary, rentCar.getEndDateTime());
            }

        });
        return rentalClassMap;
    }

    private void addRentMinutesToRentalClassMap(HashMap<RentalClass, RentMinutesPerDay> rentalClassMap, RentalClass rentalClass, LocalDateTime localDateTimeFrom, LocalDateTime localDateTimeTo
    ) {
        Long rentMinutes = ChronoUnit.MINUTES.between(localDateTimeFrom, localDateTimeTo);
        DayOfWeek dayOfWeek = DayOfWeek.from(localDateTimeFrom);
        rentalClassMap.compute(rentalClass, (rentalClass1, rentMinutesPerDay) -> rentMinutesPerDay.addRentMinutesToDay(dayOfWeek, rentMinutes));
    }


    public String generateTableWithEarningsAndUtilizationByRentalClass(HashMap<RentalClass, RentMinutesPerDay> mapWithRentalClassAndHoursOfRentForEachRentalClass, RentMinutesPerDay minutesBetweenPerDay) {
        StringBuilder tableText = new StringBuilder();
        tableText.append("RentalClass   |   ").append("Earnings   |   ").append("Utilization").append("\n");
        mapWithRentalClassAndHoursOfRentForEachRentalClass.forEach((rentalClass, rentMinutesPerDay) -> {
            long allRentMinutes = rentMinutesPerDay.getAllRentMinutes();
            tableText.append(String.format("%s   |   %.2f     |     %.2f   \n", rentalClass.getName(), rentalClass.getRate().multiply(new BigDecimal(allRentMinutes / 60)), ((double) allRentMinutes / minutesBetweenPerDay.getAllRentMinutes()) * 100));
        });
        tableText.append("\n\n\n");
        tableText.append("Rental Class  |  ").append("MONDAY  |  Tuesday  |  Wednesday  |  Thursday  |  Friday  |  Saturday  |  Sunday ").append("\n");
        mapWithRentalClassAndHoursOfRentForEachRentalClass.forEach((rentalClass, rentMinutesPerDay) -> {
            tableText.append(rentalClass.getName() + "  |  ");
            for (DayOfWeek dayOfWeek : DayOfWeek.values())
                tableText.append(String.format("   %.2f  |  ", ((double) rentMinutesPerDay.getRentMinutesFor(dayOfWeek) / minutesBetweenPerDay.getRentMinutesFor(dayOfWeek)) * 100));
            tableText.append("\n");
        });
        return tableText.toString();
    }

    public RentMinutesPerDay getMapWithMinutesForEachDayBetweenPeriod(LocalDate fromLocalDate, LocalDate toLocalDate) {
        LocalDateTime fromLocalDateTime = ZonedDateTime.of(fromLocalDate, LocalTime.NOON, ZoneId.systemDefault()).toLocalDateTime();
        LocalDateTime toLocalDateTime = ZonedDateTime.of(toLocalDate, LocalTime.MIDNIGHT, ZoneId.systemDefault()).toLocalDateTime();
        RentMinutesPerDay rentMinutesPerDay = new RentMinutesPerDay();

        if (ChronoUnit.DAYS.between(fromLocalDateTime, toLocalDateTime) == 0) {
            addMinutesToRentingPeriodPerDay(fromLocalDateTime, toLocalDateTime, rentMinutesPerDay);
        } else {
            LocalDateTime localDateTimeTemporary = fromLocalDateTime;
            while (ChronoUnit.DAYS.between(localDateTimeTemporary, toLocalDateTime) > 0) {
                addMinutesToRentingPeriodPerDay(localDateTimeTemporary, ZonedDateTime.of(localDateTimeTemporary.toLocalDate().plusDays(1), LocalTime.MIDNIGHT, ZoneId.systemDefault()).toLocalDateTime(), rentMinutesPerDay);
                localDateTimeTemporary = localDateTimeTemporary.plusDays(1).withHour(0).withMinute(00).withSecond(00);
            }
            addMinutesToRentingPeriodPerDay(localDateTimeTemporary, toLocalDateTime, rentMinutesPerDay);
        }
        return rentMinutesPerDay;
    }

    private void addMinutesToRentingPeriodPerDay(LocalDateTime fromLocalDate, LocalDateTime toLocalDate, RentMinutesPerDay rentMinutesPerDay) {
        Long rentMinutes = ChronoUnit.MINUTES.between(fromLocalDate, toLocalDate);
        DayOfWeek dayOfWeek = DayOfWeek.from(fromLocalDate);
        rentMinutesPerDay.addRentMinutesToDay(dayOfWeek, rentMinutes);
    }

}