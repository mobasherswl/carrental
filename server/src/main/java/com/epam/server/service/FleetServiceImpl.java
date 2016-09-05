package com.epam.server.service;

import com.epam.common.dto.CarDto;
import com.epam.common.service.FleetService;
import com.epam.server.model.Car;
import com.epam.server.repo.BaseRepository;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by Ahmed_Khan on 5/10/2016.
 */
@Service
public class FleetServiceImpl implements FleetService {

    private Type carDtoType = new TypeToken<List<CarDto>>() {
    }.getType();
    private Type carType = new TypeToken<List<Car>>() {
    }.getType();
    private BaseRepository<Car, Long> carRepository;
    private ModelMapper modelMapper;

    @Autowired
    public FleetServiceImpl(BaseRepository<Car, Long> carRepository, ModelMapper modelMapper) {
        this.carRepository = carRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public CarDto add(CarDto carDto) {
        return modelMapper.map(
                carRepository.save(
                        modelMapper.map(carDto, Car.class)
                ), CarDto.class);
    }

    @Override
    public List<CarDto> findAll() {
        return modelMapper.map(carRepository.findAll(), carDtoType);
    }

    @Override
    public List<CarDto> add(List<CarDto> list) {
        return modelMapper.map(carRepository.save((Iterable<Car>) modelMapper.map(list, carType)), carDtoType);
    }

}
