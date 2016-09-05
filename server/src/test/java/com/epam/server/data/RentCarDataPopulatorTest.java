package com.epam.server.data;

import com.epam.common.dto.CarDto;
import com.epam.common.dto.CustomerDto;
import com.epam.common.dto.RentCarDto;
import com.epam.common.service.CustomerService;
import com.epam.common.service.FleetService;
import com.epam.common.service.RentCarService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class RentCarDataPopulatorTest {
    private RentCarDataPopulator rentCarDataPopulator;
    @Mock
    private CustomerService customerService;
    @Mock
    private FleetService fleetService;
    @Mock
    private RentCarService rentCarServiceWithStartDateTime;
    @Mock
    private RentCarService rentCarServiceWithEndDateTime;
    @Mock
    private ArrayList<RentCarDto> rentCarDtoList;

    @Before
    public void init() {
        rentCarDataPopulator = new RentCarDataPopulator(customerService, fleetService, rentCarServiceWithStartDateTime, rentCarServiceWithEndDateTime, rentCarDtoList);
    }

    @Test
    public void populate() {
        List<CustomerDto> customerDtoList = mock(List.class);
        List<CarDto> carDtoList = mock(List.class);
        RentCarDto rentCarDto = mock(RentCarDto.class);
        CarDto carDto = mock(CarDto.class);
        CustomerDto customerDto = mock(CustomerDto.class);

        rentCarDto.setCarDto(carDto);
        rentCarDto.setCustomerDto(customerDto);
        when(customerService.findAll()).thenReturn(customerDtoList);
        when(fleetService.findAll()).thenReturn(carDtoList);
        when(rentCarDtoList.size()).thenReturn(1);
        when(rentCarDtoList.get(anyInt())).thenReturn(rentCarDto);
        when(carDtoList.get(anyInt())).thenReturn(carDto);
        when(customerDtoList.get(anyInt())).thenReturn(customerDto);
        when(rentCarServiceWithStartDateTime.rentOut(rentCarDto)).thenReturn(rentCarDto);
        when(rentCarServiceWithEndDateTime.rentCarReturn(rentCarDto)).thenReturn(rentCarDto);
        rentCarDataPopulator.populate();
        verify(customerService, times(1)).findAll();
        verify(fleetService, times(1)).findAll();
        verify(rentCarDtoList, times(2)).size();
        verify(rentCarDtoList, times(1)).get(anyInt());
        verify(carDtoList, times(1)).get(anyInt());
        verify(customerDtoList, times(1)).get(anyInt());
        verify(rentCarServiceWithStartDateTime, times(1)).rentOut(rentCarDto);
        verify(rentCarServiceWithEndDateTime, times(1)).rentCarReturn(rentCarDto);
    }
}
