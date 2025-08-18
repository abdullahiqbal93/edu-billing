<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <!DOCTYPE html>
    <html lang="en">

    <head>
        <meta charset="UTF-8" />
        <meta name="viewport" content="width=device-width, initial-scale=1" />
        <title>Help - Pahana Edu Bookshop</title>
        <link rel="stylesheet" href="<%=request.getContextPath()%>/css/help_styles.css">
    </head>

    <body>
        <div class="bg-decoration"></div>
        <div class="container">
            <header class="header">
                <div class="header-left">
                    <div class="page-icon">❓</div>
                    <h1 class="page-title">Help & Guidelines</h1>
                </div>
                <a href="dashboard" class="back-btn">
                    <span>←</span> Back to Dashboard
                </a>
            </header>

            <div class="help-section">
                <h2 class="section-title">
                    <div class="section-icon">🚀</div>
                    New User Guide
                </h2>
                <div class="help-grid">
                    <div class="help-card">
                        <h3 class="card-title">
                            <div class="card-icon">�</div>
                            1) Sign in & Access
                        </h3>
                        <ul class="help-list">
                            <li>Open the app and sign in with your username and password.</li>
                            <li>If you don't have an account, contact super admin to create an account
                            <li>For security, always use the Logout button to end your session.</li>
                        </ul>
                    </div>

                    <div class="help-card">
                        <h3 class="card-title">
                            <div class="card-icon">�</div>
                            2) Dashboard Tour
                        </h3>
                        <ul class="help-list">
                            <li>The dashboard links to: Customers, Items, Billing, and Settings.</li>
                            <li>Use the top navigation or quick buttons below to move between sections.</li>
                            <li>Status messages and errors appear as small toasts at the top.</li>
                        </ul>
                    </div>

                    <div class="help-card">
                        <h3 class="card-title">
                            <div class="card-icon">⚙️</div>
                            3) Change Settings
                        </h3>
                        <ul class="help-list">
                            <li>Go to <span class="highlight-text">Settings</span> to change username or password</li>
                            <li>Go to <span class="highlight-text">Security</span> to change password.</li>
                        </ul>
                    </div>

                    <div class="help-card">
                        <h3 class="card-title">
                            <div class="card-icon">�️</div>
                            4) Manage Your Data
                        </h3>
                        <ul class="help-list">
                            <li>Customers: add, edit, delete; all fields are required and validated.</li>
                            <li>Items: maintain product details and pricing; keep your catalog current.</li>
                            <li>Use search to quickly find records.</li>
                        </ul>
                    </div>

                    <div class="help-card">
                        <h3 class="card-title">
                            <div class="card-icon">🧾</div>
                            5) Create Your First Bill
                        </h3>
                        <ul class="help-list">
                            <li>Open <span class="highlight-text">Billing</span> and select a customer.</li>
                            <li>Add items and quantities; prices and totals calculate automatically.</li>
                            <li>Click add bill to <span class="highlight-text">Print Bill</span> or
                                <span class="highlight-text">Send Receipt</span> via email to the customer.</li>
                        </ul>
                    </div>



                    <div class="help-card">
                        <h3 class="card-title">
                            <div class="card-icon">�</div>
                            6) Tips & Troubleshooting
                        </h3>
                        <ul class="help-list">
                            <li>If a form shows an error, check required fields and value formats.</li>
                            <li>Session timed out? Log in again and retry the last action.</li>
                            <li>Still stuck? Contact your admin for support.</li>
                        </ul>
                    </div>
                </div>

                <div class="important-note">
                    Your data is validated automatically. Fix highlighted fields and submit again if you see an error.
                </div>
            </div>

            <div class="help-section">
                <h2 class="section-title">
                    <div class="section-icon">⚡</div>
                    Quick Actions
                </h2>
                <div class="quick-actions">
                    <a href="customer" class="action-btn">👥 Manage Customers</a>
                    <a href="item" class="action-btn">📦 Manage Items</a>
                    <a href="bill" class="action-btn">💰 Create Bills</a>
                    <a href="dashboard" class="action-btn secondary-btn">📊 View Dashboard</a>
                </div>
            </div>
        </div>
    </body>

    </html>