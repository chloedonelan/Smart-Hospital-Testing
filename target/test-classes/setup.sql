SET FOREIGN_KEY_CHECKS = 0;

-- Drop tables in the correct order
DROP TABLE IF EXISTS appointment;
DROP TABLE IF EXISTS user_details;
DROP TABLE IF EXISTS specialist;
DROP TABLE IF EXISTS doctor;

-- Re-enable foreign key checks
SET FOREIGN_KEY_CHECKS = 1;

-- Create the user_details table
CREATE TABLE user_details (
                              id INT AUTO_INCREMENT PRIMARY KEY,
                              full_name VARCHAR(255) NOT NULL,
                              email VARCHAR(255) NOT NULL UNIQUE,
                              password VARCHAR(255) NOT NULL
);

-- Create the specialist table
CREATE TABLE specialist (
                            id INT AUTO_INCREMENT PRIMARY KEY,
                            specialist_name VARCHAR(255) NOT NULL
);

-- Create the doctor table
CREATE TABLE doctor (
                        id INT AUTO_INCREMENT PRIMARY KEY,
                        fullName VARCHAR(255) NOT NULL,
                        dateOfBirth VARCHAR(50) NOT NULL,
                        qualification VARCHAR(255) NOT NULL,
                        specialist VARCHAR(255) NOT NULL,
                        email VARCHAR(255) NOT NULL UNIQUE,
                        phone VARCHAR(20) NOT NULL,
                        password VARCHAR(255) NOT NULL
);

-- Create the appointment table
CREATE TABLE appointment (
                             id INT AUTO_INCREMENT PRIMARY KEY,
                             userId INT NOT NULL,
                             fullName VARCHAR(255) NOT NULL,
                             gender VARCHAR(10) NOT NULL,
                             age VARCHAR(5) NOT NULL,
                             appointmentDate VARCHAR(50) NOT NULL,
                             email VARCHAR(255) NOT NULL,
                             phone VARCHAR(20) NOT NULL,
                             diseases VARCHAR(255) NOT NULL,
                             doctorId INT NOT NULL,
                             address VARCHAR(500) NOT NULL,
                             status VARCHAR(255) DEFAULT 'Pending',
                             FOREIGN KEY (userId) REFERENCES user_details(id) ON DELETE CASCADE,
                             FOREIGN KEY (doctorId) REFERENCES doctor(id) ON DELETE CASCADE
);

-- Indexes for better performance
CREATE INDEX idx_appointment_userid ON appointment(userId);
CREATE INDEX idx_appointment_doctorid ON appointment(doctorId);
CREATE INDEX idx_doctor_specialist ON doctor(specialist);