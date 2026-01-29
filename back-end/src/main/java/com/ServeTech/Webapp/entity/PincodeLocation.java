package com.ServeTech.Webapp.entity;

import jakarta.persistence.*;

// This db table stores pincode-location mappings
// This will help to send jobs to clients based on their location
@Entity
@Table(
        name = "pincode_location",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "pincode")
        }
)
public class PincodeLocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 6, unique = true)
    private String pincode;

    @Column(nullable = false, length = 100)
    private String city;

    @Column(nullable = false, length = 100)
    private String state;

    @Column(length = 100)
    private String district;

    // Constructors
    public PincodeLocation() {
    }

    public PincodeLocation(String pincode, String city, String state, String district) {
        this.pincode = pincode;
        this.city = city;
        this.state = state;
        this.district = district;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    @Override
    public String toString() {
        return "PincodeLocation{" +
                "id=" + id +
                ", pincode='" + pincode + '\'' +
                ", city='" + city + '\'' +
                ", state='" + state + '\'' +
                ", district='" + district + '\'' +
                '}';
    }
}