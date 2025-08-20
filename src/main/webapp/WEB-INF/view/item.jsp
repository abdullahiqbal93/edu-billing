<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <title>Item Management - Pahana Edu Bookshop</title>
    <link rel="stylesheet" href="<%=request.getContextPath()%>/css/item_styles.css">
</head>
<body>
<%@ include file="_toast.jspf" %>
<div class="bg-decoration"></div>
<div class="container">
    <header class="header">
        <div class="header-left">
            <div class="page-icon">📚</div>
            <h1 class="page-title">Item Management</h1>
        </div>
        <a href="dashboard" class="back-btn"><span>←</span> Back to Dashboard</a>
    </header>
    <div class="form-section">
        <h2 class="form-title">
            <%
                Object itemObj = request.getAttribute("item");
                boolean isEdit = itemObj != null;
                com.pahana.model.Item item = null;
                if (isEdit) { item = (com.pahana.model.Item) itemObj; }
            %>
            <%= isEdit ? "Edit Item" : "Add New Item" %>
        </h2>
        <%
            if (request.getAttribute("error") != null) {
        %>
        <div class="error-message"><%= request.getAttribute("error") %></div>
        <%
            }
        %>
        <form method="post" action="item">
            <input type="hidden" name="action" value="<%= isEdit ? "update" : "add" %>" />
            <% if (isEdit) { %>
                <input type="hidden" name="id" value="<%= item.getId() %>" />
            <% } %>
            <div class="form-grid">
                <div class="input-group">
                    <input type="text" name="name" class="form-input" placeholder="Item Name" 
                           value="<%= isEdit ? item.getName() : (request.getAttribute("name") != null ? request.getAttribute("name") : "") %>" />
                </div>
                <div class="input-group">
                    <input type="number" name="price" class="form-input" placeholder="Price (LKR)" min="0" step="0.01" 
                           value="<%= isEdit ? item.getPrice() : (request.getAttribute("price") != null ? request.getAttribute("price") : "") %>" />
                </div>
                <div class="input-group">
                    <input type="number" name="quantity" class="form-input" placeholder="Quantity" min="0" step="1"
                           value="<%= isEdit ? item.getQuantity() : (request.getAttribute("quantity") != null ? request.getAttribute("quantity") : "") %>" />
                </div>
                <div class="input-group description-full">
                    <textarea name="description" class="form-textarea" placeholder="Item Description" ><%= isEdit ? item.getDescription() : (request.getAttribute("description") != null ? request.getAttribute("description") : "") %></textarea>
                </div>
            </div>
            <button type="submit" class="form-button"><%= isEdit ? "Update Item" : "Add Item" %></button>
        </form>
    </div>
    <div class="table-section">
        <div class="table-header">
            <h2 class="table-title">Items Inventory</h2>
            <form method="get" action="item">
                <input type="hidden" name="action" value="list" />
                <div class="input-group" style="display:flex; gap:12px; align-items:center; margin-top:12px;">
                    <input type="text" name="q" class="form-input" placeholder="Search by name or description" value="<%= request.getAttribute("q") != null ? request.getAttribute("q") : (request.getParameter("q") != null ? request.getParameter("q") : "") %>" />
                    <button type="submit" class="form-button">Search</button>
                    <a href="item?action=list" class="back-btn">Clear</a>
                    <a href="#" id="download-inventory-report" class="form-button pdf-btn" onclick="window.open('<%=request.getContextPath()%>/report?type=inventory','_blank');return false;">Download Inventory Report</a>
                </div>
            </form>
        </div>
        <div class="table-wrapper">
            <%
                java.util.List<com.pahana.model.Item> items = (java.util.List<com.pahana.model.Item>) request.getAttribute("items");
                if (items != null && !items.isEmpty()) {
            %>
            <table class="data-table">
                <thead>
                <tr>
                    <th>ID</th>
                    <th>Name</th>
                    <th>Description</th>
                    <th>Price</th>
                    <th>Quantity</th>
                    <th>Actions</th>
                </tr>
                </thead>
                <tbody>
                <% for (com.pahana.model.Item i : items) { %>
                    <tr>
                        <td><%= i.getId() %></td>
                        <td><%= i.getName() %></td>
                        <td><%= i.getDescription() %></td>
                        <td class="price-cell">LKR <%= String.format("%.2f", i.getPrice()) %></td>
                        <td><%= i.getQuantity() %></td>
                        <td>
                                     <div class="actions">
                                          <a href="item?action=edit&id=<%= i.getId() %>" class="action-btn edit-btn">Edit</a>
                                          <%
                                              com.pahana.model.User _user = (com.pahana.model.User) session.getAttribute("user");
                                              boolean _isSuper = _user != null && "SUPER_ADMIN".equals(_user.getRole());
                                              if (_isSuper) {
                                          %>
                                          <a href="item?action=delete&id=<%= i.getId() %>" class="action-btn delete-btn"
                                              onclick="return confirm('Are you sure you want to delete this item?');">Delete</a>
                                          <% } %>
                                     </div>
                        </td>
                    </tr>
                <% } %>
                </tbody>
            </table>
            <% } else { %>
            <div class="empty-state">
                <div class="empty-icon"></div>
                <h3>No items found</h3>
                <p>Add your first item using the form above.</p>
            </div>
            <% } %>
        </div>
    </div>
</div>
</body>
</html>
