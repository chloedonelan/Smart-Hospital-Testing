package com.hms.admin.servlet;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DeleteDoctorServletTest {
  private static Connection conn;
  @Mock
  private HttpServletRequest request;
  @Mock
  private HttpServletResponse response;
  @Mock
  private HttpSession session;
  @InjectMocks
  private DeleteDoctorServlet servlet;
  @InjectMocks
  private DoctorServlet doctorServlet;
  @BeforeAll
  public static void setupDB() throws Exception {
    conn = DriverManager.getConnection(
        "jdbc:mysql://localhost:3306/hospital_db",
        "root", "rootuser"
    );
  }
  
  @BeforeEach
  public void setup() throws SQLException {
    conn.setAutoCommit(false);
    
    Statement stmt = conn.createStatement();
    stmt.execute("SET FOREIGN_KEY_CHECKS=0");
    
    // reset auto-increment and clear data in tables
    stmt.execute("TRUNCATE TABLE appointment");
    stmt.execute("TRUNCATE TABLE user_details");
    stmt.execute("TRUNCATE TABLE specialist");
    stmt.execute("TRUNCATE TABLE doctor");
    
    request = mock(HttpServletRequest.class);
    response = mock(HttpServletResponse.class);
    session = mock(HttpSession.class);
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
  
  // Delete a doctor that does exist
  @Test
  public void testSuccess() {
    when(request.getSession()).thenReturn(session);
    when(request.getParameter("fullName")).thenReturn("John Doe");
    when(request.getParameter("dateOfBirth")).thenReturn("2-22-1969");
    when(request.getParameter("qualification")).thenReturn("MD");
    when(request.getParameter("specialist")).thenReturn("General Surgery");
    when(request.getParameter("email")).thenReturn("john.doe@jh.edu");
    when(request.getParameter("phone")).thenReturn("234-555-6790");
    when(request.getParameter("password")).thenReturn("J0hnD03!");
    when(request.getParameter("id")).thenReturn("1");
  
    try {
      doctorServlet.doPost(request, response); // add the doctor to the database
      
      servlet.doGet(request, response);
  
      verify(session).setAttribute(eq("successMsg"), eq("Doctor Deleted Successfully."));
      verify(response).sendRedirect("admin/view_doctor.jsp");
      verify(session, never()).setAttribute(eq("errorMsg"), eq("Something went wrong on server!"));
    } catch (Exception e) {
      fail();
    }
  }
  
  // Delete a doctor that does not exist
  // Affected by a bug in deleteDoctorById, so it is commented out
  @Test
  public void testFailure() {
    when(request.getSession()).thenReturn(session);
    when(request.getParameter("id")).thenReturn("100");

    try {
      servlet.doGet(request, response);

      verify(session).setAttribute(eq("errorMsg"), eq("Something went wrong on server!"));
      verify(response).sendRedirect("admin/view_doctor.jsp");
      verify(session, never()).setAttribute(eq("successMsg"), eq("Doctor Deleted Successfully."));
    } catch (Exception e) {
      fail();
    }
  }
}
