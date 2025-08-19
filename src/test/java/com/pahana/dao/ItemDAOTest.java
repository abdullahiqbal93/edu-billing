package com.pahana.dao;

import static org.junit.Assert.*;

import java.sql.Connection;
import java.util.List;

import org.junit.After;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;

import com.pahana.model.Item;

public class ItemDAOTest {

    private ItemDAO dao;
    private boolean dbAvailable;

    @Before
    public void setUp() {
        Connection conn = DBConnectionFactory.getConnection();
        dbAvailable = conn != null;
        Assume.assumeTrue("Skipping ItemDAO tests: DB not reachable", dbAvailable);
        dao = ItemDAO.getInstance();
        cleanupTestData();
    }

    @After
    public void tearDown() {
        if (dbAvailable) cleanupTestData();
    }

    private void cleanupTestData() {
        List<Item> items = dao.searchItems("__TEST__");
        for (Item i : items) {
            dao.deleteItem(i.getId());
        }
    }

    @Test
    public void addFindUpdateDeleteTest() {
        Item i = new Item(0, "__TEST__ Ruler", "30cm plastic", 0.99, 10);
        dao.addItem(i);

        List<Item> search = dao.searchItems("__TEST__ Ruler");
        assertNotNull(search);
        assertTrue(search.size() >= 1);
        Item found = search.get(0);

        found.setDescription("30cm plastic - clear");
        found.setQuantity(25);
        dao.updateItem(found);

        Item byId = dao.findById(found.getId());
        assertNotNull(byId);
        assertEquals("30cm plastic - clear", byId.getDescription());
        assertEquals(25, byId.getQuantity());

        boolean decremented = dao.decrementStock(byId.getId(), 5);
        assertTrue(decremented);
        Item afterDec = dao.findById(byId.getId());
        assertEquals(20, afterDec.getQuantity());

        dao.deleteItem(afterDec.getId());
        Item deleted = dao.findById(afterDec.getId());
        assertNull(deleted);
    }

    @Test
    public void getAllAndSearchReturnLists() {
        List<Item> all = dao.getAllItems();
        assertNotNull(all);

        List<Item> search = dao.searchItems("nonexistent-term-xyz");
        assertNotNull(search);
    }
}
