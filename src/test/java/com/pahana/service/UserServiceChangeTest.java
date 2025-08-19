package com.pahana.service;

import static org.junit.Assert.*;

import java.sql.Connection;

import org.junit.After;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;

import com.pahana.dao.DBConnectionFactory;
import com.pahana.dao.UserDAO;
import com.pahana.model.User;

public class UserServiceChangeTest {

    private boolean dbAvailable;
    private UserService service;
    private UserDAO dao;

    @Before
    public void setUp() {
        Connection conn = DBConnectionFactory.getConnection();
        dbAvailable = conn != null;
        service = UserService.getInstance();
        dao = UserDAO.getInstance();
        cleanup("user_to_rename");
        cleanup("renamed_user");
        cleanup("user_to_change_pw");
    }

    @After
    public void tearDown() {
        if (!dbAvailable) return;
        cleanup("user_to_rename");
        cleanup("renamed_user");
        cleanup("user_to_change_pw");
    }

    private void cleanup(String username) {
        if (!dbAvailable) return;
        try {
            User u = dao.getUserByUsername(username);
            if (u != null) dao.deleteUser(u.getId());
        } catch (Exception ignore) {}
    }

    @Test
    public void changeUsernameTest() {
        Assume.assumeTrue("Skipping: DB not reachable", dbAvailable);

        assertTrue(service.addUser("user_to_rename", "Aa123456!"));
        User u = dao.getUserByUsername("user_to_rename");
        assertNotNull(u);

        boolean ok = service.changeUsername(u.getId(), "renamed_user");
        assertTrue(ok);

        assertNull(dao.getUserByUsername("user_to_rename"));
        User renamed = dao.getUserByUsername("renamed_user");
        assertNotNull(renamed);
        assertEquals("renamed_user", renamed.getUsername());
    }

    @Test
    public void changePasswordTest() {
        Assume.assumeTrue("Skipping: DB not reachable", dbAvailable);

        String username = "user_to_change_pw";
        String oldPw = "OldPw123!";
        String newPw = "NewPw456!";

        assertTrue(service.addUser(username, oldPw));
        User u = dao.getUserByUsername(username);
        assertNotNull(u);

        boolean ok = service.changePassword(u.getId(), oldPw, newPw);
        assertTrue(ok);

        assertNull(service.authenticate(username, oldPw));
        assertNotNull(service.authenticate(username, newPw));
    }
}
