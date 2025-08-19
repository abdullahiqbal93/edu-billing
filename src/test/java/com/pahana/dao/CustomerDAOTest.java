package com.pahana.dao;

import static org.junit.Assert.*;

import java.sql.Connection;
import java.util.List;

import org.junit.After;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;

import com.pahana.model.Customer;

public class CustomerDAOTest {

    private CustomerDAO dao;
    private boolean dbAvailable;

    @Before
    public void setUp() {
        Connection conn = DBConnectionFactory.getConnection();
        dbAvailable = conn != null;
        Assume.assumeTrue("Skipping DAO tests: DB not reachable", dbAvailable);
        dao = CustomerDAO.getInstance();
        cleanupTestData();
    }

    @After
    public void tearDown() {
        if (dbAvailable)
            cleanupTestData();
    }

    private void cleanupTestData() {
        Customer existing = dao.findByAccountNumber("900001");
        if (existing != null)
            dao.deleteCustomer(existing.getId());
        existing = dao.findByAccountNumber("900002");
        if (existing != null)
            dao.deleteCustomer(existing.getId());
    }

    @Test
    public void addFindUpdateDeleteTest() {
        Customer c = new Customer(0, "900001", "Test User", "Test Address", "0700000000", "t@example.com");
        boolean added = dao.addCustomer(c);
        assertTrue("Customer should be added", added);

        Customer exCust = dao.findByAccountNumber("900001");
        assertNotNull("Should fetch by account number", exCust);
        assertEquals("Test User", exCust.getName());

        exCust.setName("Updated User");
        exCust.setAccountNumber("900002");
        dao.updateCustomer(exCust);

        Customer byNewAcc = dao.findByAccountNumber("900002");
        assertNotNull(byNewAcc);
        assertEquals("Updated User", byNewAcc.getName());

        dao.deleteCustomer(byNewAcc.getId());
        assertNull("Customer should be deleted", dao.findByAccountNumber("900002"));
    }

    @Test
    public void getAllAndSearchReturnLists() {
        List<Customer> all = dao.getAllCustomers();
        assertNotNull(all);

        List<Customer> search = dao.searchCustomers("nonexistent-term-xyz");
        assertNotNull(search);
    }
}
