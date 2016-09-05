package com.epam.server.model;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Created by Ahmed_Khan on 5/11/2016.
 */
@Entity
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"car_id", "is_car_rented"})})
public class RentCar {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private Customer customer;
    @OneToOne
    private Car car;
    @Column(name = "is_car_rented")
    private Boolean isCarRented;
    @Column
    private LocalDateTime startDateTime;
    @Column
    private LocalDateTime endDateTime;
    @Column
    @Version
    private Integer version;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Car getCar() {
        return car;
    }

    public void setCar(Car car) {
        this.car = car;
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

    public Boolean getCarRented() {
        return isCarRented;
    }

    public void setCarRented(Boolean carRented) {
        isCarRented = carRented;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public static final class RentCarBuilder {
        private RentCar rentCar;

        {
            rentCar = new RentCar();
        }

        public RentCarBuilder setId(Long id) {
            rentCar.setId(id);
            return this;
        }

        public RentCarBuilder setCarDto(Car car) {
            rentCar.setCar(car);
            return this;
        }

        public RentCarBuilder setCustomer(Customer customer) {
            rentCar.setCustomer(customer);
            return this;
        }

        public RentCar build() {
            return rentCar;
        }
    }

}
