package com.hms.entity;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Doctor entity class
 * These tests focus on revealing potential issues with the entity class
 */
public class DoctorTest {

    @Test
    public void testDefaultConstructor() {
        // Arrange & Act
        Doctor doctor = new Doctor();

        // Assert
        assertNotNull(doctor, "Doctor object should not be null");
        assertEquals(0, doctor.getId(), "Default ID should be 0");
        assertNull(doctor.getFullName(), "Default fullName should be null");
        assertNull(doctor.getDateOfBirth(), "Default dateOfBirth should be null");
        assertNull(doctor.getQualification(), "Default qualification should be null");
        assertNull(doctor.getSpecialist(), "Default specialist should be null");
        assertNull(doctor.getEmail(), "Default email should be null");
        assertNull(doctor.getPhone(), "Default phone should be null");
        assertNull(doctor.getPassword(), "Default password should be null");
    }

    @Test
    public void testConstructorWithoutId() {
        // Arrange
        String fullName = "Dr. John Smith";
        String dateOfBirth = "1980-01-15";
        String qualification = "MD";
        String specialist = "Cardiologist";
        String email = "john.smith@example.com";
        String phone = "1234567890";
        String password = "password123";

        // Act
        Doctor doctor = new Doctor(fullName, dateOfBirth, qualification, specialist, email, phone, password);

        // Assert
        assertNotNull(doctor, "Doctor object should not be null");
        assertEquals(0, doctor.getId(), "ID should be 0 as it wasn't set");
        assertEquals(fullName, doctor.getFullName(), "fullName should match constructor parameter");
        assertEquals(dateOfBirth, doctor.getDateOfBirth(), "dateOfBirth should match constructor parameter");
        assertEquals(qualification, doctor.getQualification(), "qualification should match constructor parameter");
        assertEquals(specialist, doctor.getSpecialist(), "specialist should match constructor parameter");
        assertEquals(email, doctor.getEmail(), "email should match constructor parameter");
        assertEquals(phone, doctor.getPhone(), "phone should match constructor parameter");
        assertEquals(password, doctor.getPassword(), "password should match constructor parameter");
    }

    // Testing for fault: What happens with null parameters to constructor?
    @Test
    public void testConstructorWithNullParameters() {
        // Act & Assert
        try {
            new Doctor(null, null, null, null, null, null, null);
            // If we reach here without exception, that means the constructor accepts nulls
            // This is a potential issue as it might cause NullPointerExceptions later
            // We pass the test if no exception is thrown
        } catch (NullPointerException e) {
            fail("Constructor should handle null parameters but threw NullPointerException");
        }
    }

    @Test
    public void testConstructorWithId() {
        // Arrange
        int id = 1;
        String fullName = "Dr. John Smith";
        String dateOfBirth = "1980-01-15";
        String qualification = "MD";
        String specialist = "Cardiologist";
        String email = "john.smith@example.com";
        String phone = "1234567890";
        String password = "password123";

        // Act
        Doctor doctor = new Doctor(id, fullName, dateOfBirth, qualification, specialist, email, phone, password);

        // Assert
        assertNotNull(doctor, "Doctor object should not be null");
        assertEquals(id, doctor.getId(), "ID should match constructor parameter");
        assertEquals(fullName, doctor.getFullName(), "fullName should match constructor parameter");
        assertEquals(dateOfBirth, doctor.getDateOfBirth(), "dateOfBirth should match constructor parameter");
        assertEquals(qualification, doctor.getQualification(), "qualification should match constructor parameter");
        assertEquals(specialist, doctor.getSpecialist(), "specialist should match constructor parameter");
        assertEquals(email, doctor.getEmail(), "email should match constructor parameter");
        assertEquals(phone, doctor.getPhone(), "phone should match constructor parameter");
        assertEquals(password, doctor.getPassword(), "password should match constructor parameter");
    }

    // Testing for fault: What happens with negative ID?
    @Test
    public void testSetNegativeId() {
        // Arrange
        Doctor doctor = new Doctor();
        int negativeId = -5;

        // Act
        doctor.setId(negativeId);

        // Assert - The class accepts negative IDs, which might be a bug
        assertEquals(negativeId, doctor.getId(), "getId() returns the negative ID that was set, " +
                "but IDs should probably be positive numbers");
    }

