package com.epam.server.data;

import com.epam.common.dto.CarDto;
import com.epam.common.dto.CustomerDto;
import com.epam.common.dto.RentCarDto;
import com.epam.common.service.CustomerService;
import com.epam.common.service.FleetService;
import com.epam.common.service.RentCarService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Component
@DependsOn({"carJsonDataPopulator", "customerJsonDataPopulator"})
public class RentCarDataPopulator implements DataPopulator {
    private static final Logger logger = LoggerFactory.getLogger(RentCarDataPopulator.class);
    private CustomerService customerService;
    private FleetService fleetService;
    private RentCarService rentCarServiceWithStartDateTime;
    private RentCarService rentCarServiceWithEndDateTime;
    private List<RentCarDto> rentCarDtoList;

    @Autowired
    public RentCarDataPopulator(CustomerService customerService,
                                FleetService fleetService,
                                @Qualifier("rentCarServiceWithStartDateTime")
                                        RentCarService rentCarServiceWithStartDateTime,
                                @Qualifier("rentCarServiceWithEndDateTime")
                                        RentCarService rentCarServiceWithEndDateTime,
                                @Qualifier("rentCarDtoJsonData") ArrayList<RentCarDto> rentCarDtoList) {
        this.customerService = customerService;
        this.fleetService = fleetService;
        this.rentCarServiceWithStartDateTime = rentCarServiceWithStartDateTime;
        this.rentCarServiceWithEndDateTime = rentCarServiceWithEndDateTime;
        this.rentCarDtoList = rentCarDtoList;
    }

    @PostConstruct
    @Override
    public void populate() {
        List<CustomerDto> customerDtoList = customerService.findAll();
        List<CarDto> carDtoList = fleetService.findAll();

        for (int i = 0; i < rentCarDtoList.size(); i++) {
            RentCarDto rentCarDto = rentCarDtoList.get(i);
            rentCarDto.setCarDto(carDtoList.get(i));
            rentCarDto.setCustomerDto(customerDtoList.get(i));
            rentCarDtoList.set(i,
                    rentCarServiceWithEndDateTime.rentCarReturn(rentCarServiceWithStartDateTime.rentOut(rentCarDto)));
        }
    }
}
