# Pahana Edu Bookshop Billing System

A lightweight Java Servlet/JSP app to manage customers, inventory, billing, and users for a bookshop. Build with Maven and deploy to Tomcat.

## Features
- Secure login with roles (ADMIN, SUPER_ADMIN)
- Customers: add, edit, delete, search
- Items: add, edit, delete, search; automatic stock management via database triggers
- Billing: create multi-item bills, view history, print receipt, delete (SUPER_ADMIN)
- Email receipts: send HTML receipt to the customer via SMTP
- Settings: change your username/password, manage users (SUPER_ADMIN)
- **Automatic Stock Management**: Stock is automatically decremented when bills are created and restored when bills are deleted using MySQL triggers

## Database Triggers
The system uses MySQL triggers to automatically manage stock levels:
- **Stock Decrement**: When a bill item is added, the item's stock is automatically decreased
- **Stock Restore**: When a bill item is deleted, the item's stock is automatically restored
- **Stock Validation**: Triggers prevent stock from going negative by raising an error
- **Stock Updates**: When bill item quantities are modified, stock is adjusted accordingly
- **Bill Deletion**: When bills are deleted, all bill items are deleted first to trigger proper stock restoration

## Quick Start

Requirements: JDK 8+, Maven 3.8+, MySQL 8.x, Tomcat 9.x

1) Build
```powershell
mvn clean package
```

2) Deploy
- Copy `target/edu-billing.war` to Tomcat `webapps/`
- Start Tomcat, open: `http://localhost:8080/edu-billing/`

3) First login
- Seed a user in MySQL (example):
```sql
INSERT INTO users (username, password, role) VALUES ('admin', 'admin', 'SUPER_ADMIN');
```
- Sign in at `/login` 

## How to use
- Dashboard: quick links to Customers, Items, Billing, Help, Settings
- Customers: add/edit/delete, search by name/account/address/phone/email
- Items: add/edit/delete, set price and stock; search by name/description
- Billing: select a customer, add items (qty auto-calculates totals), create bill
  - Print receipt with the "Print Receipt" button
  - Email receipt with "Send Receipt" (requires SMTP config)
- Settings:
  - Account: change your username
  - Security: change password
  - User Management (SUPER_ADMIN): add/delete users, update roles (ADMIN/SUPER_ADMIN)

## Minimal configuration
- Database (default in code): `jdbc:mysql://127.0.0.1:3306/pahana`, user `root`, password empty
  - Change in `src/main/java/com/pahana/dao/DBConnection.java` if needed
  - Run `src/main/resources/db/schema.sql` to create tables and sample data
  - Run `src/main/resources/db/triggers.sql` to set up automatic stock management triggers
- Email (optional, for receipts): set environment variables before starting Tomcat
  - `MAIL_HOST` (default: smtp.gmail.com)
  - `MAIL_PORT` (default: 587)
  - `MAIL_TLS` (default: true)
  - `MAIL_SSL` (default: false)
  - `MAIL_USER`, `MAIL_PASS` (required)
  - `MAIL_FROM` (optional)

## Roles
- ADMIN: manage customers/items, create bills
- SUPER_ADMIN: ADMIN + delete bills/customers/items and manage users/roles

Notes:
- Passwords can be bcrypt-hashed (recommended). For quick start, plaintext works (dev only).
- Authentication is enforced globally; unauthenticated users are redirected to `/login`.
