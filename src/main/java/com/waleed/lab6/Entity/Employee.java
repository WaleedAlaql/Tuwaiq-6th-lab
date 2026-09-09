package com.waleed.lab6.Entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Employee {

    @NotNull(message = "ID cannot be null")
    @Size(min = 3, message = "ID length must be more than 2 characters")
    private String id;

    @NotNull(message = "Name cannot be null")
    @Size(min = 5, message = "Name length must be more than 4 characters")
    @Pattern(regexp = "^[a-zA-Z\\s]+$", message = "Name must contain only characters (no numbers)")
    private String name;

    @Email(message = "Must be a valid email format")
    private String email;

    @NotNull(message = "Phone number cannot be null")
    @Pattern(regexp = "^05\\d{8}$", message = "Phone number must start with '05' and consist of 10 digits")
    private String phoneNumber;

    @NotNull(message = "Age cannot be null")
    @Min(value = 26, message = "Age must be more than 25")
    private Integer age;

    @NotNull(message = "Position cannot be null")
    @Pattern(regexp = "supervisor|coordinator", message = "Position must be either 'supervisor' or 'coordinator' only")
    private String position;

    @AssertFalse()
    private boolean onLeave = false;

    @NotNull(message = "HireDate cannot be null")
    @PastOrPresent(message = "hireDate should be a date in the present or the past")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate hireDate;

    @NotNull(message = "AnnualLeave cannot be null")
    @Positive(message = "AnnualLeave must be a positive number")
    private Integer annualLeave;
}