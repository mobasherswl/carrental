package com.epam.server.service;

import com.epam.common.dto.CustomerDto;
import com.epam.server.model.Customer;
import com.epam.server.repo.BaseRepository;
import com.epam.common.service.CustomerService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by Ahmed_Khan on 5/2/2016.
 */
@Service
public class CustomerServiceImpl implements CustomerService {

    private Type customerDtoType = new TypeToken<List<CustomerDto>>() {
    }.getType();
    private Type customerType = new TypeToken<List<Customer>>() {
    }.getType();
    private BaseRepository<Customer, Long> customerRepository;
    private ModelMapper modelMapper;

    @Autowired
    public CustomerServiceImpl(BaseRepository<Customer, Long> customerRepository, ModelMapper modelMapper) {
        this.customerRepository = customerRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public CustomerDto add(CustomerDto customerDto) {
        return modelMapper.map(
                customerRepository.save(
                        modelMapper.map(customerDto, Customer.class)
                ), CustomerDto.class);
    }

    @Override
    public List<CustomerDto> add(List<CustomerDto> list) {
        return modelMapper.map(customerRepository.save((Iterable<Customer>) modelMapper.map(list, customerType)), customerDtoType);
    }

    @Override
    public List<CustomerDto> findAll() {
        return modelMapper.map(customerRepository.findAll(), customerDtoType);
    }
}
