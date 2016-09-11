package com.epam.server.rentcarstatistics

import com.epam.server.model.Car
import com.epam.server.model.RentCar
import com.epam.server.model.RentalClass
import spock.lang.Specification

import java.time.ZoneId
import java.time.ZonedDateTime

import static java.util.Arrays.asList

class RentCarStatisticGeneratorTest extends Specification {

    def "converting from RentCar List to Map where key is RentClass and value rent minutes divided per day for same day rent and return"() {
        given:
        def rentCarStatisticGenerator = new RentCarStatisticGenerator()
        RentCar rentCar = generateDefaultRentalCar("rentalClassName1", 0, 10)
        RentCar rentCar1 = generateDefaultRentalCar("rentalClassName2", 0, 20)
        RentCar rentCar2 = generateDefaultRentalCar("rentalClassName3", 0, 30)
        RentCar rentCar3 = generateDefaultRentalCar("rentalClassName4", 0, 40)
        def rentCarList = asList(rentCar, rentCar1, rentCar2, rentCar3)

        when:
        def mapWithRentalClassAndMinutesOfRentForEachDay = rentCarStatisticGenerator.getMapWithRentalClassAndMinutesOfRentForEachDay(rentCarList)

        then:
        mapWithRentalClassAndMinutesOfRentForEachDay.keySet().size() == 4
        mapWithRentalClassAndMinutesOfRentForEachDay.get(new RentalClass("rentalClassName1")).allRentMinutes == 10
        mapWithRentalClassAndMinutesOfRentForEachDay.get(new RentalClass("rentalClassName2")).allRentMinutes == 20
        mapWithRentalClassAndMinutesOfRentForEachDay.get(new RentalClass("rentalClassName3")).allRentMinutes == 30
        mapWithRentalClassAndMinutesOfRentForEachDay.get(new RentalClass("rentalClassName4")).allRentMinutes == 40
    }

    def "converting from RentCar List to Map where key is RentClass and value rent minutes divided per day for different day rent and return"() {
        given:
        def minutesPerDay = 24*60
        def rentCarStatisticGenerator = new RentCarStatisticGenerator()
        RentCar rentCar = generateDefaultRentalCar("rentalClassName1", 1, 10)
        RentCar rentCar1 = generateDefaultRentalCar("rentalClassName2", 4, 20)
        RentCar rentCar2 = generateDefaultRentalCar("rentalClassName3", 6, 30)
        RentCar rentCar3 = generateDefaultRentalCar("rentalClassName4", 8, 40)
        def rentCarList = asList(rentCar, rentCar1, rentCar2, rentCar3)

        when:
        def mapWithRentalClassAndMinutesOfRentForEachDay = rentCarStatisticGenerator.getMapWithRentalClassAndMinutesOfRentForEachDay(rentCarList)

        then:
        mapWithRentalClassAndMinutesOfRentForEachDay.keySet().size() == 4
        mapWithRentalClassAndMinutesOfRentForEachDay.get(new RentalClass("rentalClassName1")).allRentMinutes == (10+minutesPerDay*1)
        mapWithRentalClassAndMinutesOfRentForEachDay.get(new RentalClass("rentalClassName2")).allRentMinutes == (20+minutesPerDay*4)
        mapWithRentalClassAndMinutesOfRentForEachDay.get(new RentalClass("rentalClassName3")).allRentMinutes == (30+minutesPerDay*6)
        mapWithRentalClassAndMinutesOfRentForEachDay.get(new RentalClass("rentalClassName4")).allRentMinutes == (40+minutesPerDay*8)
    }


    def RentCar generateDefaultRentalCar(String rentClassName, int timeDiffranceInDays, int timeDifferenceInMinutes) {
        def rentalClass = new RentalClass(rentClassName)
        def car = new Car();
        car.setRentalClass(rentalClass)
        def rentCar = new RentCar()
        rentCar.setCar(car)
        rentCar.setStartDateTime(ZonedDateTime.of(1, 1, 1, 1, 1, 0, 0, ZoneId.systemDefault()).toLocalDateTime())
        rentCar.setEndDateTime(ZonedDateTime.of(1, 1, (1 + timeDiffranceInDays), 1, (1 + timeDifferenceInMinutes), 0, 0, ZoneId.systemDefault()).toLocalDateTime())
        return rentCar
    }


}