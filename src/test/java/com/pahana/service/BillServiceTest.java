package com.pahana.service;

import static org.junit.Assert.*;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;

import com.pahana.dao.BillDAO;
import com.pahana.dao.CustomerDAO;
import com.pahana.dao.DBConnectionFactory;
import com.pahana.dao.ItemDAO;
import com.pahana.model.Bill;
import com.pahana.model.BillItem;
import com.pahana.model.Customer;
import com.pahana.model.Item;

public class BillServiceTest {

    private boolean dbAvailable;
    private BillService billService;
    private ItemDAO itemDAO;
    private CustomerDAO customerDAO;
    private BillDAO billDAO;

    private int createdCustomerId = -1;
    private int createdItemId = -1;
    private Integer createdBillId = null;

    @Before
    public void setUp() {
        Connection conn = DBConnectionFactory.getConnection();
        dbAvailable = conn != null;
        Assume.assumeTrue("Skipping BillService tests: DB not reachable", dbAvailable);

        billService = BillService.getInstance();
        itemDAO = com.pahana.dao.ItemDAO.getInstance();
        customerDAO = com.pahana.dao.CustomerDAO.getInstance();
        billDAO = com.pahana.dao.BillDAO.getInstance();

        cleanupTestData();
    }

    @After
    public void tearDown() {
        if (!dbAvailable) return;
        try {
            if (createdBillId != null) {
                billDAO.deleteBill(createdBillId);
            }
        } catch (Exception ignore) {}

        try {
            if (createdItemId > 0) itemDAO.deleteItem(createdItemId);
        } catch (Exception ignore) {}

        try {
            if (createdCustomerId > 0) customerDAO.deleteCustomer(createdCustomerId);
        } catch (Exception ignore) {}

        cleanupTestData();
    }

    private void cleanupTestData() {
        try {
            List<Item> items = itemDAO.searchItems("__TEST__");
            for (Item it : items) {
                itemDAO.deleteItem(it.getId());
            }
        } catch (Exception ignore) {}

        try {
            List<Customer> customers = customerDAO.searchCustomers("__TEST__");
            for (Customer c : customers) {
                List<Bill> bills = billDAO.getBillsByCustomerId(c.getId());
                for (Bill b : bills) {
                    billDAO.deleteBill(b.getId());
                }
                customerDAO.deleteCustomer(c.getId());
            }
        } catch (Exception ignore) {}
    }

    @Test
    public void createBillWithItems_decrementsStockAndReturnsBill() {
        Assume.assumeTrue("Skipping BillService tests: DB not reachable", dbAvailable);

        Customer customer = new Customer(0, "__TEST__ACC", "__TEST__ Customer", "addr", "000", "t@t.com");
        boolean added = customerDAO.addCustomer(customer);
        assertTrue(added);
        Customer created = customerDAO.findByAccountNumber("__TEST__ACC");
        assertNotNull(created);
        createdCustomerId = created.getId();

        Item item = new Item(0, "__TEST__ Book", "__TEST__ desc", 5.50, 10);
        itemDAO.addItem(item);
        List<Item> found = itemDAO.searchItems("__TEST__ Book");
        assertNotNull(found);
        assertTrue(found.size() >= 1);
        Item createdItem = found.get(0);
        createdItemId = createdItem.getId();

        int qty = 2;
        double price = createdItem.getPrice();

        List<BillItem> items = new ArrayList<>();
        items.add(new BillItem(0, 0, createdItemId, qty, price, price * qty));

        Bill bill = billService.createBillWithItems(created.getId(), items);
        assertNotNull(bill);
        assertTrue(bill.getId() > 0);
        createdBillId = bill.getId();

        assertEquals(qty, bill.getTotalUnits());
        assertEquals(price * qty, bill.getTotalAmount(), 0.001);

        Item after = itemDAO.findById(createdItemId);
        assertNotNull(after);
        assertEquals(10 - qty, after.getQuantity());
    }
}
