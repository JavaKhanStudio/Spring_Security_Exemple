package com.security.entity;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "validation_example")
public class ValidationExample {

    // Identifiant fourni par le client (le DTO exige @NotNull sur id) : clé primaire assignée.
    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "username")
    private String username;

    @Column(name = "email")
    private String email;

    @Column(name = "age")
    private Integer age;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "agreed_to_terms")
    private boolean agreedToTerms;

    @Column(name = "birthdate")
    private LocalDate birthdate;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "validation_example_roles",
            joinColumns = @JoinColumn(name = "validation_example_id")
    )
    @Column(name = "role")
    private List<String> roles;
}
