package com.example.restaurant.Service;

import com.example.restaurant.Entity.Customer;
import com.example.restaurant.Entity.Employee;
import com.example.restaurant.Repository.CustomerRepository;
import com.example.restaurant.Repository.EmployeeRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Employee> empOpt = employeeRepository.findByUsername(username);
        if (empOpt.isPresent()) {
            Employee employee = empOpt.get();
            return new User(
                    employee.getUsername(),
                    employee.getPassword(),
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + employee.getRole().name()))
            );
        }

        Optional<Customer> custOpt = customerRepository.findByEmail(username);
        if (custOpt.isPresent()) {
            Customer customer = custOpt.get();
            return new User(
                    customer.getEmail(),
                    customer.getPassword(),
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_CUSTOMER"))
            );
        }

        throw new UsernameNotFoundException("User not found: " + username);
    }
}
