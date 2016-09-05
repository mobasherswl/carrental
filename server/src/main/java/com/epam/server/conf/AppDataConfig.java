package com.epam.server.conf;

import com.epam.common.dto.CarDto;
import com.epam.common.dto.CustomerDto;
import com.epam.common.dto.RentCarDto;
import com.epam.common.dto.RentalClassDto;
import com.epam.common.service.RentCarService;
import com.epam.server.model.Car;
import com.epam.server.model.RentCar;
import com.epam.server.repo.BaseRepository;
import com.epam.server.service.RentCarServiceImpl;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ClassPathResource;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.time.*;
import java.util.List;
import java.util.function.Function;

@Configuration
@ComponentScan(basePackages = {"com.epam.server.data"})
@Import(AppConfig.class)
public class AppDataConfig {
    @Resource
    BaseRepository<RentCar, Long> rentCarRepository;
    @Resource
    BaseRepository<Car, Long> carRepository;
    @Autowired
    AppConfig appConfig;
    @Value("${data.rentcar.filepath}")
    String filePath;
    @Value("${data.car.filepath}")
    String carsJsonFilename;
    @Value("${data.customer.filepath}")
    String customersJsonFilename;
    @Value("${data.rentalclass.filepath}")
    String rentalClassJsonFilename;

    @Bean
    ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        return objectMapper;
    }

    @Bean
    File carsJsonFile() throws IOException {
        return new ClassPathResource(carsJsonFilename).getFile();
    }

    @Bean
    File customersJsonFile() throws IOException {
        return new ClassPathResource(customersJsonFilename).getFile();
    }

    @Bean
    File rentalClassJsonFile() throws IOException {
        return new ClassPathResource(rentalClassJsonFilename).getFile();
    }

    @Bean
    TypeReference<List<CarDto>> carDtoListTypeReference() {
        return new TypeReference<List<CarDto>>() {
        };
    }

    @Bean
    TypeReference<List<CustomerDto>> customerDtoListTypeReference() {
        return new TypeReference<List<CustomerDto>>() {
        };
    }

    @Bean
    TypeReference<List<RentalClassDto>> rentalClassDtoListTypeReference() {
        return new TypeReference<List<RentalClassDto>>() {
        };
    }
    @Bean
    List<RentCarDto> rentCarDtoJsonData() throws IOException {
        return objectMapper().readValue(
                new ClassPathResource(filePath).getFile(),
                new TypeReference<List<RentCarDto>>() {
                }
        );
    }

    @Bean
    Clock rentCarStartDateTimeClock() throws IOException {
        return new RentCarJsonDataClock(rentCarDtoJsonData(), RentCarDto::getStartDateTime);
    }

    @Bean
    Clock rentCarEndDateTimeClock() throws IOException {
        return new RentCarJsonDataClock(rentCarDtoJsonData(), RentCarDto::getEndDateTime);
    }

    @Bean
    RentCarService rentCarServiceWithStartDateTime() throws IOException {
        return new RentCarServiceImpl(rentCarRepository, carRepository, appConfig.modelMapper(), rentCarStartDateTimeClock());
    }

    @Bean
    RentCarService rentCarServiceWithEndDateTime() throws IOException {
        return new RentCarServiceImpl(rentCarRepository, carRepository, appConfig.modelMapper(), rentCarEndDateTimeClock());
    }

    static class RentCarJsonDataClock extends Clock {
        private List<RentCarDto> rentCarDtoList;
        private Function<RentCarDto, LocalDateTime> localDateTimeFunction;
        private int index;

        public RentCarJsonDataClock(List<RentCarDto> rentCarDtoList, Function<RentCarDto, LocalDateTime> localDateTimeFunction) {
            this.rentCarDtoList = rentCarDtoList;
            this.localDateTimeFunction = localDateTimeFunction;
        }

        @Override
        public ZoneId getZone() {
            return ZoneId.systemDefault();
        }

        @Override
        public Clock withZone(ZoneId zone) {
            return this;
        }

        @Override
        public Instant instant() {
            return localDateTimeFunction.apply(rentCarDtoList.get(index++)).toInstant(ZoneOffset.UTC);
        }
    }
}
