package com.epam.server.model;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table
public class RentalClass {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(length = 30, unique = true, nullable = false)
    private String name;
    @Column(precision = 6, scale = 2, nullable = false)
    private BigDecimal rate;

    public RentalClass() {
    }

    public RentalClass(String name) {
        this.name = name;
    }

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

    public BigDecimal getRate() {
        return rate;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RentalClass that = (RentalClass) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (!name.equals(that.name)) return false;
        return rate != null ? rate.equals(that.rate) : that.rate == null;

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + name.hashCode();
        result = 31 * result + (rate != null ? rate.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "RentalClass{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", rate=" + rate +
                '}';
    }
}
