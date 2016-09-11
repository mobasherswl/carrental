package com.epam.server.service;

import com.epam.common.dto.CarDto;
import com.epam.common.dto.CustomerDto;
import com.epam.common.dto.RentCarDto;
import com.epam.server.model.Car;
import com.epam.server.model.RentCar;
import com.epam.server.repo.CarRepository;
import com.epam.server.repo.RentCarRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.*;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class RentCarServiceImplTest {
    private RentCarServiceImpl rentCarService;
    @Mock
    private RentCarRepository rentCarRepository;
    @Mock
    private CarRepository carRepository;
    @Mock
    private ModelMapper modelMapper;
    private Clock clock;

    @Before
    public void init() {
        clock = Clock.fixed(Instant.now(), ZoneId.systemDefault());
        rentCarService = new RentCarServiceImpl(rentCarRepository, carRepository, modelMapper, clock);
    }

    @Test
    public void rentOutCar() {
        RentCarDto expectedRentCarDto = new RentCarDto();
        RentCarDto newRentCarDto = mock(RentCarDto.class);
        RentCarDto rentedCarDto = new RentCarDto();
        RentCar rentCar = mock(RentCar.class);
        CarDto carDto = mock(CarDto.class);
        CustomerDto customerDto = mock(CustomerDto.class);

        expectedRentCarDto.setId(1L);
        expectedRentCarDto.setStartDateTime(LocalDateTime.now(clock));
        expectedRentCarDto.setCustomerDto(customerDto);
        expectedRentCarDto.setCarDto(carDto);
        expectedRentCarDto.setEndDateTime(null);

        rentedCarDto.setId(1L);
        rentedCarDto.setStartDateTime(LocalDateTime.now(clock));
        rentedCarDto.setCustomerDto(customerDto);
        rentedCarDto.setCarDto(carDto);
        rentedCarDto.setEndDateTime(null);

        when(modelMapper.map(newRentCarDto, RentCar.class)).thenReturn(rentCar);
        when(modelMapper.map(rentCar, RentCarDto.class)).thenReturn(rentedCarDto);
        when(rentCarRepository.save(rentCar)).thenReturn(rentCar);
        assertEquals(expectedRentCarDto, rentCarService.rentOut(newRentCarDto));
        verify(modelMapper, times(1)).map(newRentCarDto, RentCar.class);
        verify(modelMapper, times(1)).map(rentCar, RentCarDto.class);
        verify(rentCarRepository, times(1)).save(rentCar);
    }

    @Test(expected = DataIntegrityViolationException.class)
    public void rentOutSameCarTwice() {
        RentCarDto newRentCarDto = mock(RentCarDto.class);
        RentCarDto rentedCarDto = new RentCarDto();
        RentCar rentCar = mock(RentCar.class);
        CarDto carDto = mock(CarDto.class);
        CustomerDto customerDto = mock(CustomerDto.class);

        rentedCarDto.setId(1L);
        rentedCarDto.setCustomerDto(customerDto);
        rentedCarDto.setCarDto(carDto);

        when(modelMapper.map(newRentCarDto, RentCar.class)).thenReturn(rentCar);
        when(modelMapper.map(rentCar, RentCarDto.class)).thenReturn(rentedCarDto);
        when(rentCarRepository.save(rentCar)).thenThrow(DataIntegrityViolationException.class);
        rentCarService.rentOut(newRentCarDto);
        verify(modelMapper, times(1)).map(newRentCarDto, RentCar.class);
        verify(modelMapper, times(0)).map(rentCar, RentCarDto.class);
        verify(rentCarRepository, times(1)).save(rentCar);
    }

    @Test
    public void returnCar() {
        CarDto carDto = mock(CarDto.class);
        CustomerDto customerDto = mock(CustomerDto.class);
        RentCarDto rentedCarDto = new RentCarDto();
        RentCar rentCar = mock(RentCar.class);
        RentCarDto expectedRentCarDto = new RentCarDto();

        expectedRentCarDto.setId(1L);
        expectedRentCarDto.setStartDateTime(LocalDateTime.now(clock));
        expectedRentCarDto.setCustomerDto(customerDto);
        expectedRentCarDto.setCarDto(carDto);
        expectedRentCarDto.setEndDateTime(LocalDateTime.now(clock));

        rentedCarDto.setId(1L);
        rentedCarDto.setStartDateTime(LocalDateTime.now(clock));
        rentedCarDto.setCustomerDto(customerDto);
        rentedCarDto.setCarDto(carDto);
        rentedCarDto.setEndDateTime(LocalDateTime.now(clock));
        when(rentCarRepository.findOne(anyLong())).thenReturn(rentCar);
        when(rentCarRepository.save(rentCar)).thenReturn(rentCar);
        when(modelMapper.map(rentCar, RentCarDto.class)).thenReturn(rentedCarDto);
        assertEquals(expectedRentCarDto, rentCarService.rentCarReturn(rentedCarDto));
        verify(rentCarRepository, times(1)).findOne(anyLong());
        verify(rentCarRepository, times(1)).save(rentCar);
        verify(modelMapper, times(1)).map(rentCar, RentCarDto.class);
    }

    @Test
    public void findRentedOutCars() {
        List<RentCar> rentCarList = new ArrayList<>();
        RentCar rentCar = new RentCar();
        RentCarDto rentCarDto = new RentCarDto();

        rentCarList.add(rentCar);
        when(rentCarRepository.findAll()).thenReturn(rentCarList);
        when(modelMapper.map(rentCar, RentCarDto.class)).thenReturn(rentCarDto);
        assertEquals(rentCarService.findRentedOutCars().size(), 1);
        verify(rentCarRepository, times(1)).findAll();
        verify(modelMapper, times(1)).map(rentCar, RentCarDto.class);
    }

    @Test
    public void findAvailableCars() {
        List<RentCar> rentCarList = new ArrayList<>();
        RentCar rentCar = new RentCar();
        RentCarDto rentedCarDto = new RentCarDto();
        List<Car> carList = new ArrayList<>();
        Car rentedCar = new Car();
        Car availableCar = new Car();
        CarDto availableCarDto = new CarDto();
        CarDto onRentCarDto = new CarDto();

        onRentCarDto.setId(1L);
        rentedCarDto.setId(1L);
        rentedCarDto.setCarDto(onRentCarDto);
        rentCarList.add(rentCar);
        rentedCar.setId(1L);
        availableCar.setId(2L);
        carList.add(rentedCar);
        carList.add(availableCar);
        when(rentCarRepository.findAll()).thenReturn(rentCarList);
        when(modelMapper.map(rentCar, RentCarDto.class)).thenReturn(rentedCarDto);
        when(carRepository.findAll()).thenReturn(carList);
        when(modelMapper.map(availableCar, CarDto.class)).thenReturn(availableCarDto);
        assertEquals(rentCarService.findAvailableCars().size(), 1L);
        verify(rentCarRepository, times(1)).findAll();
        verify(modelMapper, times(1)).map(rentCar, RentCarDto.class);
        verify(carRepository, times(1)).findAll();
        verify(modelMapper, times(1)).map(availableCar, CarDto.class);
    }

    @Test
    public void getRentHistory() {
        List<RentCar> rentCarList = new ArrayList<>();
        RentCar rentCarWithinRange = new RentCar();
        RentCar rentCarNotWithinRange = new RentCar();
        RentCarDto rentCarDtoWithinRange = new RentCarDto();

        rentCarWithinRange.setStartDateTime(LocalDateTime.now(clock));
        rentCarWithinRange.setEndDateTime(LocalDateTime.now(clock));
        rentCarDtoWithinRange.setStartDateTime(LocalDateTime.now(clock).plusDays(1));
        rentCarDtoWithinRange.setEndDateTime(LocalDateTime.now(clock).plusDays(2));
        rentCarList.add(rentCarWithinRange);
        rentCarList.add(rentCarNotWithinRange);
        when(rentCarRepository.findAll()).thenReturn(rentCarList);
        when(modelMapper.map(rentCarWithinRange, RentCarDto.class)).thenReturn(rentCarDtoWithinRange);
        assertEquals(rentCarService.getRentHistoryDto(LocalDate.now(clock), LocalDate.now(clock)).size(), 1L);
        verify(rentCarRepository, times(1)).findAll();
        verify(modelMapper, times(1)).map(rentCarWithinRange, RentCarDto.class);
    }
}
