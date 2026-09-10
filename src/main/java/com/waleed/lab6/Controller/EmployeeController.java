package com.waleed.lab6.Controller;

import com.waleed.lab6.Entity.Employee;
import com.waleed.lab6.Response.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/employees")
public class EmployeeController {

    private List<Employee> employees = new ArrayList<>(List.of(
        new Employee("EMP001", "Waleed Alaql", "waleed.alaql@gmail.com", "0532188888", 28, "coordinator", false, LocalDate.of(2021, 1, 1), 10),
        new Employee("EMP002", "Abdullah Alaql", "abdullah.alaql@gmail.com", "0532199999", 30, "coordinator", false, LocalDate.of(2021, 1, 1), 10)
     ));

    // Get all employees
    @GetMapping("/all")
    public ResponseEntity<ApiResponse> getAllEmployees() {
        ApiResponse response = new ApiResponse("All employees retrieved successfully", employees);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    // Add a new employee
    @PostMapping("/add")
    public ResponseEntity<ApiResponse> addEmployee(@Valid @RequestBody Employee newEmployee, Errors errors) {
        if (errors.hasErrors()) {
            String errorMessage = errors.getFieldError().getDefaultMessage();
            ApiResponse response = new ApiResponse(errorMessage, null);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        employees.add(newEmployee);
        ApiResponse response = new ApiResponse("Employee added successfully", newEmployee);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Update an employee
    @PutMapping("/update/{id}")
    public ResponseEntity<ApiResponse> updateEmployee(@PathVariable String id, @Valid @RequestBody Employee updatedData, Errors errors) {
        if (errors.hasErrors()) {
            String errorMessage = errors.getFieldError().getDefaultMessage();
            ApiResponse response = new ApiResponse(errorMessage, null);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        for (Employee e : employees) {
            if (e.getId().equals(id)) {
                e.setName(updatedData.getName());
                e.setEmail(updatedData.getEmail());
                e.setPhoneNumber(updatedData.getPhoneNumber());
                e.setAge(updatedData.getAge());
                e.setPosition(updatedData.getPosition());
                e.setOnLeave(updatedData.isOnLeave());
                e.setHireDate(updatedData.getHireDate());
                e.setAnnualLeave(updatedData.getAnnualLeave());

                ApiResponse response = new ApiResponse("Employee updated successfully", e);
                return ResponseEntity.status(HttpStatus.OK).body(response);
            }
        }
        ApiResponse response = new ApiResponse("Employee not found for update", null);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    // Delete an employee
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResponse> deleteEmployee(@PathVariable String id) {
        boolean removed = employees.removeIf(e -> e.getId().equals(id));
        if (removed) {
                ApiResponse response = new ApiResponse("Employee with id " + "'" + id + "'" + " deleted successfully", null);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } else {
            ApiResponse response = new ApiResponse("Employee with id " + "'" + id + "'" + " not found to delete", null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    // Search Employees by Position
    @GetMapping("/search/position/{position}")
    public ResponseEntity<ApiResponse> getEmployeesByPosition(@PathVariable String position) {
        if (!position.equalsIgnoreCase("supervisor") && !position.equalsIgnoreCase("coordinator")) {
            ApiResponse response = new ApiResponse("Invalid position parameter. Must be 'supervisor' or 'coordinator'", null);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        List<Employee> result = new ArrayList<>();
        for (Employee e : employees) {
            if (e.getPosition().equalsIgnoreCase(position)) {
                result.add(e);
            }
        }

        if (result.isEmpty()) {
            ApiResponse response = new ApiResponse("No employees found with position '" + position + "'", null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        ApiResponse response = new ApiResponse("Employees with position '" + position + "' retrieved successfully", result);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    // Get Employees by Age Range
    @GetMapping("/search/age-range/{minAge}/{maxAge}")
    public ResponseEntity<ApiResponse> getEmployeesByAgeRange(@PathVariable Integer minAge, @PathVariable Integer maxAge) {
        if (minAge < 0 || maxAge < 0 || minAge > maxAge) {
            ApiResponse response = new ApiResponse("Invalid age range values", null);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        List<Employee> result = new ArrayList<>();
        for (Employee e : employees) {
            if (e.getAge() >= minAge && e.getAge() <= maxAge) {
                result.add(e);
            }
        }

        if (result.isEmpty()) {
            ApiResponse response = new ApiResponse("No employees found within the age range " + minAge + " to " + maxAge, null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        ApiResponse response = new ApiResponse("Employees with age range " + minAge + " to " + maxAge + " retrieved successfully", result);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    // Apply for annual leave
    @PutMapping("/apply/leave/{id}")
    public ResponseEntity<ApiResponse> applyAnnualLeave(@PathVariable String id) {
        for (Employee e : employees) {
            if (e.getId().equals(id)) {
                if (e.isOnLeave()) {
                    ApiResponse response = new ApiResponse("Employee is already on leave", e);
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
                }
                if (e.getAnnualLeave() <= 0) {
                    ApiResponse response = new ApiResponse("Employee has no annual leave remaining", e);
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
                }

                e.setOnLeave(true);
                e.setAnnualLeave(e.getAnnualLeave() - 1);

                ApiResponse response = new ApiResponse("Annual leave applied successfully", e);
                return ResponseEntity.status(HttpStatus.OK).body(response);
            }
        }
        ApiResponse response = new ApiResponse("Employee not found", null);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    // Get Employees with No Annual Leave
    @GetMapping("/search/no-leave")
    public ResponseEntity<ApiResponse> getEmployeesWithNoAnnualLeave() {
        List<Employee> result = new ArrayList<>();
        for (Employee e : employees) {
            if (e.getAnnualLeave() == 0) {
                result.add(e);
            }
        }

        if (result.isEmpty()) {
            ApiResponse response = new ApiResponse("No employees found with zero annual leave", null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        ApiResponse response = new ApiResponse("Employees with no annual leave retrieved successfully", result);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    // Promote Employee
    @PutMapping("/promote/{id}/{requesterId}")
    public ResponseEntity<ApiResponse> promoteEmployee(@PathVariable String id, @PathVariable String requesterId) {

        Employee requester = null;
        for (Employee e : employees) {
            if (e.getId().equals(requesterId)) {
                requester = e;
                break;
            }
        }

        if (requester == null || !requester.getPosition().equalsIgnoreCase("supervisor")) {
            ApiResponse response = new ApiResponse("Unauthorized: Only a supervisor can promote employees", null);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }

        for (Employee e : employees) {
            if (e.getId().equals(id)) {
                // I've added a new logic to check if the employee is already a supervisor it will return a 400 status code
                // That says [Employee is already a supervisor]
                if (e.getPosition().equalsIgnoreCase("supervisor")) {
                    ApiResponse response = new ApiResponse("Employee is already a supervisor", e);
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
                }
                if (e.getAge() < 30) {
                    ApiResponse response = new ApiResponse("Employee age must be at least 30 years to promote", e);
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
                }
                if (e.isOnLeave()) {
                    ApiResponse response = new ApiResponse("Cannot promote an employee who is currently on leave", e);
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
                }

                e.setPosition("supervisor");
                ApiResponse response = new ApiResponse("Employee promoted to supervisor successfully", e);
                return ResponseEntity.status(HttpStatus.OK).body(response);
            }
        }

        ApiResponse response = new ApiResponse("Target employee not found", null);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
}