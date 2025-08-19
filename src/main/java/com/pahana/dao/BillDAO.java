package com.pahana.dao;

import com.pahana.model.Bill;
import com.pahana.model.BillItem;
import java.sql.*;
import java.util.*;

public class BillDAO {
    private static BillDAO instance;
    private Connection conn;

    private BillDAO() {
        conn = DBConnectionFactory.getConnection();
    }

    public List<Bill> getAllBills() {
        List<Bill> bills = new ArrayList<>();
        String sql = "SELECT * FROM bills ORDER BY created_at DESC, id DESC";
        try (PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                bills.add(new Bill(
                        rs.getInt("id"),
                        rs.getInt("customer_id"),
                        rs.getInt("total_units"),
                        rs.getDouble("total_amount"),
                        rs.getString("created_at")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bills;
    }

    public static synchronized BillDAO getInstance() {
        if (instance == null) {
            instance = new BillDAO();
        }
        return instance;
    }

    public List<Bill> getBillsByCustomerId(int customerId) {
        List<Bill> bills = new ArrayList<>();
        try {
            PreparedStatement ps = conn
                    .prepareStatement("SELECT * FROM bills WHERE customer_id = ? ORDER BY created_at DESC, id DESC");
            ps.setInt(1, customerId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                bills.add(new Bill(
                        rs.getInt("id"),
                        rs.getInt("customer_id"),
                        rs.getInt("total_units"),
                        rs.getDouble("total_amount"),
                        rs.getString("created_at")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bills;
    }

    public int addBill(Bill bill) {
        String sql = "INSERT INTO bills (customer_id, total_units, total_amount) VALUES (?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, bill.getCustomerId());
            ps.setInt(2, bill.getTotalUnits());
            ps.setDouble(3, bill.getTotalAmount());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public void addBillItems(int billId, List<BillItem> items) {
        String sql = "INSERT INTO bill_items (bill_id, item_id, quantity, unit_price, total_price) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (BillItem bi : items) {
                ps.setInt(1, billId);
                ps.setInt(2, bi.getItemId());
                ps.setInt(3, bi.getQuantity());
                ps.setDouble(4, bi.getUnitPrice());
                ps.setDouble(5, bi.getTotalPrice());
                ps.addBatch();
            }
            ps.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<BillItem> getBillItems(int billId) {
        List<BillItem> items = new ArrayList<>();
        String sql = "SELECT * FROM bill_items WHERE bill_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, billId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    items.add(new BillItem(
                            rs.getInt("bill_item_id"),
                            rs.getInt("bill_id"),
                            rs.getInt("item_id"),
                            rs.getInt("quantity"),
                            rs.getDouble("unit_price"),
                            rs.getDouble("total_price")));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return items;
    }

    public Bill getBillById(int id) {
        String sql = "SELECT * FROM bills WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Bill(
                            rs.getInt("id"),
                            rs.getInt("customer_id"),
                            rs.getInt("total_units"),
                            rs.getDouble("total_amount"),
                            rs.getString("created_at"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean deleteBill(int id) {
        String sql = "DELETE FROM bills WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
