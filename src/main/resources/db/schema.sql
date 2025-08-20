
CREATE DATABASE IF NOT EXISTS pahana;
USE pahana;

CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role ENUM('ADMIN', 'SUPER_ADMIN') NOT NULL DEFAULT 'ADMIN',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS customers (
    id INT AUTO_INCREMENT PRIMARY KEY,
    account_number VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    address TEXT,
    telephone VARCHAR(20),
    email VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS items (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    price DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    quantity INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS bills (
    id INT AUTO_INCREMENT PRIMARY KEY,
    customer_id INT NOT NULL,
    total_units INT NOT NULL DEFAULT 0,
    total_amount DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES customers(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS bill_items (
    bill_item_id INT AUTO_INCREMENT PRIMARY KEY,
    bill_id INT NOT NULL,
    item_id INT NOT NULL,
    quantity INT NOT NULL,
    unit_price DECIMAL(10,2) NOT NULL,
    total_price DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (bill_id) REFERENCES bills(id) ON DELETE CASCADE,
    FOREIGN KEY (item_id) REFERENCES items(id) ON DELETE CASCADE
);

INSERT IGNORE INTO users (username, password, role) VALUES ('admin', '1234', 'SUPER_ADMIN');

INSERT IGNORE INTO customers (account_number, name, address, telephone, email) VALUES 
('ACC001', 'John Doe', '123 Main St, Colombo', '0771234567', 'john.doe@email.com'),
('ACC002', 'Jane Smith', '456 Park Ave, Kandy', '0777654321', 'jane.smith@email.com'),
('ACC003', 'Bob Johnson', '789 High St, Galle', '0776543210', 'bob.johnson@email.com');

INSERT IGNORE INTO items (name, description, price, quantity) VALUES 
('Exercise Book', 'A4 size exercise book with 200 pages', 45.00, 100),
('Pencil HB', 'Standard HB pencil for writing', 15.00, 200),
('Ballpoint Pen', 'Blue ink ballpoint pen', 25.00, 150),
('Ruler', '30cm plastic ruler', 35.00, 80),
('Eraser', 'White rubber eraser', 10.00, 120),
('Geometry Set', 'Complete geometry set with compass and protractor', 350.00, 50);
