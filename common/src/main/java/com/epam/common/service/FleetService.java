package com.epam.common.service;

import com.epam.common.dto.CarDto;

import java.util.List;

/**
 * Created by Ahmed_Khan on 5/10/2016.
 */
public interface FleetService {

    CarDto add(CarDto carDto);

    List<CarDto> add(List<CarDto> list);

    List<CarDto> findAll();

}
