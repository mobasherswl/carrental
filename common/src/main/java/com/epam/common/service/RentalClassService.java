package com.epam.common.service;

import com.epam.common.dto.RentalClassDto;

import java.util.List;

public interface RentalClassService {
    RentalClassDto add(RentalClassDto rentalClassDto);
    List<RentalClassDto> add(List<RentalClassDto> rentalClassDtoList);
    List<RentalClassDto> findAll();
}
