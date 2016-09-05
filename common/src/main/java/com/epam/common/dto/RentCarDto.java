package com.epam.common.dto;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Created by Ahmed_Khan on 5/13/2016.
 */
public class RentCarDto implements Serializable {
    private Long id;
    private CarDto carDto;
    private CustomerDto customerDto;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private Integer version;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CarDto getCarDto() {
        return carDto;
    }

    public void setCarDto(CarDto carDto) {
        this.carDto = carDto;
    }

    public CustomerDto getCustomerDto() {
        return customerDto;
    }

    public void setCustomerDto(CustomerDto customerDto) {
        this.customerDto = customerDto;
    }

    public LocalDateTime getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(LocalDateTime startDateTime) {
        this.startDateTime = startDateTime;
    }

    public LocalDateTime getEndDateTime() {
        return endDateTime;
    }

    public void setEndDateTime(LocalDateTime endDateTime) {
        this.endDateTime = endDateTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RentCarDto that = (RentCarDto) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(carDto, that.carDto) &&
                Objects.equals(customerDto, that.customerDto) &&
                Objects.equals(startDateTime, that.startDateTime) &&
                Objects.equals(endDateTime, that.endDateTime) &&
                Objects.equals(version, that.version);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, carDto, customerDto, startDateTime, endDateTime, version);
    }

    @Override
    public String toString() {
        return "RentCarDto{" +
                "id=" + id +
                ", carDto=" + carDto +
                ", customerDto=" + customerDto +
                ", startDateTime=" + startDateTime +
                ", endDateTime=" + endDateTime +
                ", version=" + version +
                '}';
    }

    public static final class RentCarDtoBuilder {
        private RentCarDto rentCarDto;

        {
            rentCarDto = new RentCarDto();
        }

        public RentCarDtoBuilder setId(Long id) {
            rentCarDto.setId(id);
            return this;
        }

        public RentCarDtoBuilder setCarDto(CarDto carDto) {
            rentCarDto.setCarDto(carDto);
            return this;
        }

        public RentCarDtoBuilder setCustomerDto(CustomerDto customerDto) {
            rentCarDto.setCustomerDto(customerDto);
            return this;
        }

        public RentCarDto build() {
            return rentCarDto;
        }
    }

}
