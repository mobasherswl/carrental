package com.epam.server.model;

import javax.persistence.*;

/**
 * Created by Ahmed_Khan on 5/2/2016.
 */
@Entity
@Table
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(nullable = false, length = 30)
    private String name;
    @Column(unique = true, nullable = false, length = 50)
    private String email;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                '}';
    }

    public static final class CustomerBuilder {
        private Customer customer;

        {
            customer = new Customer();
        }

        public CustomerBuilder setId(Long id) {
            customer.setId(id);
            return this;
        }

        public CustomerBuilder setName(String name) {
            customer.setName(name);
            return this;
        }

        public CustomerBuilder setEmail(String email) {
            customer.setEmail(email);
            return this;
        }

        public Customer build() {
            return customer;
        }
    }

}
