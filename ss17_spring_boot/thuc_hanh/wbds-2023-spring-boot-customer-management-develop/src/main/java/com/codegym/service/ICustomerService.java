package com.codegym.service;

import com.codegym.model.Customer;

import java.util.Optional;

public interface ICustomerService  {
    Iterable<Customer> findAll();

    Optional<Customer> findById(Long id);

    void save(Customer t);

    void remove(Long id);
}
