<%@ page contentType="text/html;charset=UTF-8" language="java" %>
	<!DOCTYPE html>
	<html lang="en">

	<head>
		<meta charset="UTF-8">
		<meta name="viewport" content="width=device-width, initial-scale=1.0">
		<title>Login - Pahana Edu Bookshop</title>
		<link rel="stylesheet" href="<%=request.getContextPath()%>/css/login_styles.css">
	</head>

	<body>
		<div class="bg-decoration"></div>

		<div class="login-container">
			<div class="header">
				<div class="brand">
					<div class="brand-icon">📖</div>
				</div>
				<h1 class="title">Welcome Back</h1>
				<p class="subtitle">Sign in to Pahana Edu Bookshop</p>
			</div>

			<form method="post" action="login" class="login-form">
				<div class="input-group">
					<input type="text" name="username" class="form-input" placeholder="Username" required /> <span
						class="input-icon">👤</span>
				</div>

				<div class="input-group">
					<input type="password" name="password" class="form-input" placeholder="Password" required /> <span
						class="input-icon">🔒</span>
				</div>

				<button type="submit" class="login-button">Sign In</button>
			</form>

			<% if (request.getAttribute("error") !=null) { %>
				<div class="error-message">
					<%=request.getAttribute("error")%>
				</div>
				<% } %>
		</div>
	</body>

	</html>