-- DBMS Mini Project: Hotel Management System Schema
-- Technology: MySQL

-- 1. Create Database
CREATE DATABASE IF NOT EXISTS hotel_db;
USE hotel_db;

-- 2. Tables Definition

-- a) Guests
CREATE TABLE IF NOT EXISTS guests (
    guest_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    phone VARCHAR(15) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    address TEXT,
    id_proof VARCHAR(50) NOT NULL
);

-- b) Rooms
CREATE TABLE IF NOT EXISTS rooms (
    room_id INT AUTO_INCREMENT PRIMARY KEY,
    room_number VARCHAR(10) UNIQUE NOT NULL,
    room_type ENUM('Standard', 'Deluxe', 'Suite', 'Executive') NOT NULL,
    price_per_night DECIMAL(10, 2) NOT NULL CHECK (price_per_night > 0),
    status ENUM('Available', 'Occupied', 'Maintenance', 'Out_of_Service') DEFAULT 'Available'
);

-- c) Reservations
CREATE TABLE IF NOT EXISTS reservations (
    reservation_id INT AUTO_INCREMENT PRIMARY KEY,
    guest_id INT,
    room_id INT,
    check_in_date DATE NOT NULL,
    check_out_date DATE NOT NULL,
    total_amount DECIMAL(10, 2),
    booking_status ENUM('Confirmed', 'Cancelled', 'Checked_out') DEFAULT 'Confirmed',
    FOREIGN KEY (guest_id) REFERENCES guests(guest_id) ON DELETE CASCADE,
    FOREIGN KEY (room_id) REFERENCES rooms(room_id) ON DELETE SET NULL,
    CONSTRAINT chk_dates CHECK (check_out_date > check_in_date)
);

-- d) Payments
CREATE TABLE IF NOT EXISTS payments (
    payment_id INT AUTO_INCREMENT PRIMARY KEY,
    reservation_id INT,
    payment_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    amount DECIMAL(10, 2) NOT NULL,
    payment_method ENUM('Cash', 'Card', 'UPI', 'Net Banking') NOT NULL,
    FOREIGN KEY (reservation_id) REFERENCES reservations(reservation_id) ON DELETE CASCADE
);

-- e) Staff
CREATE TABLE IF NOT EXISTS staff (
    staff_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    role VARCHAR(50) NOT NULL,
    salary DECIMAL(10, 2) NOT NULL CHECK (salary > 0),
    phone VARCHAR(15) UNIQUE NOT NULL
);

-- f) Services
CREATE TABLE IF NOT EXISTS services (
    service_id INT AUTO_INCREMENT PRIMARY KEY,
    service_name VARCHAR(50) NOT NULL,
    service_charge DECIMAL(10, 2) NOT NULL CHECK (service_charge >= 0)
);

-- g) Service_Usage (Linking Table)
CREATE TABLE IF NOT EXISTS service_usage (
    usage_id INT AUTO_INCREMENT PRIMARY KEY,
    reservation_id INT,
    service_id INT,
    quantity INT DEFAULT 1 CHECK (quantity > 0),
    total_cost DECIMAL(10, 2),
    FOREIGN KEY (reservation_id) REFERENCES reservations(reservation_id) ON DELETE CASCADE,
    FOREIGN KEY (service_id) REFERENCES services(service_id)
);

-- 3. Views for Reporting

-- View for Comprehensive Stay Audit (FETCHING DATA FROM 3 TABLES)
CREATE OR REPLACE VIEW reservation_report_view AS
SELECT 
    r.reservation_id, 
    g.name AS guest_name, 
    rm.room_type, 
    r.check_in_date, 
    r.check_out_date, 
    r.total_amount, 
    r.booking_status
FROM reservations r
JOIN guests g ON r.guest_id = g.guest_id
JOIN rooms rm ON r.room_id = rm.room_id;

-- View for Revenue Report (Aggregates)
CREATE OR REPLACE VIEW revenue_report_view AS
SELECT SUM(amount) AS total_revenue, AVG(amount) AS average_payment
FROM payments;

-- 4. Triggers

DELIMITER //

-- Trigger: After reservation insert -> update room status to 'Occupied'
CREATE TRIGGER after_reservation_insert
AFTER INSERT ON reservations
FOR EACH ROW
BEGIN
    UPDATE rooms SET status = 'Occupied' WHERE room_id = NEW.room_id;
END //

-- Trigger: After checkout -> update room status to 'Available'
CREATE TRIGGER after_reservation_checkout
AFTER UPDATE ON reservations
FOR EACH ROW
BEGIN
    IF NEW.booking_status = 'Checked-out' OR NEW.booking_status = 'Cancelled' THEN
        UPDATE rooms SET status = 'Available' WHERE room_id = NEW.room_id;
    END IF;
END //

-- Trigger: Auto calculate total_amount before inserting reservation
CREATE TRIGGER before_reservation_insert
BEFORE INSERT ON reservations
FOR EACH ROW
BEGIN
    DECLARE days INT;
    DECLARE room_price DECIMAL(10,2);
    SET days = DATEDIFF(NEW.check_out_date, NEW.check_in_date);
    SELECT price_per_night INTO room_price FROM rooms WHERE room_id = NEW.room_id;
    SET NEW.total_amount = days * room_price;
END //

DELIMITER ;
