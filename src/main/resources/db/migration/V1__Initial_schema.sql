-- Create users table
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    unique_id BINARY(16) NOT NULL UNIQUE,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    phone_number VARCHAR(20),
    password VARCHAR(255) NOT NULL,
    role ENUM('USER', 'SCANNER', 'ADMIN') NOT NULL DEFAULT 'USER',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Create data_location table
CREATE TABLE IF NOT EXISTS data_location (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    unique_id BINARY(16) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    latitude DECIMAL(10, 8) NOT NULL,
    longitude DECIMAL(11, 8) NOT NULL,
    address_line1 VARCHAR(255),
    address_line2 VARCHAR(255),
    city VARCHAR(100),
    state_province VARCHAR(100),
    postal_code VARCHAR(20),
    country VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Create entries table
CREATE TABLE IF NOT EXISTS entries (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    data_location_id BIGINT NOT NULL,
    temperature DECIMAL(4, 1),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (data_location_id) REFERENCES data_location(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Create indexes for better query performance
CREATE INDEX idx_entries_user_id ON entries(user_id);
CREATE INDEX idx_entries_data_location_id ON entries(data_location_id);
CREATE INDEX idx_entries_created_at ON entries(created_at);

-- Create a view for contact tracing
CREATE OR REPLACE VIEW contact_tracing AS
SELECT 
    e1.user_id as original_user_id,
    e2.user_id as contact_user_id,
    e1.data_location_id,
    e1.created_at as contact_time,
    TIMESTAMPDIFF(MINUTE, e1.created_at, e2.created_at) as minutes_apart
FROM 
    entries e1
JOIN 
    entries e2 ON e1.data_location_id = e2.data_location_id
    AND e1.user_id != e2.user_id
    AND ABS(TIMESTAMPDIFF(MINUTE, e1.created_at, e2.created_at)) <= 60; -- Within 60 minutes
