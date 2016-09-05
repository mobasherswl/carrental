package com.epam.common.dto;

import java.io.Serializable;
import java.util.Objects;

/**
 * Created by Ahmed_Khan on 5/2/2016.
 */
public class CustomerDto implements Serializable {
    private Long id;
    private String name;
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CustomerDto that = (CustomerDto) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(name, that.name) &&
                Objects.equals(email, that.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, email);
    }

    @Override
    public String toString() {
        return "CustomerDto{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                '}';
    }

    public static final class CustomerDtoBuilder {
        private CustomerDto customerDto;

        {
            customerDto = new CustomerDto();
        }

        public CustomerDtoBuilder setId(Long id) {
            customerDto.setId(id);
            return this;
        }

        public CustomerDtoBuilder setName(String name) {
            customerDto.setName(name);
            return this;
        }

        public CustomerDtoBuilder setEmail(String email) {
            customerDto.setEmail(email);
            return this;
        }

        public CustomerDto build() {
            return customerDto;
        }
    }

}
