package com.pahana.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.pahana.model.Customer;

public class CustomerDAO {

    private static CustomerDAO instance;
    private Connection conn;

    private CustomerDAO() {
        conn = DBConnectionFactory.getConnection();
    }

    public static synchronized CustomerDAO getInstance() {
        if (instance == null) {
            instance = new CustomerDAO();
        }
        return instance;
    }

    public boolean addCustomer(Customer customer) {
        String query = "INSERT INTO customers (account_number, name, address, telephone, email) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setString(1, customer.getAccountNumber());
            statement.setString(2, customer.getName());
            statement.setString(3, customer.getAddress());
            statement.setString(4, customer.getTelephone());
            statement.setString(5, customer.getEmail());
            int rows = statement.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Customer> getAllCustomers() {
        List<Customer> customers = new ArrayList<>();
        String query = "SELECT * FROM customers";
        try (PreparedStatement statement = conn.prepareStatement(query);
                ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                customers.add(mapRowToCustomer(resultSet));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return customers;
    }

    public List<Customer> searchCustomers(String term) {
        List<Customer> customers = new ArrayList<>();
        if (term == null)
            term = "";
        String like = "%" + term.trim() + "%";
        String query = "SELECT * FROM customers WHERE name LIKE ? OR account_number LIKE ? OR address LIKE ? OR telephone LIKE ? OR email LIKE ?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, like);
            ps.setString(2, like);
            ps.setString(3, like);
            ps.setString(4, like);
            ps.setString(5, like);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    customers.add(mapRowToCustomer(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return customers;
    }

    public Customer findById(int id) {
        String query = "SELECT * FROM customers WHERE id = ?";
        try (PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapRowToCustomer(resultSet);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Customer findByAccountNumber(String accountNumber) {
        String query = "SELECT * FROM customers WHERE account_number = ?";
        try (PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setString(1, accountNumber);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapRowToCustomer(resultSet);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void updateCustomer(Customer customer) {
        String query = "UPDATE customers SET account_number = ?, name = ?, address = ?, telephone = ?, email = ? WHERE id = ?";
        try (PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setString(1, customer.getAccountNumber());
            statement.setString(2, customer.getName());
            statement.setString(3, customer.getAddress());
            statement.setString(4, customer.getTelephone());
            statement.setString(5, customer.getEmail());
            statement.setInt(6, customer.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteCustomer(int id) {
        String query = "DELETE FROM customers WHERE id = ?";
        try (PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setInt(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Customer mapRowToCustomer(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String accountNumber = rs.getString("account_number");
        String name = rs.getString("name");
        String address = rs.getString("address");
        String telephone = rs.getString("telephone");
        String email = rs.getString("email");
        return new Customer(id, accountNumber, name, address, telephone, email);
    }
}
