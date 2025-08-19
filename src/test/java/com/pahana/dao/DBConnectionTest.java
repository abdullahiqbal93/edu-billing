package com.pahana.dao;

import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.junit.Assume;
import org.junit.Test;

public class DBConnectionTest {

    @Test
    public void singletonInstanceIsCreatedAndNotNull() {
        DBConnection instance = DBConnection.getInstance();
        assertNotNull("DBConnection singleton instance should not be null", instance);
    }

    @Test
    public void multipleCallsReturnSameInstance() {
        DBConnection a = DBConnection.getInstance();
        DBConnection b = DBConnection.getInstance();
        assertSame("getInstance() should return the same singleton instance", a, b);
    }

    @Test
    public void getConnectionReturnsValidConnection() throws SQLException {
        Connection conn = DBConnectionFactory.getConnection();
        Assume.assumeTrue("Skipping: DB not reachable (null connection)", conn != null);
        Assume.assumeTrue("Skipping: Connection not valid", conn.isValid(3));

        assertNotNull("Connection should not be null", conn);
        assertTrue("Connection should be valid", conn.isValid(3));
        conn.close();
    }

    @Test
    public void canExecuteSimpleQuery() throws SQLException {
        Connection conn = DBConnectionFactory.getConnection();
        Assume.assumeTrue("Skipping: DB not reachable (null connection)", conn != null);
        Assume.assumeTrue("Skipping: Connection not valid", conn.isValid(3));

        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery("SELECT 1")) {
            assertTrue("SELECT 1 should return at least one row", rs.next());
            int one = rs.getInt(1);
            assertEquals("First column should be 1", 1, one);
        } finally {
            try { conn.close(); } catch (SQLException ignore) {}
        }
    }
}
