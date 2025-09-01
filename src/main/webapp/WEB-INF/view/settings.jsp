<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1" />
  <title>User Settings - Pahana Edu Bookshop</title>
  <link rel="stylesheet" href="<%=request.getContextPath()%>/css/variables.css">
  <link rel="stylesheet" href="<%=request.getContextPath()%>/css/dashboard_styles.css">
  <link rel="stylesheet" href="<%=request.getContextPath()%>/css/settings_styles.css">
</head>
<body>
    <div class="bg-decoration"></div>
    
    <div class="layout">
        <aside class="sidebar" id="sidebar">
            <div class="sidebar-header">
                <div class="sidebar-brand">
                    <div class="sidebar-icon"></div>
                    <div>
                        <div class="sidebar-title">Settings</div>
                        <div class="sidebar-subtitle">Account Management</div>
                    </div>
                </div>
            </div>

            <nav class="sidebar-nav">
                <a href="#" class="nav-item active" onclick="showSection('account')">
                    <span class="nav-icon"></span>
                    Account Settings
                </a>
                <a href="#" class="nav-item" onclick="showSection('security')">
                    <span class="nav-icon"></span>
                    Security
                </a>
                <% 
                com.pahana.model.User currUser = (com.pahana.model.User) session.getAttribute("user");
                boolean isSuperAdmin = currUser != null && "SUPER_ADMIN".equals(currUser.getRole());
                if (isSuperAdmin) {
                %>
                <a href="#" class="nav-item" onclick="showSection('users')">
                    <span class="nav-icon"></span>
                    User Management
                </a>
                <% } %>
            </nav>

            <a href="dashboard" class="back-link">
                Back to Dashboard
            </a>
        </aside>

        <main class="main-content">
            <div class="mobile-header">
                <button class="menu-btn" onclick="toggleSidebar()">Menu</button>
            </div>

            <%@ include file="_toast.jspf" %>
            <% String err = (String) request.getAttribute("error"); if (err != null) { %>
                <div class="error-message"><%= err %></div>
            <% } %>

            <section id="account-section" class="content-section active">
                <div class="section-header">
                    <h1 class="section-title">Account Settings</h1>
                    <p class="section-description">Manage your personal account information</p>
                    <%
                        com.pahana.model.User loggedInUser = (com.pahana.model.User) session.getAttribute("user");
                        if (loggedInUser != null && loggedInUser.getUsername() != null) {
                    %>
                        <p class="section-description">Signed in as: <strong><%= loggedInUser.getUsername() %></strong></p>
                    <%
                        }
                    %>
                </div>

                <div class="cards-grid">
                    <div class="settings-card username-card">
                        <div class="card-header">
                            <div class="card-icon"></div>
                            <h3 class="card-title">Change Username</h3>
                        </div>
                        <form method="post" action="settings" class="card-form">
                            <input type="hidden" name="action" value="change-username" />
                            <div class="form-group">
                                <label class="form-label">New Username</label>
                                <input type="text" name="newUsername" class="form-input" placeholder="Enter new username" required />
                            </div>
                            <button type="submit" class="form-button">Update Username</button>
                        </form>
                    </div>
                </div>
            </section>

            <section id="security-section" class="content-section">
                <div class="section-header">
                    <h1 class="section-title">Security</h1>
                    <p class="section-description">Manage your password and security settings</p>
                </div>

                <div class="cards-grid">
                    <div class="settings-card password-card">
                        <div class="card-header">
                            <div class="card-icon"></div>
                            <h3 class="card-title">Change Password</h3>
                        </div>
                        <form method="post" action="settings" class="card-form">
                            <input type="hidden" name="action" value="change-password" />
                            <div class="form-group">
                                <label class="form-label">Current Password</label>
                                <input type="password" name="currentPassword" class="form-input" placeholder="Enter current password" required />
                            </div>
                            <div class="form-group">
                                <label class="form-label">New Password</label>
                                <input type="password" name="newPassword" class="form-input" placeholder="Enter new password" required />
                            </div>
                            <button type="submit" class="form-button">Update Password</button>
                        </form>
                    </div>
                </div>
            </section>

            <% if (isSuperAdmin) { %>
            <section id="users-section" class="content-section">
                <div class="section-header">
                    <h1 class="section-title">User Management</h1>
                    <p class="section-description">Manage system users and permissions</p>
                </div>

                <div class="cards-grid">
                    <div class="settings-card users-card">
                        <div class="card-header">
                            <div class="card-icon"></div>
                            <h3 class="card-title">Add New User</h3>
                        </div>
                        <form method="post" action="settings" class="card-form">
                            <input type="hidden" name="action" value="add-user" />
                            <div class="form-group">
                                <label class="form-label">Username</label>
                                <input type="text" name="username" class="form-input" placeholder="Enter username" required />
                            </div>
                            <div class="form-group">
                                <label class="form-label">Password</label>
                                <input type="password" name="password" class="form-input" placeholder="Enter password" required />
                            </div>
                            <button type="submit" class="form-button">Add User</button>
                        </form>
                    </div>
                </div>

                <div class="users-table-container">
                        <div class="table-header">
                        <div class="table-icon"></div>
                        <h2 class="table-title">Existing Users</h2>
                    </div>
                    <table class="users-table">
                        <thead>
                            <tr>
                                <th>ID</th>
                                <th>Username</th>
                                <th>Role</th>
                                <th>Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                        <%
                        java.util.List<com.pahana.model.User> users = (java.util.List<com.pahana.model.User>) request.getAttribute("users");
                        com.pahana.model.User curr = (com.pahana.model.User) session.getAttribute("user");
                        if (users != null) {
                            for (com.pahana.model.User u : users) {
                        %>
                            <tr>
                                <td><%= u.getId() %></td>
                                <td><%= u.getUsername() %></td>
                                <td>
                                    <div class="user-role">
                                        <span class="role-badge <%= "SUPER_ADMIN".equals(u.getRole()) ? "role-super" : "role-admin" %>">
                                            <%= (u.getRole() == null || u.getRole().isEmpty()) ? "ADMIN" : u.getRole() %>
                                        </span>
                                    </div>
                                    <% if (isSuperAdmin && u.getId() != curr.getId()) { %>
                                        <form method="post" action="settings" style="display:inline-flex; align-items:center; gap:8px;">
                                            <input type="hidden" name="action" value="update-role" />
                                            <input type="hidden" name="id" value="<%= u.getId() %>" />
                                            <select name="role" class="role-select">
                                                <option value="ADMIN" <%= (u.getRole() == null || "ADMIN".equals(u.getRole())) ? "selected" : "" %>>ADMIN</option>
                                                <option value="SUPER_ADMIN" <%= "SUPER_ADMIN".equals(u.getRole()) ? "selected" : "" %>>SUPER_ADMIN</option>
                                            </select>
                                            <button type="submit" class="update-btn">Update</button>
                                        </form>
                                    <% } %>
                                </td>
                                <td>
                                    <%
                                    boolean isSelf = (curr != null && u.getId() == curr.getId());
                                    if (isSelf) {
                                    %>
                                        <span class="current-user-badge">Current User</span>
                                    <%
                                    } else if (isSuperAdmin) {
                                    %>
                                        <form method="post" action="settings" onsubmit="return confirm('Delete user <%= u.getUsername() %>?');" style="display:inline;">
                                            <input type="hidden" name="action" value="delete-user" />
                                            <input type="hidden" name="id" value="<%= u.getId() %>" />
                                            <button type="submit" class="delete-btn">Delete</button>
                                        </form>
                                    <%
                                    }
                                    %>
                                </td>
                            </tr>
                        <%
                            }
                        }
                        %>
                        </tbody>
                    </table>
                </div>
            </section>
            <% } %>
        </main>
    </div>

    <script>
        function showSection(sectionName) {
            const sections = document.querySelectorAll('.content-section');
            sections.forEach(section => section.classList.remove('active'));
            
            document.getElementById(sectionName + '-section').classList.add('active');
            
            const navItems = document.querySelectorAll('.nav-item');
            navItems.forEach(item => item.classList.remove('active'));
            event.target.classList.add('active');
        }

        function toggleSidebar() {
            document.getElementById('sidebar').classList.toggle('open');
        }

        document.addEventListener('click', function(e) {
            if (window.innerWidth <= 1024) {
                const sidebar = document.getElementById('sidebar');
                const menuBtn = document.querySelector('.menu-btn');
                
                if (!sidebar.contains(e.target) && !menuBtn.contains(e.target)) {
                    sidebar.classList.remove('open');
                }
            }
        });
    </script>

    <script src="<%=request.getContextPath()%>/js/theme.js"></script>
</body>
</html>
