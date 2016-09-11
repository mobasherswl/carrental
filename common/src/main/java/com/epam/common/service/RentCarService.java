package com.epam.common.service;

import com.epam.common.dto.CarDto;
import com.epam.common.dto.RentCarDto;

import java.time.LocalDate;
import java.util.List;

/**
 * Created by Ahmed_Khan on 5/2/2016.
 */
public interface RentCarService {

    RentCarDto rentOut(RentCarDto rentedCarDto);

    List<CarDto> findAvailableCars();

    List<RentCarDto> findRentedOutCars();

    List<RentCarDto> getRentHistoryDto(LocalDate fromLocalDate, LocalDate toLocalDate);

    RentCarDto rentCarReturn(RentCarDto rentCarDto);

    String getRentHistoryStatistics(LocalDate fromLocalDate, LocalDate toLocalDate);
}
