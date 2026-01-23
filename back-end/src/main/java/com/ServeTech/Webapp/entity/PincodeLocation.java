package com.ServeTech.Webapp.entity;

import jakarta.persistence.*;

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

    @Column(nullable = false, length = 6)
    private String pincode;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private String state;
}
