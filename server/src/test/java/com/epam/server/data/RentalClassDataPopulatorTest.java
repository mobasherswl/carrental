package com.epam.server.data;

import com.epam.common.dto.RentalClassDto;
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
import java.util.List;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class RentalClassDataPopulatorTest {
    @InjectMocks
    private RentalClassDataPopulator rentalClassDataPopulator;
    @Mock
    private RentalClassService rentalClassService;
    @Mock
    private File file;
    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private TypeReference<List<RentalClassDto>> rentalClassDtoListTypeReference;

    @Test
    public void populate() throws IOException {
        List<RentalClassDto> rentalClassDtoList = mock(List.class);

        when(objectMapper.readValue(file, rentalClassDtoListTypeReference)).thenReturn(rentalClassDtoList);
        when(rentalClassService.add(rentalClassDtoList)).thenReturn(rentalClassDtoList);
        rentalClassDataPopulator.populate();
        verify(objectMapper, times(1)).readValue(file, rentalClassDtoListTypeReference);
        verify(rentalClassService, times(1)).add(rentalClassDtoList);
    }

    @Test(expected = RuntimeException.class)
    public void populateWithException() throws IOException {
        List<RentalClassDto> rentalClassDtoList = mock(List.class);
        RentalClassDto rentalClassDto = mock(RentalClassDto.class);

        when(objectMapper.readValue(file, rentalClassDtoListTypeReference)).thenThrow(IOException.class);
        when(rentalClassService.add(rentalClassDtoList)).thenReturn(rentalClassDtoList);
        rentalClassDataPopulator.populate();
        verify(objectMapper, times(1)).readValue(file, rentalClassDtoListTypeReference);
        verify(rentalClassService, times(0)).add(rentalClassDtoList);
    }
}
