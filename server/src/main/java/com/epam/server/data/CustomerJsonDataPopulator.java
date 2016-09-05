package com.epam.server.data;

import com.epam.common.dto.CustomerDto;
import com.epam.common.service.CustomerService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.util.List;

@Component
public class CustomerJsonDataPopulator implements DataPopulator {
    private static final Logger logger = LoggerFactory.getLogger(CustomerJsonDataPopulator.class);
    private CustomerService customerService;
    private File file;
    private ObjectMapper objectMapper;
    private TypeReference<List<CustomerDto>> customerDtoListTypeReference;

    @Autowired
    public CustomerJsonDataPopulator(CustomerService customerService, @Qualifier("customersJsonFile") File file, ObjectMapper objectMapper, TypeReference<List<CustomerDto>> customerDtoListTypeReference) {
        this.customerService = customerService;
        this.file = file;
        this.objectMapper = objectMapper;
        this.customerDtoListTypeReference = customerDtoListTypeReference;
    }

    @PostConstruct
    @Override
    public void populate() {
        try {
            List<CustomerDto> customerDtoList = objectMapper.readValue(
                    file,
                    customerDtoListTypeReference
            );
            customerService.add(customerDtoList);
        } catch (IOException e) {
            logger.error("Customer JSON file processing failed", e);
            throw new RuntimeException("Customer JSON file processing failed", e);
        }
    }
}
