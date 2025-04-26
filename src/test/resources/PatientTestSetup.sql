SET FOREIGN_KEY_CHECKS=0;

TRUNCATE TABLE doctor;
TRUNCATE TABLE user_details;
TRUNCATE TABLE appointment;

SET FOREIGN_KEY_CHECKS=1;

INSERT INTO doctor (fullName, dateOfBirth, qualification, specialist, email, phone, password)
VALUES ('Sally Smith', '1978-01-06', 'MD/PhD', 'Cardiology', 'ssmith@gmail.com', '567-555-4673', 'S@lly!');

INSERT INTO doctor (fullName, dateOfBirth, qualification, specialist, email, phone, password)
VALUES ('Steve Winthrop', '1995-05-17', 'MD', 'Plastic Surgery', 'stevew@gmail.com', '322-555-6123', 'St3v3@');

INSERT INTO user_details (full_name, email, password)
VALUES ('Grace Green', 'ggreen@gmail.com', 'Gr@c3!');

INSERT INTO user_details (full_name, email, password)
VALUES ('Rebecca Millstone', 'becmill@gmail.com', 'B3cc@?');

INSERT INTO appointment (userId, fullName, gender, age, appointmentDate, email, phone, diseases, doctorId, address, status)
VALUES (1, 'Grace Green', '2003-12-18', '21', '2025-05-02', 'ggreen@gmail.com', '200-555-6789', 'Unknown', '1', '321 Elm St.', 'Scheduled');

INSERT INTO appointment (userId, fullName, gender, age, appointmentDate, email, phone, diseases, doctorId, address, status)
VALUES (2, 'Rebecca Millstone', '2000-04-01', '25', '2025-05-14', 'becmill@gmail.com', '609-555-1221', 'Unknown', '2', '457 First St.', 'Scheduled');