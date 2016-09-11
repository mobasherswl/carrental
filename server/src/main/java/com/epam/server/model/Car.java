package com.epam.server.model;

import javax.persistence.*;

/**
 * Created by Ahmed_Khan on 5/10/2016.
 */
@Entity
@Table
public class Car {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private Long id;
    @Column(nullable = false, length = 50)
    private String model;
    @Column(unique = true, nullable = false, length = 20)
    private String registration;
    @ManyToOne
    @JoinColumn(nullable = false)
    private RentalClass rentalClass;

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
    public String toString() {
        return "Car{" +
                "id=" + id +
                ", model='" + model + '\'' +
                ", registration='" + registration + '\'' +
                ", rentalClass=" + rentalClass +
                '}';
    }

    public RentalClass getRentalClass() {
        return rentalClass;
    }

    public void setRentalClass(RentalClass rentalClass) {
        this.rentalClass = rentalClass;
    }

    public static class CarBuilder {
        private Car car;

        {
            car = new Car();
        }

        public CarBuilder setId(Long id) {
            car.setId(id);
            return this;
        }

        public CarBuilder setModel(String model) {
            car.setModel(model);
            return this;
        }

        public CarBuilder setRegistration(String registration) {
            car.setRegistration(registration);
            return this;
        }

        public CarBuilder setRentalClass(RentalClass rentalClass) {
            car.setRentalClass(rentalClass);
            return this;
        }

        public Car build() {
            return car;
        }

    }

}
