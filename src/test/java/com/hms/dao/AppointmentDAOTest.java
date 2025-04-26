package com.hms.dao;

import com.hms.entity.Appointment;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;

import org.junit.jupiter.api.*;

import java.sql.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class AppointmentDAOTest {
  
  private static Connection conn;
  private AppointmentDAO apptDAO;
  
  @BeforeAll
  public static void setupDB() throws Exception {
//    conn = DriverManager.getConnection(
//        "jdbc:mysql://localhost:3306/?allowMultiQueries=true",
//        "root", "rootuser"
//    );
    
    // Now switch connection to hospital_db
    conn = DriverManager.getConnection(
        "jdbc:mysql://localhost:3306/hospital_db",
        "root", "root"
    );
  }
  
  @BeforeEach
  public void setup() throws SQLException {
    conn.setAutoCommit(false);
    apptDAO = new AppointmentDAO(conn);
    
    Statement stmt = conn.createStatement();
    stmt.execute("SET FOREIGN_KEY_CHECKS=0");
  
    // reset auto-increment and clear data in tables
    stmt.execute("TRUNCATE TABLE appointment");
    stmt.execute("TRUNCATE TABLE user_details");
    stmt.execute("TRUNCATE TABLE specialist");
    stmt.execute("TRUNCATE TABLE doctor");
  }
  
  @AfterEach
  public void rollback() throws SQLException {
    conn.rollback(); // undo db changes
    Statement stmt = conn.createStatement();
    stmt.execute("SET FOREIGN_KEY_CHECKS = 1");
  }
  
  @AfterAll
  public static void cleanup() throws SQLException {
    if (conn != null) conn.close();
  }
  
  @Nested
  public class ConstructorTests {
    // null is an interesting value, should be handled appropriately
    // FAULT FOUND: does not throw an exception
    @Test
    public void testNullConnection() {
      assertThrows(IllegalArgumentException.class, () -> {
        AppointmentDAO aDAO = new AppointmentDAO(null);
      });
    }
    
    // valid connection should be tested to make sure constructor works as expected
    @Test
    public void testValidConnection() {
      assertDoesNotThrow(() -> {
        AppointmentDAO aDAO = new AppointmentDAO(conn);
      });
    }
  }
  
  @Nested
  public class AddAppointmentTests {
    // null is an interesting value, should be handled appropriately
    @Test
    public void testAddNullAppointment() {
      assertAll(
          () -> assertFalse(apptDAO.addAppointment(null)),
          () -> assertDoesNotThrow(() -> apptDAO.addAppointment(null))
      );
    }
    
    // using default Appointment constructor (instead of parameterized constructor) may lead to unexpected behavior
    // when parameters aren't explicitly given values
    @Test
    public void testAddInvalidAppointmentDefaultConstructor() {
      Appointment a = new Appointment();
      assertAll(
          () -> assertFalse(apptDAO.addAppointment(a)),
          () -> assertDoesNotThrow(() -> apptDAO.addAppointment(a))
      );
    }
    
    // uses default Appointment constructor, then sets values manually
    @Test
    public void testAddValidAppointmentDefaultConstructor() {
      Appointment a = new Appointment();
      a.setUserId(1);
      a.setFullName("Sample Patient");
      a.setGender("Male");
      a.setAge("30");
      a.setAppointmentDate("5-20-2025");
      a.setEmail("patient@jhu.edu");
      a.setPhone("678-555-8989");
      a.setDiseases("Liver");
      a.setDoctorId(9);
      a.setAddress("2300 St. Paul St.");
      a.setStatus("Confirmed");
      
      assertAll(
          () -> assertTrue(apptDAO.addAppointment(a)),
          () -> assertDoesNotThrow(() -> apptDAO.addAppointment(a))
      );
    }
    
    // tests adding an appointment made with the first parameterized constructor
    // of Appointment
    @Test
    public void testAddValidAppointmentParameterizedConstructorOne() {
      Appointment a = new Appointment(10, 3, "Sample Patient", "Female", "22", "4-30-2025", "user@gmail.com", "920-555-6769", "Throat", 5, "100 N Charles St", "Scheduled");
      
      assertAll(
          () -> assertTrue(apptDAO.addAppointment(a)),
          () -> assertDoesNotThrow(() -> apptDAO.addAppointment(a))
      );
    }
  
    // tests adding an appointment made with the second parameterized constructor
    // of Appointment
    @Test
    public void testAddValidAppointmentParameterizedConstructorTwo() {
      Appointment a = new Appointment(1, "Sample Patient", "Female", "22", "4-30-2025", "user@gmail.com", "920-555-6769", "Throat", 5, "100 N Charles St", "Scheduled");
  
      assertAll(
          () -> assertTrue(apptDAO.addAppointment(a)),
          () -> assertDoesNotThrow(() -> apptDAO.addAppointment(a))
      );
    }
  }
  
  @Nested
  public class GetAllAppointmentByLoginUserTests {
    // Appointments from an invalid user id value (negative)
    @Test
    public void testInvalidUserId() {
      List<Appointment> expected = new ArrayList<Appointment>(); // ArrayList is assumed
      assertEquals(expected.size(), apptDAO.getAllAppointmentByLoginUser(-1).size());
    }
    
    // Appointments from a user id that is valid but there are no Appointments in the database
    @Test
    public void testUserNoAppointmentsAtAll() {
      List<Appointment> expected = new ArrayList<Appointment>(); // ArrayList is assumed
      assertEquals(expected.size(), apptDAO.getAllAppointmentByLoginUser(189).size());
    }
  
    // Appointments from a user id that is valid but has no Appointments
    // (other users' Appointments do exist in the database)
    @Test
    public void testUserNoAppointmentsOtherAppointmentsExist() {
      Appointment a = new Appointment(3, "Sample Patient", "Female", "22", "4-30-2025", "user@gmail.com", "920-555-6769", "Throat", 5, "100 N Charles St", "Scheduled");
      apptDAO.addAppointment(a);
      a = new Appointment(1, "Other Patient", "Male", "60", "6-2-1-2025", "other@gmail.com", "899-555-1000", "Heart", 60, "100 N Charles St", "Scheduled");
      apptDAO.addAppointment(a);
      a = new Appointment(2, "Another Patient", "Female", "20", "5-1-2025", "another@gmail.com", "555-200-1212", "Knee", 2, "100 N Charles St", "Scheduled");
      apptDAO.addAppointment(a);
      
      List<Appointment> expected = new ArrayList<Appointment>(); // ArrayList is assumed
      assertEquals(expected.size(), apptDAO.getAllAppointmentByLoginUser(189).size());
    }
    
    // Appointments from a user id that is valid and has 1 appointment in the database
    // FAULT DETECTED: Appointment id is incorrect in returned list (should be 20, but is 1 here; seems to be
    // overwritten to match database row number)
    @Test
    public void testUserOneAppointment() {
      Appointment a = new Appointment(20, 3, "Sample Patient", "Female", "22", "4-30-2025", "user@gmail.com", "920-555-6769", "Throat", 5, "100 N Charles St", "Scheduled");
      apptDAO.addAppointment(a);
      
      List<Appointment> expected = new ArrayList<Appointment>(); // ArrayList is assumed
      expected.add(a);
      
      List<Appointment> result = apptDAO.getAllAppointmentByLoginUser(3);
      
      assertEquals(expected.size(), result.size());
      
      Appointment expectedAppt = expected.get(0);
      Appointment resultAppt = result.get(0);
      
      assertAll(
          () -> assertEquals(expectedAppt.getId(), resultAppt.getId()),
          () -> assertEquals(expectedAppt.getUserId(), resultAppt.getUserId()),
          () -> assertEquals(expectedAppt.getFullName(), resultAppt.getFullName()),
          () -> assertEquals(expectedAppt.getGender(), resultAppt.getGender()),
          () -> assertEquals(expectedAppt.getAge(), resultAppt.getAge()),
          () -> assertEquals(expectedAppt.getAppointmentDate(), resultAppt.getAppointmentDate()),
          () -> assertEquals(expectedAppt.getEmail(), resultAppt.getEmail()),
          () -> assertEquals(expectedAppt.getPhone(), resultAppt.getPhone()),
          () -> assertEquals(expectedAppt.getDiseases(), resultAppt.getDiseases()),
          () -> assertEquals(expectedAppt.getDoctorId(), resultAppt.getDoctorId()),
          () -> assertEquals(expectedAppt.getAddress(), resultAppt.getAddress()),
          () -> assertEquals(expectedAppt.getStatus(), resultAppt.getStatus())
      );
    }
  
    // Appointments from a user id that is valid and has multiple appointments in the database
    @Test
    public void testUserMultipleAppointments() {
      List<Appointment> expected = new ArrayList<Appointment>(); // ArrayList is assumed
      
      Appointment a = new Appointment(3, "Sample Patient", "Female", "22", "4-30-2025", "user@gmail.com", "920-555-6769", "Throat", 5, "100 N Charles St", "Scheduled");
      apptDAO.addAppointment(a);
      expected.add(a);
      a = new Appointment(3, "Sample Patient", "Female", "22", "5-10-2025", "user@gmail.com", "920-555-6769", "Kidney", 67, "500 N Charles St", "Scheduled");
      apptDAO.addAppointment(a);
      expected.add(a);
      a = new Appointment(3, "Sample Patient", "Female", "22", "6-7-2025", "user@gmail.com", "920-555-6769", "Neurological", 90, "2300 St. Paul St.", "Scheduled");
      apptDAO.addAppointment(a);
      expected.add(a);
  
      List<Appointment> result = apptDAO.getAllAppointmentByLoginUser(3);
  
      assertEquals(expected.size(), result.size());
      
      for (int i = 0; i < expected.size(); i++) {
        Appointment expectedAppt = expected.get(i);
        Appointment resultAppt = result.get(i);
        
        assertAll(
            () -> assertEquals(expectedAppt.getUserId(), resultAppt.getUserId()),
            () -> assertEquals(expectedAppt.getFullName(), resultAppt.getFullName()),
            () -> assertEquals(expectedAppt.getGender(), resultAppt.getGender()),
            () -> assertEquals(expectedAppt.getAge(), resultAppt.getAge()),
            () -> assertEquals(expectedAppt.getAppointmentDate(), resultAppt.getAppointmentDate()),
            () -> assertEquals(expectedAppt.getEmail(), resultAppt.getEmail()),
            () -> assertEquals(expectedAppt.getPhone(), resultAppt.getPhone()),
            () -> assertEquals(expectedAppt.getDiseases(), resultAppt.getDiseases()),
            () -> assertEquals(expectedAppt.getDoctorId(), resultAppt.getDoctorId()),
            () -> assertEquals(expectedAppt.getAddress(), resultAppt.getAddress()),
            () -> assertEquals(expectedAppt.getStatus(), resultAppt.getStatus())
        );
      }
    }
    
    // Other Appointments exist in the database besides the desired user's Appointments
    // Ensures that incorrect Appointments aren't returned by the method
    @Test
    public void testOtherAppointmentsBesidesUserAppointmentsExist() {
      List<Appointment> expected = new ArrayList<Appointment>(); // ArrayList is assumed
  
      Appointment a = new Appointment(1, "Other Patient", "Male", "60", "4-30-2025", "other@gmail.com", "899-555-1000", "Hip", 5, "100 N Charles St", "Scheduled");
      apptDAO.addAppointment(a);
      
      a = new Appointment(3, "Sample Patient", "Female", "22", "4-30-2025", "user@gmail.com", "920-555-6769", "Throat", 5, "100 N Charles St", "Scheduled");
      apptDAO.addAppointment(a);
      expected.add(a);
      
      a = new Appointment(1, "Another Patient", "Female", "20", "5-1-2025", "another@gmail.com", "555-200-1212", "Knee", 2, "100 N Charles St", "Scheduled");
      apptDAO.addAppointment(a);
      
      a = new Appointment(3, "Sample Patient", "Female", "22", "5-10-2025", "user@gmail.com", "920-555-6769", "Kidney", 67, "500 N Charles St", "Scheduled");
      apptDAO.addAppointment(a);
      expected.add(a);
      
      a = new Appointment(1, "Other Patient", "Male", "60", "6-2-1-2025", "other@gmail.com", "899-555-1000", "Heart", 60, "100 N Charles St", "Scheduled");
      apptDAO.addAppointment(a);
      
      a = new Appointment(3, "Sample Patient", "Female", "22", "6-7-2025", "user@gmail.com", "920-555-6769", "Neurological", 90, "2300 St. Paul St.", "Scheduled");
      apptDAO.addAppointment(a);
      expected.add(a);
  
      List<Appointment> result = apptDAO.getAllAppointmentByLoginUser(3);
  
      assertEquals(expected.size(), result.size());
  
      for (int i = 0; i < expected.size(); i++) {
        Appointment expectedAppt = expected.get(i);
        Appointment resultAppt = result.get(i);
    
        assertAll(
            () -> assertEquals(expectedAppt.getUserId(), resultAppt.getUserId()),
            () -> assertEquals(expectedAppt.getFullName(), resultAppt.getFullName()),
            () -> assertEquals(expectedAppt.getGender(), resultAppt.getGender()),
            () -> assertEquals(expectedAppt.getAge(), resultAppt.getAge()),
            () -> assertEquals(expectedAppt.getAppointmentDate(), resultAppt.getAppointmentDate()),
            () -> assertEquals(expectedAppt.getEmail(), resultAppt.getEmail()),
            () -> assertEquals(expectedAppt.getPhone(), resultAppt.getPhone()),
            () -> assertEquals(expectedAppt.getDiseases(), resultAppt.getDiseases()),
            () -> assertEquals(expectedAppt.getDoctorId(), resultAppt.getDoctorId()),
            () -> assertEquals(expectedAppt.getAddress(), resultAppt.getAddress()),
            () -> assertEquals(expectedAppt.getStatus(), resultAppt.getStatus())
        );
      }
    }
  }
  
  @Nested
  public class getAllAppointmentByLoginDoctorTests {
    // Appointments from an invalid doctor id value (negative)
    @Test
    public void testInvalidDoctorId() {
      List<Appointment> expected = new ArrayList<Appointment>(); // ArrayList is assumed
      assertEquals(expected.size(), apptDAO.getAllAppointmentByLoginDoctor(-1).size());
    }
  
    // Appointments from a doctor id that is valid but there are no Appointments in the database
    @Test
    public void testDoctorNoAppointmentsAtAll() {
      List<Appointment> expected = new ArrayList<Appointment>(); // ArrayList is assumed
      assertEquals(expected.size(), apptDAO.getAllAppointmentByLoginDoctor(23).size());
    }
  
    // Appointments from a doctor id that is valid but has no Appointments
    // (other Appointments do exist in the database)
    @Test
    public void testDoctorNoAppointmentsOtherAppointmentsExist() {
      Appointment a = new Appointment(3, "Sample Patient", "Female", "22", "4-30-2025", "user@gmail.com", "920-555-6769", "Throat", 5, "100 N Charles St", "Scheduled");
      apptDAO.addAppointment(a);
      a = new Appointment(1, "Other Patient", "Male", "60", "6-2-1-2025", "other@gmail.com", "899-555-1000", "Heart", 60, "100 N Charles St", "Scheduled");
      apptDAO.addAppointment(a);
      a = new Appointment(2, "Another Patient", "Female", "20", "5-1-2025", "another@gmail.com", "555-200-1212", "Knee", 2, "100 N Charles St", "Scheduled");
      apptDAO.addAppointment(a);
    
      List<Appointment> expected = new ArrayList<Appointment>(); // ArrayList is assumed
      assertEquals(expected.size(), apptDAO.getAllAppointmentByLoginDoctor(999).size());
    }
  
    // Appointments from a doctor id that is valid and has 1 appointment in the database
    // FAULT DETECTED: Appointment id is incorrect in returned list (should be 67, but is 1 here; seems to be
    // overwritten to match database row number) [same fault that getAllAppointmentByLoginUser() has]
    @Test
    public void testDoctorOneAppointment() {
      Appointment a = new Appointment(67, 3, "Sample Patient", "Female", "22", "4-30-2025", "user@gmail.com", "920-555-6769", "Throat", 5, "100 N Charles St", "Scheduled");
      apptDAO.addAppointment(a);
    
      List<Appointment> expected = new ArrayList<Appointment>(); // ArrayList is assumed
      expected.add(a);
    
      List<Appointment> result = apptDAO.getAllAppointmentByLoginDoctor(5);
    
      assertEquals(expected.size(), result.size());
    
      Appointment expectedAppt = expected.get(0);
      Appointment resultAppt = result.get(0);
    
      assertAll(
          () -> assertEquals(expectedAppt.getId(), resultAppt.getId()),
          () -> assertEquals(expectedAppt.getUserId(), resultAppt.getUserId()),
          () -> assertEquals(expectedAppt.getFullName(), resultAppt.getFullName()),
          () -> assertEquals(expectedAppt.getGender(), resultAppt.getGender()),
          () -> assertEquals(expectedAppt.getAge(), resultAppt.getAge()),
          () -> assertEquals(expectedAppt.getAppointmentDate(), resultAppt.getAppointmentDate()),
          () -> assertEquals(expectedAppt.getEmail(), resultAppt.getEmail()),
          () -> assertEquals(expectedAppt.getPhone(), resultAppt.getPhone()),
          () -> assertEquals(expectedAppt.getDiseases(), resultAppt.getDiseases()),
          () -> assertEquals(expectedAppt.getDoctorId(), resultAppt.getDoctorId()),
          () -> assertEquals(expectedAppt.getAddress(), resultAppt.getAddress()),
          () -> assertEquals(expectedAppt.getStatus(), resultAppt.getStatus())
      );
    }
  
    // Appointments from a doctor id that is valid and has multiple appointments in the database
    @Test
    public void testDoctorMultipleAppointments() {
      List<Appointment> expected = new ArrayList<Appointment>(); // ArrayList is assumed
    
      Appointment a = new Appointment(1, "Sample Patient", "Female", "22", "4-30-2025", "user@gmail.com", "920-555-6769", "Throat", 5, "100 N Charles St", "Scheduled");
      apptDAO.addAppointment(a);
      expected.add(a);
      a = new Appointment(2, "Sample Patient", "Female", "22", "5-10-2025", "user@gmail.com", "920-555-6769", "Kidney", 5, "500 N Charles St", "Scheduled");
      apptDAO.addAppointment(a);
      expected.add(a);
      a = new Appointment(3, "Sample Patient", "Female", "22", "6-7-2025", "user@gmail.com", "920-555-6769", "Neurological", 5, "2300 St. Paul St.", "Scheduled");
      apptDAO.addAppointment(a);
      expected.add(a);
    
      List<Appointment> result = apptDAO.getAllAppointmentByLoginDoctor(5);
    
      assertEquals(expected.size(), result.size());
    
      for (int i = 0; i < expected.size(); i++) {
        Appointment expectedAppt = expected.get(i);
        Appointment resultAppt = result.get(i);
      
        assertAll(
            () -> assertEquals(expectedAppt.getUserId(), resultAppt.getUserId()),
            () -> assertEquals(expectedAppt.getFullName(), resultAppt.getFullName()),
            () -> assertEquals(expectedAppt.getGender(), resultAppt.getGender()),
            () -> assertEquals(expectedAppt.getAge(), resultAppt.getAge()),
            () -> assertEquals(expectedAppt.getAppointmentDate(), resultAppt.getAppointmentDate()),
            () -> assertEquals(expectedAppt.getEmail(), resultAppt.getEmail()),
            () -> assertEquals(expectedAppt.getPhone(), resultAppt.getPhone()),
            () -> assertEquals(expectedAppt.getDiseases(), resultAppt.getDiseases()),
            () -> assertEquals(expectedAppt.getDoctorId(), resultAppt.getDoctorId()),
            () -> assertEquals(expectedAppt.getAddress(), resultAppt.getAddress()),
            () -> assertEquals(expectedAppt.getStatus(), resultAppt.getStatus())
        );
      }
    }
  
    // Other Appointments exist in the database besides the desired doctor's Appointments
    // Ensures that incorrect Appointments aren't returned by the method
    @Test
    public void testOtherAppointmentsBesidesDoctorAppointmentsExist() {
      List<Appointment> expected = new ArrayList<Appointment>(); // ArrayList is assumed
    
      Appointment a = new Appointment(1, "Other Patient", "Male", "60", "4-30-2025", "other@gmail.com", "899-555-1000", "Hip", 998, "100 N Charles St", "Scheduled");
      apptDAO.addAppointment(a);
    
      a = new Appointment(3, "Sample Patient", "Female", "22", "4-30-2025", "user@gmail.com", "920-555-6769", "Throat", 50, "100 N Charles St", "Scheduled");
      apptDAO.addAppointment(a);
      expected.add(a);
    
      a = new Appointment(1, "Another Patient", "Female", "20", "5-1-2025", "another@gmail.com", "555-200-1212", "Knee", 240, "100 N Charles St", "Scheduled");
      apptDAO.addAppointment(a);
    
      a = new Appointment(3, "Sample Patient", "Female", "22", "5-10-2025", "user@gmail.com", "920-555-6769", "Kidney", 50, "500 N Charles St", "Scheduled");
      apptDAO.addAppointment(a);
      expected.add(a);
    
      a = new Appointment(1, "Other Patient", "Male", "60", "6-2-1-2025", "other@gmail.com", "899-555-1000", "Heart", 3789, "100 N Charles St", "Scheduled");
      apptDAO.addAppointment(a);
    
      a = new Appointment(3, "Sample Patient", "Female", "22", "6-7-2025", "user@gmail.com", "920-555-6769", "Neurological", 50, "2300 St. Paul St.", "Scheduled");
      apptDAO.addAppointment(a);
      expected.add(a);
    
      List<Appointment> result = apptDAO.getAllAppointmentByLoginDoctor(50);
    
      assertEquals(expected.size(), result.size());
    
      for (int i = 0; i < expected.size(); i++) {
        Appointment expectedAppt = expected.get(i);
        Appointment resultAppt = result.get(i);
      
        assertAll(
            () -> assertEquals(expectedAppt.getUserId(), resultAppt.getUserId()),
            () -> assertEquals(expectedAppt.getFullName(), resultAppt.getFullName()),
            () -> assertEquals(expectedAppt.getGender(), resultAppt.getGender()),
            () -> assertEquals(expectedAppt.getAge(), resultAppt.getAge()),
            () -> assertEquals(expectedAppt.getAppointmentDate(), resultAppt.getAppointmentDate()),
            () -> assertEquals(expectedAppt.getEmail(), resultAppt.getEmail()),
            () -> assertEquals(expectedAppt.getPhone(), resultAppt.getPhone()),
            () -> assertEquals(expectedAppt.getDiseases(), resultAppt.getDiseases()),
            () -> assertEquals(expectedAppt.getDoctorId(), resultAppt.getDoctorId()),
            () -> assertEquals(expectedAppt.getAddress(), resultAppt.getAddress()),
            () -> assertEquals(expectedAppt.getStatus(), resultAppt.getStatus())
        );
      }
    }
  }
  
  @Nested
  public class getAppointmentByIdTests {
    // Using knowledge that appointment ids are assigned chronologically by when added to the database
    // (found by fault in previous tests)
  
    // FAULT FOUND: does not throw an exception (documentation is lacking in this area, but there should be some form of
    // input validation. Seems to return null in this case, which isn't exactly good practice)
    @Test
    public void testInvalidId() {
      assertThrows(IllegalArgumentException.class, () -> {
        Appointment a = apptDAO.getAppointmentById(-1);
      });
    }
    
    // one appointment exists in the database, and that is the desired appointment
    // FAULT FOUND: if an id is not given to an Appointment object when it is created, it seems to be 0 (probably just
    // because that is the default value for an integer?) and never updated with the appropriate id from the database.
    // In this test case, Appointment a's id remains as 0 while the version of it from the database has id 1.
    @Test
    public void testOneAppointmentDesiredAppointmentExists() {
      Appointment a = new Appointment(3, "Sample Patient", "Female", "22", "4-30-2025", "user@gmail.com", "920-555-6769", "Throat", 5, "100 N Charles St", "Scheduled");
      apptDAO.addAppointment(a);
      
      Appointment resultAppt = apptDAO.getAppointmentById(1);
  
      assertAll(
          () -> assertEquals(a.getId(), resultAppt.getId()),
          () -> assertEquals(a.getUserId(), resultAppt.getUserId()),
          () -> assertEquals(a.getFullName(), resultAppt.getFullName()),
          () -> assertEquals(a.getGender(), resultAppt.getGender()),
          () -> assertEquals(a.getAge(), resultAppt.getAge()),
          () -> assertEquals(a.getAppointmentDate(), resultAppt.getAppointmentDate()),
          () -> assertEquals(a.getEmail(), resultAppt.getEmail()),
          () -> assertEquals(a.getPhone(), resultAppt.getPhone()),
          () -> assertEquals(a.getDiseases(), resultAppt.getDiseases()),
          () -> assertEquals(a.getDoctorId(), resultAppt.getDoctorId()),
          () -> assertEquals(a.getAddress(), resultAppt.getAddress()),
          () -> assertEquals(a.getStatus(), resultAppt.getStatus())
      );
    }
    
    // one Appointment exists in the database, and it is not the desired Appointment
    // (using knowledge from previous tests that this should return null, not throw an exception)
    @Test
    public void testOneAppointmentDesiredAppointmentDoesNotExist() {
      Appointment a = new Appointment(3, "Sample Patient", "Female", "22", "4-30-2025", "user@gmail.com", "920-555-6769", "Throat", 5, "100 N Charles St", "Scheduled");
      apptDAO.addAppointment(a);
      
      assertNull(apptDAO.getAppointmentById(10));
    }
    
    // multiple Appointments exist in the database, including the desired appointment
    @Test
    public void testMultipleAppointmentsDesiredAppointmentExists() {
      Appointment a = new Appointment(2, "Jane Doe", "Female", "12", "4-20-2025", "jane@gmail.com", "345-555-5555", "Flu", 9, "21 Walnut St.", "Past");
      apptDAO.addAppointment(a);
      
      Appointment expected = new Appointment(3, "Sample Patient", "Female", "22", "4-30-2025", "user@gmail.com", "920-555-6769", "Throat", 5, "100 N Charles St", "Confirmed");
      apptDAO.addAppointment(expected);
      
      a = new Appointment(1, "Other Patient", "Male", "99", "5-2-2025", "person@gmail.com", "555-555-5555", "Cancer", 1, "4561 Elm St.", "Scheduled");
      apptDAO.addAppointment(a);
  
      Appointment resultAppt = apptDAO.getAppointmentById(2);
  
      assertAll(
          () -> assertEquals(2, resultAppt.getId()),
          () -> assertEquals(expected.getUserId(), resultAppt.getUserId()),
          () -> assertEquals(expected.getFullName(), resultAppt.getFullName()),
          () -> assertEquals(expected.getGender(), resultAppt.getGender()),
          () -> assertEquals(expected.getAge(), resultAppt.getAge()),
          () -> assertEquals(expected.getAppointmentDate(), resultAppt.getAppointmentDate()),
          () -> assertEquals(expected.getEmail(), resultAppt.getEmail()),
          () -> assertEquals(expected.getPhone(), resultAppt.getPhone()),
          () -> assertEquals(expected.getDiseases(), resultAppt.getDiseases()),
          () -> assertEquals(expected.getDoctorId(), resultAppt.getDoctorId()),
          () -> assertEquals(expected.getAddress(), resultAppt.getAddress()),
          () -> assertEquals(expected.getStatus(), resultAppt.getStatus())
      );
    }
  
    // multiple Appointments exist in the database, and none of them are the desired Appointment
    // (using knowledge from previous tests that this should return null, not throw an exception)
    @Test
    public void testMultipleAppointmentsDesiredAppointmentDoesNotExist() {
      Appointment a = new Appointment(2, "Jane Doe", "Female", "12", "4-20-2025", "jane@gmail.com", "345-555-5555", "Flu", 9, "21 Walnut St.", "Past");
      apptDAO.addAppointment(a);
      
      a = new Appointment(3, "Sample Patient", "Female", "22", "4-30-2025", "user@gmail.com", "920-555-6769", "Throat", 5, "100 N Charles St", "Confirmed");
      apptDAO.addAppointment(a);
  
      a = new Appointment(1, "Other Patient", "Male", "99", "5-2-2025", "person@gmail.com", "555-555-5555", "Cancer", 1, "4561 Elm St.", "Scheduled");
      apptDAO.addAppointment(a);
  
      assertNull(apptDAO.getAppointmentById(100));
    }
  }
  
  @Nested
  public class updateDrAppointmentCommentStatusTests {
    // attempt to update an Appointment when there are none in the database
    // FAULT DETECTED: returns true when it should return false
    @Test
    public void testNoAppointments() {
      assertFalse(apptDAO.updateDrAppointmentCommentStatus(1, 1, "Flu"));
    }
  
    // update an Appointment when it is the only Appointment in the database
    @Test
    public void testOneAppointmentDesiredAppointmentExists() {
      Appointment a = new Appointment(3, "Sample Patient", "Female", "22", "4-30-2025", "user@gmail.com", "920-555-6769", "Throat", 5, "100 N Charles St", "Scheduled");
      apptDAO.addAppointment(a);
      
      assertTrue(apptDAO.updateDrAppointmentCommentStatus(1, 5, "Tested positive for strep throat and prescribed medication"));
      
      Appointment resultAppt = apptDAO.getAppointmentById(1);
      
      assertAll(
          () -> assertEquals(1, resultAppt.getId()),
          () -> assertEquals(a.getUserId(), resultAppt.getUserId()),
          () -> assertEquals(a.getFullName(), resultAppt.getFullName()),
          () -> assertEquals(a.getGender(), resultAppt.getGender()),
          () -> assertEquals(a.getAge(), resultAppt.getAge()),
          () -> assertEquals(a.getAppointmentDate(), resultAppt.getAppointmentDate()),
          () -> assertEquals(a.getEmail(), resultAppt.getEmail()),
          () -> assertEquals(a.getPhone(), resultAppt.getPhone()),
          () -> assertEquals(a.getDiseases(), resultAppt.getDiseases()),
          () -> assertEquals(a.getDoctorId(), resultAppt.getDoctorId()),
          () -> assertEquals(a.getAddress(), resultAppt.getAddress()),
          () -> assertEquals("Tested positive for strep throat and prescribed medication", resultAppt.getStatus())
      );
    }
  
    // attempt to update an Appointment when there is 1 Appointment in the database, but it is not the Appointment to
    // be updated
    // FAULT DETECTED: returns true when it should return false (same as above)
    @Test
    public void testOneAppointmentNotDesiredAppointment() {
      Appointment a = new Appointment(3, "Sample Patient", "Female", "22", "4-30-2025", "user@gmail.com", "920-555-6769", "Throat", 5, "100 N Charles St", "Scheduled");
      apptDAO.addAppointment(a);
  
      assertFalse(apptDAO.updateDrAppointmentCommentStatus(98, 5, "Tested positive for strep throat and prescribed medication"));
    }
    
    // attempt to update an Appointment when it is the only Appointment in the database, but the wrong doctorId is
    // provided
    // FAULT DETECTED: returns true when it should return false (same as above)
    @Test
    public void testOneAppointmentDesiredAppointmentWrongDocId() {
      Appointment a = new Appointment(3, "Sample Patient", "Female", "22", "4-30-2025", "user@gmail.com", "920-555-6769", "Throat", 5, "100 N Charles St", "Scheduled");
      apptDAO.addAppointment(a);
  
      assertFalse(apptDAO.updateDrAppointmentCommentStatus(1, 476, "Tested positive for strep throat and prescribed medication"));
    }
  
    // update an Appointment when it is not the only Appointment in the database
    @Test
    public void testMultipleAppointmentsDesiredAppointmentExists() {
      Appointment a = new Appointment(1, "Other Patient", "Male", "60", "4-30-2025", "other@gmail.com", "899-555-1000", "Hip", 5, "100 N Charles St", "Scheduled");
      apptDAO.addAppointment(a);
  
      a = new Appointment(3, "Sample Patient", "Female", "22", "4-30-2025", "user@gmail.com", "920-555-6769", "Throat", 5, "100 N Charles St", "Scheduled");
      apptDAO.addAppointment(a);
  
      a = new Appointment(1, "Another Patient", "Female", "20", "5-1-2025", "another@gmail.com", "555-200-1212", "Knee", 2, "100 N Charles St", "Scheduled");
      apptDAO.addAppointment(a);
  
      Appointment expected = new Appointment(3, "Sample Patient", "Female", "22", "5-10-2025", "user@gmail.com", "920-555-6769", "Kidney", 67, "500 N Charles St", "Scheduled");
      apptDAO.addAppointment(expected);
  
      a = new Appointment(1, "Other Patient", "Male", "60", "6-2-1-2025", "other@gmail.com", "899-555-1000", "Heart", 60, "100 N Charles St", "Scheduled");
      apptDAO.addAppointment(a);
  
      a = new Appointment(3, "Sample Patient", "Female", "22", "6-7-2025", "user@gmail.com", "920-555-6769", "Neurological", 90, "2300 St. Paul St.", "Scheduled");
      apptDAO.addAppointment(a);
      
      assertTrue(apptDAO.updateDrAppointmentCommentStatus(4, 67, "Cancelled"));
  
      Appointment resultAppt = apptDAO.getAppointmentById(4);
      
      assertAll(
          () -> assertEquals(4, resultAppt.getId()),
          () -> assertEquals(expected.getUserId(), resultAppt.getUserId()),
          () -> assertEquals(expected.getFullName(), resultAppt.getFullName()),
          () -> assertEquals(expected.getGender(), resultAppt.getGender()),
          () -> assertEquals(expected.getAge(), resultAppt.getAge()),
          () -> assertEquals(expected.getAppointmentDate(), resultAppt.getAppointmentDate()),
          () -> assertEquals(expected.getEmail(), resultAppt.getEmail()),
          () -> assertEquals(expected.getPhone(), resultAppt.getPhone()),
          () -> assertEquals(expected.getDiseases(), resultAppt.getDiseases()),
          () -> assertEquals(expected.getDoctorId(), resultAppt.getDoctorId()),
          () -> assertEquals(expected.getAddress(), resultAppt.getAddress()),
          () -> assertEquals("Cancelled", resultAppt.getStatus())
      );
    }
  
    // attempt to update an Appointment when it is not in the database, but other Appointments are
    // FAULT DETECTED: returns true when it should return false (same as above)
    @Test
    public void testMultipleAppointmentsDesiredAppointmentDoesNotExist() {
      Appointment a = new Appointment(1, "Other Patient", "Male", "60", "4-30-2025", "other@gmail.com", "899-555-1000", "Hip", 5, "100 N Charles St", "Scheduled");
      apptDAO.addAppointment(a);
  
      a = new Appointment(3, "Sample Patient", "Female", "22", "4-30-2025", "user@gmail.com", "920-555-6769", "Throat", 5, "100 N Charles St", "Scheduled");
      apptDAO.addAppointment(a);
  
      a = new Appointment(1, "Another Patient", "Female", "20", "5-1-2025", "another@gmail.com", "555-200-1212", "Knee", 2, "100 N Charles St", "Scheduled");
      apptDAO.addAppointment(a);
  
      Appointment expected = new Appointment(3, "Sample Patient", "Female", "22", "5-10-2025", "user@gmail.com", "920-555-6769", "Kidney", 67, "500 N Charles St", "Scheduled");
      apptDAO.addAppointment(expected);
  
      a = new Appointment(1, "Other Patient", "Male", "60", "6-2-1-2025", "other@gmail.com", "899-555-1000", "Heart", 60, "100 N Charles St", "Scheduled");
      apptDAO.addAppointment(a);
  
      a = new Appointment(3, "Sample Patient", "Female", "22", "6-7-2025", "user@gmail.com", "920-555-6769", "Neurological", 90, "2300 St. Paul St.", "Scheduled");
      apptDAO.addAppointment(a);
  
      assertFalse(apptDAO.updateDrAppointmentCommentStatus(4670, 67, "Cancelled"));
    }
  }
  
  @Nested
  public class getAllAppointmentTests {
    @Test
    // no appointments exist in the database
    public void testNoAppointments() {
      List<Appointment> expected = new ArrayList<>();
      
      List<Appointment> resulting = apptDAO.getAllAppointment();
      assertEquals(expected.size(), resulting.size());
    }
    
    @Test
    // one appointment exists in the database
    // using knowledge that id is assigned based on database entry and not updated in local Appointment object
    // (found in previous tests)
    public void testOneAppointment() {
      List<Appointment> expected = new ArrayList<>();
      
      Appointment expectedAppt = new Appointment(3, "Sample Patient", "Female", "22", "4-30-2025", "user@gmail.com", "920-555-6769", "Throat", 5, "100 N Charles St", "Scheduled");
      apptDAO.addAppointment(expectedAppt);
      expected.add(expectedAppt);
  
      List<Appointment> resulting = apptDAO.getAllAppointment();
      assertEquals(expected.size(), resulting.size());
      
      Appointment resultAppt = resulting.get(0);
  
      assertAll(
          () -> assertEquals(1, resultAppt.getId()),
          () -> assertEquals(expectedAppt.getUserId(), resultAppt.getUserId()),
          () -> assertEquals(expectedAppt.getFullName(), resultAppt.getFullName()),
          () -> assertEquals(expectedAppt.getGender(), resultAppt.getGender()),
          () -> assertEquals(expectedAppt.getAge(), resultAppt.getAge()),
          () -> assertEquals(expectedAppt.getAppointmentDate(), resultAppt.getAppointmentDate()),
          () -> assertEquals(expectedAppt.getEmail(), resultAppt.getEmail()),
          () -> assertEquals(expectedAppt.getPhone(), resultAppt.getPhone()),
          () -> assertEquals(expectedAppt.getDiseases(), resultAppt.getDiseases()),
          () -> assertEquals(expectedAppt.getDoctorId(), resultAppt.getDoctorId()),
          () -> assertEquals(expectedAppt.getAddress(), resultAppt.getAddress()),
          () -> assertEquals(expectedAppt.getStatus(), resultAppt.getStatus())
      );
    }
    
    @Test
    // multiple appointments exist in the database
    // using knowledge that id is assigned based on database entry and not updated in local Appointment object
    // (found in previous tests)
    // Not exactly a fault, but found that the list returned is in order of most recently added to least recently added
    // (opposite order compared to previous operations with lists). This should be noted in documentation for clarity.
    public void testMultipleAppointments() {
      List<Appointment> expected = new ArrayList<>();
  
      Appointment a = new Appointment(3, "Sample Patient", "Female", "22", "4-20-2025", "user@gmail.com", "920-555-6769", "Throat", 5, "100 N Charles St", "Past");
      apptDAO.addAppointment(a);
      expected.add(a);

      a = new Appointment(2, "Other Patient", "Male", "78", "5-10-2025", "patient@gmail.com", "555-555-1212", "Kidney", 34, "500 N Charles St", "Confirmed");
      apptDAO.addAppointment(a);
      expected.add(a);
      
      a = new Appointment(56, "Another Patient", "Female", "9", "6-7-2025", "another@gmail.com", "900-555-5555", "Neurological", 99999, "2300 St. Paul St.", "Scheduled");
      apptDAO.addAppointment(a);
      expected.add(a);
  
      List<Appointment> result = apptDAO.getAllAppointment();
      assertEquals(expected.size(), result.size());
  
      for (int i = 0; i < expected.size(); i++) {
        Appointment expectedAppt = expected.get(i);
        // Appointment resultAppt = result.get(i);
        Appointment resultAppt = result.get(expected.size() - 1 - i);
    
        assertAll(
            () -> assertEquals(expectedAppt.getUserId(), resultAppt.getUserId()),
            () -> assertEquals(expectedAppt.getFullName(), resultAppt.getFullName()),
            () -> assertEquals(expectedAppt.getGender(), resultAppt.getGender()),
            () -> assertEquals(expectedAppt.getAge(), resultAppt.getAge()),
            () -> assertEquals(expectedAppt.getAppointmentDate(), resultAppt.getAppointmentDate()),
            () -> assertEquals(expectedAppt.getEmail(), resultAppt.getEmail()),
            () -> assertEquals(expectedAppt.getPhone(), resultAppt.getPhone()),
            () -> assertEquals(expectedAppt.getDiseases(), resultAppt.getDiseases()),
            () -> assertEquals(expectedAppt.getDoctorId(), resultAppt.getDoctorId()),
            () -> assertEquals(expectedAppt.getAddress(), resultAppt.getAddress()),
            () -> assertEquals(expectedAppt.getStatus(), resultAppt.getStatus())
        );
      }
    }
  }
}