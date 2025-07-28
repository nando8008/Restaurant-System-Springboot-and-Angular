package com.example.restaurant.Service;

import com.example.restaurant.Entity.Customer;
import com.example.restaurant.Repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomerService {
    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;


    public Customer register(Customer customer) {
        long count = customerRepository.count();
        customer.setId("c" + (count + 1));
        customer.setPassword(passwordEncoder.encode(customer.getPassword()));
        return customerRepository.save(customer);
    }
    public Optional<Customer> getCustomerById(String id) {
        return customerRepository.findById(id);
    }
}
