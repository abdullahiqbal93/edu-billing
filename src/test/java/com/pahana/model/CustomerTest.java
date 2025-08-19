package com.pahana.model;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class CustomerTest {

    private Customer customer;

    @Before
    public void setUp() {
        customer = new Customer();
    }

    @Test
    public void testSettersAndGetters() {
        customer.setId(100);
        customer.setAccountNumber("ACC-100");
        customer.setName("Alice Wonderland");
        customer.setAddress("789 Park Ave");
        customer.setTelephone("0755555555");
        customer.setEmail("alice@wonder.land");

        assertEquals(100, customer.getId());
        assertEquals("ACC-100", customer.getAccountNumber());
        assertEquals("Alice Wonderland", customer.getName());
        assertEquals("789 Park Ave", customer.getAddress());
        assertEquals("0755555555", customer.getTelephone());
        assertEquals("0755555555", customer.getPhone());
        assertEquals("alice@wonder.land", customer.getEmail());
    }

    @Test
    public void testDefaultConstructorAndSetters() {
        Customer c = new Customer();
        c.setId(42);
        c.setAccountNumber("ACC-001");
        c.setName("Jane Doe");
        c.setAddress("123 Main St");
        c.setTelephone("0771234567");
        c.setEmail("jane@example.com");

        assertEquals(42, c.getId());
        assertEquals("ACC-001", c.getAccountNumber());
        assertEquals("Jane Doe", c.getName());
        assertEquals("123 Main St", c.getAddress());
        assertEquals("0771234567", c.getTelephone());
        assertEquals("0771234567", c.getPhone());
        assertEquals("jane@example.com", c.getEmail());
    }

    @Test
    public void testAllArgsConstructor() {
        Customer c = new Customer(7, "ACC-777", "John Smith", "456 High Rd", "0712345678", "john@smith.io");

        assertEquals(7, c.getId());
        assertEquals("ACC-777", c.getAccountNumber());
        assertEquals("John Smith", c.getName());
        assertEquals("456 High Rd", c.getAddress());
        assertEquals("0712345678", c.getTelephone());
        assertEquals("john@smith.io", c.getEmail());
    }

}
