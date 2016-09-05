package com.epam.server.data;

import com.epam.common.dto.CustomerDto;
import com.epam.common.service.CustomerService;
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

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CustomerJsonDataPopulatorTest {
    @InjectMocks
    private CustomerJsonDataPopulator customerJsonDataPopulator;
    @Mock
    private CustomerService customerService;
    @Mock
    private File file;
    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private TypeReference<List<CustomerDto>> customerDtoListTypeReference;

    @Test
    public void populate() throws IOException {
        List<CustomerDto> customerDtoList = mock(List.class);

        when(objectMapper.readValue(file, customerDtoListTypeReference)).thenReturn(customerDtoList);
        when(customerService.add(customerDtoList)).thenReturn(customerDtoList);
        customerJsonDataPopulator.populate();
        verify(objectMapper, times(1)).readValue(file, customerDtoListTypeReference);
        verify(customerService, times(1)).add(customerDtoList);

    }

    @Test(expected = RuntimeException.class)
    public void populateWithException() throws IOException {
        List<CustomerDto> customerDtoList = mock(List.class);

        when(objectMapper.readValue(file, customerDtoListTypeReference)).thenThrow(IOException.class);
        when(customerService.add(customerDtoList)).thenReturn(customerDtoList);
        customerJsonDataPopulator.populate();
        verify(objectMapper, times(1)).readValue(file, customerDtoListTypeReference);
        verify(customerService, times(0)).add(customerDtoList);

    }
}
