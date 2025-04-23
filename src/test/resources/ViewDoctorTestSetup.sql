SET FOREIGN_KEY_CHECKS=0;

TRUNCATE TABLE doctor;

SET FOREIGN_KEY_CHECKS=1;

INSERT INTO doctor (fullName, dateOfBirth, qualification, specialist, email, phone, password)
VALUES ('Sally Smith', '1978-01-06', 'MD/PhD', 'Cardiology', 'ssmith@gmail.com', '567-555-4673', 'Sally!');

INSERT INTO doctor (fullName, dateOfBirth, qualification, specialist, email, phone, password)
VALUES ('Steve Winthrop', '1995-05-17', 'MD', 'Plastic Surgery', 'stevew@gmail.com', '322-555-6123', 'Steve');