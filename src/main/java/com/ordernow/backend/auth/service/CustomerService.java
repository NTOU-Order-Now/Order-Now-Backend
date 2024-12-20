package com.ordernow.backend.auth.service;

import com.ordernow.backend.auth.model.entity.Customer;
import com.ordernow.backend.auth.model.entity.Role;
import com.ordernow.backend.order.repository.OrderRepository;
import com.ordernow.backend.auth.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
public class CustomerService {

    private static final Logger log = LoggerFactory.getLogger(CustomerService.class);
    private final UserRepository userRepository;

    @Autowired
    public CustomerService(UserRepository userRepository, OrderRepository orderRepository) {
        this.userRepository = userRepository;
    }

    public Customer getCustomerById(String customerId) {
        Customer customer = (Customer) userRepository.findById(customerId).orElse(null);
        if (customer == null) {
            throw new NoSuchElementException("Customer not found with ID: " + customerId);
        }
        if(customer.getRole() != Role.CUSTOMER) {
            throw new NoSuchElementException("User is not a customer");
        }
        return customer;
    }
}
