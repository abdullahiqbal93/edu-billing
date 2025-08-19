package com.pahana.service;

import static org.junit.Assert.*;

import java.sql.Connection;
import com.pahana.dao.UserDAO;

import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;

import com.pahana.dao.DBConnectionFactory;
import com.pahana.model.User;

public class UserServiceAuthTest {

    private boolean dbAvailable;
    private UserService service;

    @Before
    public void setUp() {
        Connection conn = DBConnectionFactory.getConnection();
        dbAvailable = conn != null;
        service = UserService.getInstance();
    }

    @Test
    public void validLoginReturnsUser() {
        Assume.assumeTrue("Skipping: DB not reachable", dbAvailable);

        String username = "login_ok";
        String password = "P@ssw0rd!";

        UserDAO dao = UserDAO.getInstance();
        com.pahana.model.User existing = dao.getUserByUsername(username);
        if (existing != null) {
            dao.deleteUser(existing.getId());
        }

        assertTrue(service.addUser(username, password));

        User u = service.authenticate(username, password);
        assertNotNull(u);
        assertEquals(username, u.getUsername());
    }

    @Test
    public void invalidLoginReturnsNull() {
        Assume.assumeTrue("Skipping: DB not reachable", dbAvailable);

        String username = "login_fail";
        String password = "Valid123!";

        UserDAO dao = UserDAO.getInstance();
        com.pahana.model.User existing = dao.getUserByUsername(username);
        if (existing != null) {
            dao.deleteUser(existing.getId());
        }

        assertTrue(service.addUser(username, password));

        assertNull(service.authenticate(username, "wrong"));
        assertNull(service.authenticate("no_such_user", "any"));
    }
}
