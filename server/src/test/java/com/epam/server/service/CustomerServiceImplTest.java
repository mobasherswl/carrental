package com.epam.server.service;

import com.epam.common.dto.CustomerDto;
import com.epam.server.model.Customer;
import com.epam.server.repo.CustomerRepository;
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
public class CustomerServiceImplTest {
    @InjectMocks
    private CustomerServiceImpl customerService;
    @Mock
    private ModelMapper modelMapper;
    @Mock
    private CustomerRepository customerRepository;
    private Type customerDtoType;
    private Type customerType;

    @Before
    public void init() {
        customerDtoType = new TypeToken<List<CustomerDto>>() {
        }.getType();
        customerType = new TypeToken<List<Customer>>() {
        }.getType();
    }

    @Test
    public void createCustomer() {
        CustomerDto newCustomerDto = mock(CustomerDto.class);
        CustomerDto savedCustomerDto = mock(CustomerDto.class);
        Customer customer = mock(Customer.class);
        when(savedCustomerDto.getId()).thenReturn(1L);
        when(modelMapper.map(newCustomerDto, Customer.class)).thenReturn(customer);
        when(customerRepository.save(customer)).thenReturn(customer);
        when(modelMapper.map(customer, CustomerDto.class)).thenReturn(savedCustomerDto);
        assertEquals(customerService.add(newCustomerDto).getId().longValue(), 1L);
        verify(modelMapper, times(1)).map(newCustomerDto, Customer.class);
        verify(modelMapper, times(1)).map(customer, CustomerDto.class);
        verify(customerRepository, times(1)).save(customer);
    }

    @Test(expected = DataIntegrityViolationException.class)
    public void createCustomerWithSameEmailAddresses() {
        CustomerDto newCustomerDto = mock(CustomerDto.class);
        CustomerDto savedCustomerDto = mock(CustomerDto.class);
        Customer customer = mock(Customer.class);
        when(modelMapper.map(newCustomerDto, Customer.class)).thenReturn(customer);
        when(modelMapper.map(customer, CustomerDto.class)).thenReturn(savedCustomerDto);
        when(customerRepository.save(customer)).thenThrow(DataIntegrityViolationException.class);
        customerService.add(newCustomerDto);
        verify(modelMapper, times(1)).map(newCustomerDto, Customer.class);
        verify(modelMapper, times(0)).map(customer, CustomerDto.class);
        verify(customerRepository, times(1)).save(customer);
    }

    @Test
    public void createCustomers() {
        List<CustomerDto> newCustomerDtoList = mock(List.class);
        List<CustomerDto> savedCustomerDtoList = mock(List.class);
        List<Customer> customerList = mock(List.class);

        when(savedCustomerDtoList.size()).thenReturn(1);
        when(modelMapper.map(newCustomerDtoList, customerType)).thenReturn(customerList);
        when(customerRepository.save(customerList)).thenReturn(customerList);
        when(modelMapper.map(customerList, customerDtoType)).thenReturn(savedCustomerDtoList);
        assertEquals(customerService.add(newCustomerDtoList).size(), 1);
        verify(modelMapper, times(1)).map(newCustomerDtoList, customerType);
        verify(modelMapper, times(1)).map(customerList, customerDtoType);
        verify(customerRepository, times(1)).save(customerList);
    }

    @Test(expected = DataIntegrityViolationException.class)
    public void createCustomersWithSameEmailAddresses() {
        List<CustomerDto> newCustomerDtoList = mock(List.class);
        List<CustomerDto> savedCustomerDtoList = mock(List.class);
        List<Customer> customerList = mock(List.class);

        when(modelMapper.map(newCustomerDtoList, customerType)).thenReturn(customerList);
        when(customerRepository.save(customerList)).thenThrow(DataIntegrityViolationException.class);
        when(modelMapper.map(customerList, customerDtoType)).thenReturn(savedCustomerDtoList);
        assertEquals(customerService.add(newCustomerDtoList).size(), 1);
        verify(modelMapper, times(1)).map(newCustomerDtoList, customerType);
        verify(modelMapper, times(0)).map(customerList, customerDtoType);
        verify(customerRepository, times(1)).save(customerList);
    }

    @Test
    public void searchAllCustomers() {
        List<CustomerDto> searchedCustomerDtoList = mock(List.class);
        List<Customer> searchedCustomerList = mock(List.class);
        Type customerDtoType = new TypeToken<List<CustomerDto>>() {
        }.getType();

        when(searchedCustomerDtoList.size()).thenReturn(1);
        when(customerRepository.findAll()).thenReturn(searchedCustomerList);
        when(modelMapper.map(searchedCustomerList, customerDtoType)).thenReturn(searchedCustomerDtoList);
        assertEquals(customerService.findAll().size(), 1);
        verify(modelMapper, times(1)).map(searchedCustomerList, customerDtoType);
        verify(customerRepository, times(1)).findAll();
    }
}
