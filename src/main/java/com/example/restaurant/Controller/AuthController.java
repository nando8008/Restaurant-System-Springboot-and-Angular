package com.example.restaurant.Controller;

import com.example.restaurant.Config.JwtUtil;
import com.example.restaurant.DTO.JwtResponseDto;
import com.example.restaurant.DTO.LoginRequestDto;
import com.example.restaurant.Entity.Customer;
import com.example.restaurant.Entity.Employee;
import com.example.restaurant.Repository.CustomerRepository;
import com.example.restaurant.Repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDto request) {
        Optional<Employee> employeeOpt = employeeRepository.findByUsername(request.getUsername());
        if (employeeOpt.isPresent()) {
            Employee employee = employeeOpt.get();
            if (passwordEncoder.matches(request.getPassword(), employee.getPassword())) {
                String token = jwtUtil.generateToken(employee.getUsername(),employee.getRole().name(),employee.getUsername());

                JwtResponseDto response = new JwtResponseDto();
                response.setToken(token);
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(401).body("❌ Invalid password");
            }
        }

        Optional<Customer> customerOpt = customerRepository.findByEmail(request.getUsername());
        if (customerOpt.isPresent()) {
            Customer customer = customerOpt.get();
            if (passwordEncoder.matches(request.getPassword(), customer.getPassword())) {
                String token = jwtUtil.generateToken(customer.getEmail(), "CUSTOMER",customer.getId());

                JwtResponseDto response = new JwtResponseDto();
                response.setToken(token);
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(401).body("❌ Invalid password");
            }
        }

        return ResponseEntity.status(401).body("❌ User not found");
    }
}
