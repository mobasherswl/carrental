package com.epam.server.data;

import com.epam.common.dto.CarDto;
import com.epam.common.dto.RentalClassDto;
import com.epam.common.service.FleetService;
import com.epam.common.service.RentalClassService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Component
@DependsOn({"rentalClassDataPopulator"})
public class CarJsonDataPopulator implements DataPopulator {
    private static final Logger logger = LoggerFactory.getLogger(CarJsonDataPopulator.class);
    private FleetService fleetService;
    private RentalClassService rentalClassService;
    private File carsJsonFile;
    private ObjectMapper objectMapper;
    private TypeReference<List<CarDto>>  carDtoListTypeReference;

    @Autowired
    public CarJsonDataPopulator(FleetService fleetService, RentalClassService rentalClassService, @Qualifier("carsJsonFile") File carsJsonFile, ObjectMapper objectMapper, TypeReference<List<CarDto>> carDtoListTypeReference) {
        this.fleetService = fleetService;
        this.rentalClassService = rentalClassService;
        this.carsJsonFile = carsJsonFile;
        this.objectMapper = objectMapper;
        this.carDtoListTypeReference = carDtoListTypeReference;
    }

    @PostConstruct
    @Override
    public void populate() {
        List<RentalClassDto> rentalClassDtoList = rentalClassService.findAll();
        try {
            List<CarDto> carDtoList = objectMapper.readValue(
                    carsJsonFile,
                    carDtoListTypeReference
            );
            carDtoList.parallelStream().forEach(carDto -> carDto.setRentalClassDto(rentalClassDtoList.get(ThreadLocalRandom.current().nextInt(rentalClassDtoList.size()))));
            fleetService.add(carDtoList);
        } catch (IOException e) {
            logger.error("Car JSON carsJsonFile processing failed", e);
            throw new RuntimeException("Car JSON carsJsonFile processing failed", e);
        }
    }
}
