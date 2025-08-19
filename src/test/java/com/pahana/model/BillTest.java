package com.pahana.model;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

public class BillTest {

    @Test
    public void testCalculateTotalsFromBillItems() {
        BillItem a = new BillItem(0, 0, 1, 2, 5.00, 10.00);
        BillItem b = new BillItem(0, 0, 2, 3, 7.50, 22.50);
        List<BillItem> items = Arrays.asList(a, b);

        int totalUnits = items.stream().mapToInt(BillItem::getQuantity).sum();
        double totalAmount = items.stream().mapToDouble(BillItem::getTotalPrice).sum();

        Bill bill = new Bill(0, 123, totalUnits, totalAmount, "2025-08-19 12:00:00");
        bill.setItems(items);

        assertEquals(5, bill.getTotalUnits());
        assertEquals(32.50, bill.getTotalAmount(), 0.0001);
        assertNotNull(bill.getItems());
        assertEquals(2, bill.getItems().size());
    }

    @Test
    public void testBillSettersAndGetters() {
        Bill bill = new Bill();
        bill.setId(77);
        bill.setCustomerId(1001);
        bill.setTotalUnits(9);
        bill.setTotalAmount(99.99);
        bill.setCreatedAt("2025-08-19 09:30:00");

        List<BillItem> items = new ArrayList<>();
        items.add(new BillItem(1, 77, 10, 1, 9.99, 9.99));
        bill.setItems(items);

        assertEquals(77, bill.getId());
        assertEquals(1001, bill.getCustomerId());
        assertEquals(9, bill.getTotalUnits());
        assertEquals(99.99, bill.getTotalAmount(), 0.0001);
        assertEquals("2025-08-19 09:30:00", bill.getCreatedAt());
        assertNotNull(bill.getItems());
        assertEquals(1, bill.getItems().size());
    }
}
