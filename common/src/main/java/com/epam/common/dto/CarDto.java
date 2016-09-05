package com.epam.common.dto;

import java.io.Serializable;
import java.util.Objects;

/**
 * Created by Ahmed_Khan on 5/10/2016.
 */
public class CarDto implements Serializable {
    private Long id;
    private String model;
    private String registration;
    private RentalClassDto rentalClassDto;

    @Override
    public String toString() {
        return "CarDto{" +
                "id=" + id +
                ", model='" + model + '\'' +
                ", registration='" + registration + '\'' +
                ", rentalClassDto=" + rentalClassDto +
                '}';
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getRegistration() {
        return registration;
    }

    public void setRegistration(String registration) {
        this.registration = registration;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CarDto carDto = (CarDto) o;
        return Objects.equals(id, carDto.id) &&
                Objects.equals(model, carDto.model) &&
                Objects.equals(registration, carDto.registration);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, model, registration);
    }

    public RentalClassDto getRentalClassDto() {
        return rentalClassDto;
    }

    public void setRentalClassDto(RentalClassDto rentalClassDto) {
        this.rentalClassDto = rentalClassDto;
    }

    public static final class CarDtoBuilder {
        private CarDto carDto;

        {
            carDto = new CarDto();
        }

        public CarDtoBuilder setId(Long id) {
            carDto.setId(id);
            return this;
        }

        public CarDtoBuilder setModel(String model) {
            carDto.setModel(model);
            return this;
        }

        public CarDtoBuilder setRegistration(String registration) {
            carDto.setRegistration(registration);
            return this;
        }

        public CarDto build() {
            return carDto;
        }
    }
}