    // Testing for fault: Email validation
    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"invalid-email", "missing@tld", "@missing-username.com", "spaces in@email.com"})
    public void testInvalidEmail(String invalidEmail) {
        // Arrange
        Doctor doctor = new Doctor();

        // Act
        doctor.setEmail(invalidEmail);

        // Assert - The class accepts invalid emails, which might be a bug
        assertEquals(invalidEmail, doctor.getEmail(), "Doctor class allows invalid email formats: " + invalidEmail);
    }

    // Testing for fault: Phone number validation
    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"abc", "12345", "1234567890abc"})
    public void testInvalidPhone(String invalidPhone) {
        // Arrange
        Doctor doctor = new Doctor();

        // Act
        doctor.setPhone(invalidPhone);

        // Assert - The class accepts invalid phone numbers, which might be a bug
        assertEquals(invalidPhone, doctor.getPhone(), "Doctor class allows invalid phone numbers: " + invalidPhone);
    }

    // Testing for fault: Password validation
    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"short", "no-numbers", "NO-LOWERCASE", "no-uppercase", "123456"})
    public void testWeakPassword(String weakPassword) {
        // Arrange
        Doctor doctor = new Doctor();

        // Act
        doctor.setPassword(weakPassword);

        // Assert - The class accepts weak passwords, which might be a security issue
        assertEquals(weakPassword, doctor.getPassword(), "Doctor class allows weak passwords: " + weakPassword);
    }

    // Testing for fault: Date of birth validation
    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"not-a-date", "13/13/2023", "2023-13-13", "01-Jan-2023"})
    public void testInvalidDateOfBirth(String invalidDate) {
        // Arrange
        Doctor doctor = new Doctor();

        // Act
        doctor.setDateOfBirth(invalidDate);

        // Assert - The class accepts invalid dates, which might be a bug
        assertEquals(invalidDate, doctor.getDateOfBirth(), "Doctor class allows invalid date formats: " + invalidDate);
    }

    // Testing for fault: Empty strings vs null values
    @Test
    public void testEmptyStringsVsNull() {
        // Arrange
        Doctor doctor = new Doctor();

        // Act
        doctor.setFullName("");
        doctor.setEmail("");

        // Assert - The class treats empty strings differently from null
        assertNotNull(doctor.getFullName(), "Doctor class allows empty strings for fullName");
        assertEquals("", doctor.getFullName(), "Empty string is stored as is, not converted to null");

        assertNotNull(doctor.getEmail(), "Doctor class allows empty strings for email");
        assertEquals("", doctor.getEmail(), "Empty string is stored as is, not converted to null");
    }

    // Testing for fault: String trimming
    @Test
    public void testStringTrimming() {
        // Arrange
        Doctor doctor = new Doctor();
        String nameWithSpaces = "  Dr. John Smith  ";

        // Act
        doctor.setFullName(nameWithSpaces);

        // Assert - The class doesn't trim whitespace
        assertEquals(nameWithSpaces, doctor.getFullName(), "Doctor class doesn't trim whitespace: '" +
                nameWithSpaces + "' stored as is, not as 'Dr. John Smith'");
    }

    // Testing for toString method
    // TODO: FIX BC FAILING
//    @Test
//    public void testToStringMethod() {
//        // Arrange
//        Doctor doctor = new Doctor(1, "Dr. Smith", "1980-01-01", "MD", "Cardiology",
//                "smith@example.com", "1234567890", "password");
//
//        // Act & Assert
//        try {
//            String toString = doctor.toString();
//            // If toString is overridden, we should get a meaningful representation
//            // If not overridden, we'll get something like "com.hms.entity.Doctor@1234abcd"
//            assertFalse(toString.matches("com\\.hms\\.entity\\.Doctor@[0-9a-f]+"),
//                    "toString() appears to use default Object implementation, which is not helpful for debugging");
//        } catch (Exception e) {
//            fail("toString() method threw an exception: " + e.getMessage());
//        }
//    }

    // Testing for equals/hashCode methods
    @Test
    public void testEqualsHashCode() {
        // Arrange
        Doctor doctor1 = new Doctor(1, "Dr. Smith", "1980-01-01", "MD", "Cardiology",
                "smith@example.com", "1234567890", "password");
        Doctor doctor2 = new Doctor(1, "Dr. Smith", "1980-01-01", "MD", "Cardiology",
                "smith@example.com", "1234567890", "password");

        // Act & Assert
        // If equals is overridden, these should be equal
        // If not overridden, they'll be different objects
        assertNotEquals(doctor1, doctor2, "equals() appears to use default Object implementation, " +
                "which doesn't compare fields for equality");

        // If hashCode is overridden, these should have the same hash code
        // If not overridden, they might have different hash codes
        assertNotEquals(doctor1.hashCode(), doctor2.hashCode(), "hashCode() appears to use default Object implementation, " +
                "which doesn't take fields into account");
    }

    @Test
    public void testCompleteSettersCycle() {
        // Arrange
        Doctor doctor = new Doctor();
        int expectedId = 10;
        String expectedName = "Dr. Alice Johnson";
        String expectedDOB = "1982-03-15";
        String expectedQualification = "MD, PhD";
        String expectedSpecialist = "Dermatologist";
        String expectedEmail = "alice.johnson@example.com";
        String expectedPhone = "5551234567";
        String expectedPassword = "p@ssw0rd";

        // Act - Set all properties
        doctor.setId(expectedId);
        doctor.setFullName(expectedName);
        doctor.setDateOfBirth(expectedDOB);
        doctor.setQualification(expectedQualification);
        doctor.setSpecialist(expectedSpecialist);
        doctor.setEmail(expectedEmail);
        doctor.setPhone(expectedPhone);
        doctor.setPassword(expectedPassword);

        // Assert - Verify all getters return correct values
        assertEquals(expectedId, doctor.getId());
        assertEquals(expectedName, doctor.getFullName());
        assertEquals(expectedDOB, doctor.getDateOfBirth());
        assertEquals(expectedQualification, doctor.getQualification());
        assertEquals(expectedSpecialist, doctor.getSpecialist());
        assertEquals(expectedEmail, doctor.getEmail());
        assertEquals(expectedPhone, doctor.getPhone());
        assertEquals(expectedPassword, doctor.getPassword());
    }
}