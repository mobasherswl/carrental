package com.epam.server.service;

import com.epam.common.dto.CarDto;
import com.epam.server.model.Car;
import com.epam.server.repo.CarRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.dao.DataIntegrityViolationException;

import java.lang.reflect.Type;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class FleetServiceImplTest {
    @InjectMocks
    private FleetServiceImpl fleetService;
    @Mock
    private CarRepository carRepository;
    @Mock
    private ModelMapper modelMapper;
    private Type carDtoType;
    private Type carType;

    @Before
    public void init() {
        carDtoType = new TypeToken<List<CarDto>>() {
        }.getType();
        carType = new TypeToken<List<Car>>() {
        }.getType();
    }

    @Test
    public void addCar() {
        CarDto newCarDto = mock(CarDto.class);
        CarDto savedCarDto = mock(CarDto.class);
        Car car = mock(Car.class);

        when(savedCarDto.getId()).thenReturn(1L);
        when(modelMapper.map(newCarDto, Car.class)).thenReturn(car);
        when(carRepository.save(car)).thenReturn(car);
        when(modelMapper.map(car, CarDto.class)).thenReturn(savedCarDto);
        assertEquals(fleetService.add(newCarDto).getId().longValue(), 1L);
        verify(modelMapper, times(1)).map(newCarDto, Car.class);
        verify(modelMapper, times(1)).map(car, CarDto.class);
        verify(carRepository, times(1)).save(car);
    }

    @Test(expected = DataIntegrityViolationException.class)
    public void addCarWithSameRegistration() {
        CarDto newCarDto = mock(CarDto.class);
        CarDto savedCarDto = mock(CarDto.class);
        Car car = mock(Car.class);

        when(modelMapper.map(newCarDto, Car.class)).thenReturn(car);
        when(modelMapper.map(car, CarDto.class)).thenReturn(savedCarDto);
        when(carRepository.save(car)).thenThrow(DataIntegrityViolationException.class);
        fleetService.add(newCarDto);
        verify(modelMapper, times(1)).map(newCarDto, Car.class);
        verify(modelMapper, times(0)).map(car, CarDto.class);
        verify(carRepository, times(1)).save(car);
    }

    @Test
    public void addCars() {
        List<CarDto> newCarDtoList = mock(List.class);
        List<CarDto> savedCarDtoList = mock(List.class);
        List<Car> carList = mock(List.class);

        when(savedCarDtoList.size()).thenReturn(1);
        when(modelMapper.map(newCarDtoList, carType)).thenReturn(carList);
        when(carRepository.save(carList)).thenReturn(carList);
        when(modelMapper.map(carList, carDtoType)).thenReturn(savedCarDtoList);
        assertEquals(fleetService.add(newCarDtoList).size(), 1);
        verify(modelMapper, times(1)).map(newCarDtoList, carType);
        verify(modelMapper, times(1)).map(carList, carDtoType);
        verify(carRepository, times(1)).save(carList);
    }

    @Test(expected = DataIntegrityViolationException.class)
    public void addCarsWithSameRegistration() {
        List<CarDto> newCarDtoList = mock(List.class);
        List<CarDto> savedCarDtoList = mock(List.class);
        List<Car> carList = mock(List.class);

        when(modelMapper.map(newCarDtoList, carType)).thenReturn(carList);
        when(carRepository.save(carList)).thenThrow(DataIntegrityViolationException.class);
        when(modelMapper.map(carList, carDtoType)).thenReturn(savedCarDtoList);
        assertEquals(fleetService.add(newCarDtoList).size(), 1);
        verify(modelMapper, times(1)).map(newCarDtoList, carType);
        verify(modelMapper, times(0)).map(carList, carDtoType);
        verify(carRepository, times(1)).save(carList);
    }

    @Test
    public void searchAllCars() {
        List<CarDto> searchedCarDtoList = mock(List.class);
        List<Car> searchedCarList = mock(List.class);

        when(searchedCarDtoList.size()).thenReturn(1);
        when(carRepository.findAll()).thenReturn(searchedCarList);
        when(modelMapper.map(searchedCarList, carDtoType)).thenReturn(searchedCarDtoList);
        assertEquals(fleetService.findAll().size(), 1);
        verify(modelMapper, times(1)).map(searchedCarList, carDtoType);
        verify(carRepository, times(1)).findAll();
    }
}
