package com.pahana.model;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class ItemTest {

    private Item item;

    @Before
    public void setUp() {
        item = new Item();
    }

    @Test
    public void testItemSettersAndGetters() {
        item.setId(11);
        item.setName("Marker Pen");
        item.setDescription("Blue permanent marker");
        item.setPrice(2.49);
        item.setQuantity(30);

        assertEquals(11, item.getId());
        assertEquals("Marker Pen", item.getName());
        assertEquals("Blue permanent marker", item.getDescription());
        assertEquals(2.49, item.getPrice(), 0.0001);
        assertEquals(30, item.getQuantity());
    }

    @Test
    public void testFourArgConstructorDefaultsQuantityToZero() {
        Item i = new Item(5, "Notebook", "A5 ruled", 1.25);
        assertEquals(5, i.getId());
        assertEquals("Notebook", i.getName());
        assertEquals("A5 ruled", i.getDescription());
        assertEquals(1.25, i.getPrice(), 0.0001);
        assertEquals(0, i.getQuantity());
    }

    @Test
    public void testFiveArgConstructor() {
        Item i = new Item(9, "Pencil", "HB", 0.45, 100);
        assertEquals(9, i.getId());
        assertEquals("Pencil", i.getName());
        assertEquals("HB", i.getDescription());
        assertEquals(0.45, i.getPrice(), 0.0001);
        assertEquals(100, i.getQuantity());
    }
}
