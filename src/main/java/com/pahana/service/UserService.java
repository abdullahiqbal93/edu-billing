package com.pahana.service;

import com.pahana.dao.UserDAO;
import com.pahana.model.User;
import com.pahana.util.PasswordUtil;

public class UserService {
	private static UserService instance;
	private UserDAO userDAO;

	private UserService() {
		userDAO = UserDAO.getInstance();
	}

	public static synchronized UserService getInstance() {
		if (instance == null) {
			instance = new UserService();
		}
		return instance;
	}

	public User authenticate(String username, String password) {
		User user = userDAO.getUserByUsername(username);
		if (user != null && PasswordUtil.verify(password, user.getPassword())) {
			return user;
		}
		return null;
	}

	public boolean changeUsername(int userId, String newUsername) {
		if (newUsername == null || newUsername.trim().isEmpty()) return false;
		if (userDAO.usernameExists(newUsername)) return false;
		return userDAO.updateUsername(userId, newUsername.trim());
	}

	public boolean changePassword(int userId, String currentPassword, String newPassword) {
		if (newPassword == null || newPassword.isEmpty()) return false;
		User u = userDAO.getUserById(userId);
		if (u == null) return false;
		if (!PasswordUtil.verify(currentPassword, u.getPassword())) return false;
		String hashed = PasswordUtil.hash(newPassword);
		return userDAO.updatePassword(userId, hashed);
	}

	public java.util.List<User> listUsers() {
		return userDAO.getAllUsers();
	}

	public boolean addUser(String username, String password) {
		if (username == null || username.trim().isEmpty() || password == null || password.isEmpty()) return false;
		if (userDAO.usernameExists(username.trim())) return false;
		String hashed = PasswordUtil.hash(password);
		return userDAO.createUser(username.trim(), hashed, "ADMIN");
	}

	public boolean removeUser(int id) {
		return userDAO.deleteUser(id);
	}

	public boolean updateRole(int userId, String role) {
		if (role == null || role.isEmpty()) return false;
		if (!"SUPER_ADMIN".equals(role) && !"ADMIN".equals(role)) return false;
		return userDAO.updateRole(userId, role);
	}

	public boolean isSuperAdmin(User user) {
		return user != null && "SUPER_ADMIN".equals(user.getRole());
	}

	public boolean isAdmin(User user) {
		return user != null && ("ADMIN".equals(user.getRole()) || "SUPER_ADMIN".equals(user.getRole()));
	}
}
