package com.pahana.service;

import java.util.List;

import com.pahana.dao.CustomerDAO;
import com.pahana.model.Customer;

public class CustomerService {

    private static volatile CustomerService instance;
    private CustomerDAO customerDAO;

    private CustomerService() {
        this.customerDAO = CustomerDAO.getInstance();
    }

    public static CustomerService getInstance() {
        if (instance == null) {
            synchronized (CustomerService.class) {
                if (instance == null) {
                    instance = new CustomerService();
                }
            }
        }
        return instance;
    }

    public void addCustomer(Customer customer) {
        customerDAO.addCustomer(customer);
    }

    public List<Customer> getAllCustomers() {
        return customerDAO.getAllCustomers();
    }

    public List<Customer> searchCustomers(String term) {
        return customerDAO.searchCustomers(term);
    }

    public Customer findCustomerById(int id) {
        return customerDAO.findById(id);
    }

    public void updateCustomer(Customer customer) {
        customerDAO.updateCustomer(customer);
    }

    public void deleteCustomer(int id) {
        customerDAO.deleteCustomer(id);
    }
}
