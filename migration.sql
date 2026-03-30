USE hotel_db;

-- 1. Align room_type ENUM
ALTER TABLE rooms MODIFY COLUMN room_type ENUM('Standard', 'Deluxe', 'Suite', 'Executive') NOT NULL;

-- 2. Align room status ENUM
ALTER TABLE rooms MODIFY COLUMN status ENUM('Available', 'Occupied', 'Maintenance', 'Out_of_Service') DEFAULT 'Available';

-- 3. Align booking_status ENUM
ALTER TABLE reservations MODIFY COLUMN booking_status ENUM('Confirmed', 'Cancelled', 'Checked_out') DEFAULT 'Confirmed';
