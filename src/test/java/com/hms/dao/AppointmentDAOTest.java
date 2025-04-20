package com.hms.dao;

import com.hms.entity.Appointment;
import com.hms.entity.Doctor;
import com.hms.entity.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;

import org.junit.jupiter.api.*;

import java.nio.file.*;
import java.sql.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;

public class AppointmentDAOTest {
  
  private static Connection conn;
  private AppointmentDAO apptDAO;
  
  @BeforeAll
  public static void setupDB() throws Exception {
    conn = DriverManager.getConnection(
        "jdbc:mysql://localhost:3306/?allowMultiQueries=true",
        "root", "rootuser"
    );
    
    // Now switch connection to hospital_db
    conn = DriverManager.getConnection(
        "jdbc:mysql://localhost:3306/hospital_db",
        "root", "rootuser"
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
    // TODO
  }
  
  @Nested
  public class getAppointmentByIdTests {
    // TODO
  }
  
  @Nested
  public class updateDrAppointmentCommentStatusTests {
    // TODO
  }
  
  @Nested
  public class getAllAppointmentTests {
    // TODO
  }
}
