package com.epam.server.data;

import com.epam.common.dto.RentalClassDto;
import com.epam.common.service.RentalClassService;
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
public class RentalClassDataPopulator implements DataPopulator {
    private static final Logger logger = LoggerFactory.getLogger(RentalClassDataPopulator.class);
    private RentalClassService rentalClassService;
    private File file;
    private ObjectMapper objectMapper;
    private TypeReference<List<RentalClassDto>> rentalClassDtoListTypeReference;

    @Autowired
    public RentalClassDataPopulator(RentalClassService rentalClassService, @Qualifier("rentalClassJsonFile") File file, ObjectMapper objectMapper, TypeReference<List<RentalClassDto>> rentalClassDtoListTypeReference) {
        this.rentalClassService = rentalClassService;
        this.file = file;
        this.objectMapper = objectMapper;
        this.rentalClassDtoListTypeReference = rentalClassDtoListTypeReference;
    }

    @PostConstruct
    @Override
    public void populate() {
        try {
            List<RentalClassDto> rentalClassDtoList = objectMapper.readValue(
                    file,
                    rentalClassDtoListTypeReference
            );
            rentalClassService.add(rentalClassDtoList);
        } catch (IOException e) {
            logger.error("Rental Class JSON file processing failed", e);
            throw new RuntimeException("Rental Class JSON file processing failed", e);
        }
    }
}
