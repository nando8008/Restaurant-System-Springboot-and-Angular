package com.example.restaurant.Controller;

import com.example.restaurant.Entity.Employee;
import com.example.restaurant.Service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/employees")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @PostMapping("/register")
    public ResponseEntity<Employee> register(@RequestBody Employee employee) {
        Employee saved = employeeService.register(employee);
        return ResponseEntity.ok(saved);
    }
}