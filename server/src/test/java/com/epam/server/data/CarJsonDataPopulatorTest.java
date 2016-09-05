package com.epam.server.data;

import com.epam.common.dto.CarDto;
import com.epam.common.dto.RentalClassDto;
import com.epam.common.service.FleetService;
import com.epam.common.service.RentalClassService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CarJsonDataPopulatorTest {
    @InjectMocks
    private CarJsonDataPopulator carJsonDataPopulator;
    @Mock
    private FleetService fleetService;
    @Mock
    private File file;
    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private TypeReference<List<CarDto>> carDtoListTypeReference;
    @Mock
    private RentalClassService rentalClassService;

    @Test
    public void populate() throws IOException {
        List<CarDto> carDtoList = new ArrayList<>();
        List<RentalClassDto> rentalClassDtoList = mock(List.class);
        RentalClassDto rentalClassDto = mock(RentalClassDto.class);
        CarDto carDto = mock(CarDto.class);

        carDtoList.add(carDto);
        when(objectMapper.readValue(file, carDtoListTypeReference)).thenReturn(carDtoList);
        when(fleetService.add(carDtoList)).thenReturn(carDtoList);
        when(rentalClassService.findAll()).thenReturn(rentalClassDtoList);
        when(rentalClassDtoList.get(anyInt())).thenReturn(rentalClassDto);
        when(rentalClassDtoList.size()).thenReturn(1);
        carJsonDataPopulator.populate();
        verify(objectMapper, times(1)).readValue(file, carDtoListTypeReference);
        verify(fleetService, times(1)).add(carDtoList);
        verify(rentalClassService, times(1)).findAll();
        verify(rentalClassDtoList, times(1)).size();
        verify(rentalClassDtoList, times(1)).get(anyInt());
    }

    @Test(expected = RuntimeException.class)
    public void populateWithException() throws IOException {
        List<CarDto> carDtoList = mock(List.class);

        when(objectMapper.readValue(file, carDtoListTypeReference)).thenThrow(IOException.class);
        when(fleetService.add(carDtoList)).thenReturn(carDtoList);
        carJsonDataPopulator.populate();
        verify(objectMapper, times(1)).readValue(file, carDtoListTypeReference);
        verify(fleetService, times(0)).add(carDtoList);

    }
}
