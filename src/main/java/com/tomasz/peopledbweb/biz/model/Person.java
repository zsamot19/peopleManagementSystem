package com.tomasz.peopledbweb.biz.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Person {
    @Id
    @GeneratedValue
    private Long id;
    @NotEmpty(message = "You need to fill the first name field")
    private String firstName;
    @NotEmpty(message = "You need to fill the last name field")
    private String lastName;
    @NotNull(message = "You need to fill the date of birth field")
    @Past(message = "Your date of birth must be in the past")
    private LocalDate dob;
    @Email(message = "Your email must be valid")
    @NotEmpty(message = "Your emil cannot be empty")
    private String emial;
    @NotNull(message = "Your salary cannot be empty")
    @DecimalMin(value = "0", message = "Your salary cannot be below zero")
    private BigDecimal salary;

    private String photoFileName;
}




