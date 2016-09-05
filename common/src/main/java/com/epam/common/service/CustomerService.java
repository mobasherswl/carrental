package com.epam.common.service;

import com.epam.common.dto.CustomerDto;

import java.util.List;

/**
 * Created by Ahmed_Khan on 5/2/2016.
 */
public interface CustomerService {

    CustomerDto add(CustomerDto customerDto);

    List<CustomerDto> add(List<CustomerDto> customerDtoList);

    List<CustomerDto> findAll();
}
