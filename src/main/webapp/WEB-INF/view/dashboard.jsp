<!DOCTYPE html>
<html lang="en">

	<head>
	<meta charset="UTF-8" />
	<meta name="viewport" content="width=device-width, initial-scale=1" />
	<title>Dashboard - Pahana Edu Bookshop</title>
	<link rel="stylesheet" href="<%=request.getContextPath()%>/css/variables.css">
	<link rel="stylesheet" href="<%=request.getContextPath()%>/css/dashboard_styles.css">
</head><body>
	<div class="bg-decoration"></div>

	<div class="container">
		<header class="header">
			<div class="brand">
				<div class="brand-icon" aria-label="Book Icon" role="img">
					<svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
						<path d="M4 19H20V5H4V19Z" stroke="white" stroke-width="2" stroke-linejoin="round" />
						<path d="M4 5L12 13L20 5" stroke="white" stroke-width="2" stroke-linejoin="round" />
					</svg>
				</div>
				<div>
					<h1 class="title">Pahana Edu</h1>
					<p class="subtitle">Bookshop Management</p>
				</div>
			</div>
		</header>

		<main class="dashboard-grid">
			<a href="customer" class="dashboard-card customers">
				<div class="card-icon" aria-label="Customers Icon" role="img">
					<svg width="28" height="28" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
						<circle cx="12" cy="8" r="4" stroke="white" stroke-width="2" />
						<path d="M4 20c0-4 8-4 8-4s8 0 8 4" stroke="white" stroke-width="2" stroke-linecap="round" />
					</svg>
				</div>
				<h3 class="card-title">Customers</h3>
				<p class="card-description">Manage customer profiles, accounts,
					and purchase history</p>
			</a> <a href="item" class="dashboard-card items">
				<div class="card-icon" aria-label="Inventory Icon" role="img">
					<svg width="28" height="28" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
						<rect x="3" y="4" width="18" height="16" stroke="white" stroke-width="2" rx="2" />
						<path d="M3 9H21" stroke="white" stroke-width="2" />
						<path d="M8 13H16" stroke="white" stroke-width="2" />
					</svg>
				</div>
				<h3 class="card-title">Inventory</h3>
				<p class="card-description">Track books, supplies, and
					educational materials</p>
			</a> <a href="bill" class="dashboard-card billing">
				<div class="card-icon" aria-label="Billing Icon" role="img">
					<svg width="28" height="28" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
						<rect x="3" y="7" width="18" height="10" stroke="white" stroke-width="2" rx="2" />
						<circle cx="12" cy="12" r="3" stroke="white" stroke-width="2" />
					</svg>
				</div>
				<h3 class="card-title">Billing</h3>
				<p class="card-description">Process transactions and manage
					invoices</p>
			</a> <a href="help" class="dashboard-card help">
				<div class="card-icon" aria-label="Support Icon" role="img">
					<svg width="28" height="28" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
						<circle cx="12" cy="12" r="10" stroke="white" stroke-width="2" />
						<path d="M12 8V12" stroke="white" stroke-width="2" stroke-linecap="round" />
						<circle cx="12" cy="16" r="1" fill="white" />
					</svg>
				</div>
				<h3 class="card-title">Support</h3>
				<p class="card-description">Get help and access documentation</p>
			</a>
		</main>

		<div class="logout-section">
			<a href="settings" class="logout-btn" style="margin-right:12px">Settings</a>
			<a href="logout" class="logout-btn">Sign Out </a>
		</div>
	</div>

	<script src="<%=request.getContextPath()%>/js/theme.js"></script>
</body>

</html>