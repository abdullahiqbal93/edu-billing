<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1" />
  <title>User Settings - Pahana Edu Bookshop</title>
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
</body>
</html>
