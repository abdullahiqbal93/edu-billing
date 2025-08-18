package com.pahana.service;

import com.pahana.dao.ItemDAO;
import com.pahana.model.Item;

import java.util.List;

public class ItemService {
    private static ItemService instance;
    private ItemDAO itemDAO;

    private ItemService() {
        itemDAO = ItemDAO.getInstance();
    }
    public static synchronized ItemService getInstance() {
        if (instance == null) {
            instance = new ItemService();
        }
        return instance;
    }
    public List<Item> getAllItems() {
        return itemDAO.getAllItems();
    }
    public List<Item> searchItems(String term) {
        return itemDAO.searchItems(term);
    }
    public void addItem(Item item) {
        itemDAO.addItem(item);
    }
    
    public Item findItemById(int id) {
        return itemDAO.findById(id);
    }

	public void updateItem(Item item) {
		itemDAO.updateItem(item);
	}
    
    public void deleteItem(int id) {
        itemDAO.deleteItem(id);
    }
}
