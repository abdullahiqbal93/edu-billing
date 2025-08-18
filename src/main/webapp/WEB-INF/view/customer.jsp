<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%
	Object custObj = request.getAttribute("customer");
	boolean isEdit = custObj != null;
	com.pahana.model.Customer customer = null;
	if (isEdit) { customer = (com.pahana.model.Customer) custObj; }
%>
<!DOCTYPE html>
<html lang="en">
<head>
	<meta charset="UTF-8">
	<meta name="viewport" content="width=device-width, initial-scale=1.0">
	<title>Customer Management - Pahana Edu Bookshop</title>
	<link rel="stylesheet" href="<%=request.getContextPath()%>/css/customer_styles.css">
</head>
<body>
	<div class="bg-decoration"></div>
	<div class="container">
		<header class="header">
			<div class="header-left">
				<div class="page-icon">👥</div>
				<h1 class="page-title">Customer Management</h1>
			</div>
			<a href="dashboard" class="back-btn"><span>←</span> Back to Dashboard</a>
		</header>

		<div class="form-section">
			<h2 class="form-title">
				<%= isEdit ? "Edit Customer" : "Add New Customer" %>
			</h2>

			<% if (request.getAttribute("error") != null) { %>
				<div class="error-message"><%=request.getAttribute("error")%></div>
			<% } %>

			<form method="post" action="customer">
				<% if (isEdit) { %>
					<input type="hidden" name="action" value="update" />
					<input type="hidden" name="id" value="<%=customer.getId()%>" />
				<% } else { %>
					<input type="hidden" name="action" value="add" />
				<% } %>

				<div class="form-grid">
					<div class="input-group">
						<input type="number" name="accountNumber" class="form-input"
							placeholder="Account Number"
							value="<%= isEdit ? customer.getAccountNumber() : (request.getAttribute("accountNumber") != null ? request.getAttribute("accountNumber") : "") %>"
							 />
					</div>

					<div class="input-group">
						<input type="text" name="name" class="form-input"
							placeholder="Customer Name"
							title="Name cannot contain numbers"
							value="<%= isEdit ? customer.getName() : (request.getAttribute("name") != null ? request.getAttribute("name") : "") %>"
							 />
					</div>

					<div class="input-group">
						<input type="text" name="address" class="form-input"
							placeholder="Address"
							value="<%= isEdit ? customer.getAddress() : (request.getAttribute("address") != null ? request.getAttribute("address") : "") %>"
							 />
					</div>

					<div class="input-group">
						<input type="text" name="telephone" class="form-input"
							placeholder="Phone Number"
							title="Phone number should be 7-15 digits; you may use +, spaces, dashes, or parentheses"
							value="<%= isEdit ? customer.getTelephone() : (request.getAttribute("telephone") != null ? request.getAttribute("telephone") : "") %>"
							 />
					</div>

					<div class="input-group">
						<input type="email" name="email" class="form-input"
							placeholder="Email"
							title="Enter a valid email (e.g., name@example.com)"
							value="<%= isEdit ? customer.getEmail() : (request.getAttribute("email") != null ? request.getAttribute("email") : "") %>"
							 />
					</div>
				</div>

				<button type="submit" class="form-button">
					<span></span>
					<%= isEdit ? "Update Customer" : "Add Customer" %>
				</button>
			</form>
		</div>

		<div class="table-section">
			<div class="table-header">
				<h2 class="table-title">Customer List</h2>
			</div>

			<div class="table-wrapper">
				<%
					java.util.List<com.pahana.model.Customer> customers =
						(java.util.List<com.pahana.model.Customer>) request.getAttribute("customers");
					if (customers != null && !customers.isEmpty()) {
				%>
					<table class="data-table">
						<thead>
							<tr>
								<th>ID</th>
								<th>Account #</th>
								<th>Name</th>
								<th>Address</th>
								<th>Phone</th>
								<th>Email</th>
								<th>Actions</th>
							</tr>
						</thead>
						<tbody>
							<% for (com.pahana.model.Customer c : customers) { %>
								<tr>
									<td><%=c.getId()%></td>
									<td><%=c.getAccountNumber()%></td>
									<td><%=c.getName()%></td>
									<td><%=c.getAddress()%></td>
									<td><%=c.getTelephone()%></td>
									<td><%=c.getEmail()%></td>
									<td>
										<div class="actions">
											<a href="customer?action=edit&id=<%=c.getId()%>" class="action-btn edit-btn">Edit</a>
											<%
												com.pahana.model.User _user = (com.pahana.model.User) session.getAttribute("user");
												boolean _isSuper = _user != null && "SUPER_ADMIN".equals(_user.getRole());
												if (_isSuper) {
											%>
											<a href="customer?action=delete&id=<%=c.getId()%>" class="action-btn delete-btn"
												onclick="return confirm('Are you sure you want to delete this customer?');">Delete</a>
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
						<h3>No customers found</h3>
						<p>Add your first customer using the form above.</p>
					</div>
				<% } %>
			</div>
		</div>
	</div>
</body>
</html>
