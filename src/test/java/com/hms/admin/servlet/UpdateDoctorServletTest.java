package com.hms.admin.servlet;

import com.hms.dao.DoctorDAO;
import com.hms.entity.Doctor;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UpdateDoctorServletTest {
  private static Connection conn;
  @Mock
  private HttpServletRequest request;
  @Mock
  private HttpServletResponse response;
  @Mock
  private HttpSession session;
  @InjectMocks
  private UpdateDoctorServlet servlet;
  @InjectMocks
  private DoctorServlet addServlet;
  
  @BeforeAll
  public static void setupDB() throws Exception {
    conn = DriverManager.getConnection(
        "jdbc:mysql://localhost:3306/hospital_db",
        "root", "root"
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
  
  // Successful edit to doctor in the database
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
  
    try {
      addServlet.doPost(request, response);
    } catch (ServletException | IOException e) {
      fail();
    }
  
    when(request.getSession()).thenReturn(session);
    when(request.getParameter("fullName")).thenReturn("Jane Doe");
    when(request.getParameter("dateOfBirth")).thenReturn("12-3-1966");
    when(request.getParameter("qualification")).thenReturn("MD/PhD");
    when(request.getParameter("specialist")).thenReturn("Neurosurgery");
    when(request.getParameter("email")).thenReturn("jane.doe@jh.edu");
    when(request.getParameter("phone")).thenReturn("234-555-6792");
    when(request.getParameter("password")).thenReturn("J4n3D03!");
    when(request.getParameter("id")).thenReturn("1");
    
    try {
      servlet.doPost(request, response);
      
      verify(session).setAttribute(eq("successMsg"), eq("Doctor update Successfully"));
      verify(response).sendRedirect("admin/view_doctor.jsp");
      verify(session, never()).setAttribute(eq("errorMsg"), eq("Something went wrong on server!"));
    } catch (ServletException | IOException e) {
      fail();
    }
  }
  
  // Unsuccessful edit to doctor in the database (null values)
  @Test
  public void testFailureNullValues() {
    when(request.getSession()).thenReturn(session);
    when(request.getParameter("fullName")).thenReturn("John Doe");
    when(request.getParameter("dateOfBirth")).thenReturn("2-22-1969");
    when(request.getParameter("qualification")).thenReturn("MD");
    when(request.getParameter("specialist")).thenReturn("General Surgery");
    when(request.getParameter("email")).thenReturn("john.doe@jh.edu");
    when(request.getParameter("phone")).thenReturn("234-555-6790");
    when(request.getParameter("password")).thenReturn("J0hnD03!");
  
    try {
      addServlet.doPost(request, response);
    } catch (ServletException | IOException e) {
      fail();
    }
  
    when(request.getSession()).thenReturn(session);
    when(request.getParameter("fullName")).thenReturn(null);
    when(request.getParameter("dateOfBirth")).thenReturn(null);
    when(request.getParameter("qualification")).thenReturn(null);
    when(request.getParameter("specialist")).thenReturn(null);
    when(request.getParameter("email")).thenReturn(null);
    when(request.getParameter("phone")).thenReturn(null);
    when(request.getParameter("password")).thenReturn(null);
    when(request.getParameter("id")).thenReturn("1");
  
    try {
      servlet.doPost(request, response);
  
      verify(session).setAttribute(eq("errorMsg"), eq("Something went wrong on server!"));
      verify(response).sendRedirect("admin/view_doctor.jsp");
      verify(session, never()).setAttribute(eq("successMsg"), eq("Doctor update Successfully"));
    } catch (ServletException | IOException e) {
      fail();
    }
  }
  
  // Unsuccessful edit to doctor in the database (does not exist)
  @Test
  public void testFailureDoctorDoesNotExist() {
    when(request.getSession()).thenReturn(session);
    when(request.getParameter("fullName")).thenReturn("Jane Doe");
    when(request.getParameter("dateOfBirth")).thenReturn("12-3-1966");
    when(request.getParameter("qualification")).thenReturn("MD/PhD");
    when(request.getParameter("specialist")).thenReturn("Neurosurgery");
    when(request.getParameter("email")).thenReturn("jane.doe@jh.edu");
    when(request.getParameter("phone")).thenReturn("234-555-6792");
    when(request.getParameter("password")).thenReturn("J4n3D03!");
    when(request.getParameter("id")).thenReturn("156");
    
    try (MockedConstruction<DoctorDAO> daoMocks = mockConstruction(DoctorDAO.class, (mockDao, ctx) -> {
      when(mockDao.updateDoctor(any(Doctor.class))).thenReturn(false);
    })){
      servlet.doPost(request, response);

      verify(session).setAttribute(eq("errorMsg"), eq("Something went wrong on server!"));
      verify(response).sendRedirect("admin/view_doctor.jsp");
      verify(session, never()).setAttribute(eq("successMsg"), eq("Doctor update Successfully"));
    } catch (ServletException | IOException e) {
      fail();
    }
  }
  
  // Unsuccessful edit to doctor in the database (empty values)
  @Test
  public void testFailureEmptyValues() {
    when(request.getSession()).thenReturn(session);
    when(request.getParameter("fullName")).thenReturn("John Doe");
    when(request.getParameter("dateOfBirth")).thenReturn("2-22-1969");
    when(request.getParameter("qualification")).thenReturn("MD");
    when(request.getParameter("specialist")).thenReturn("General Surgery");
    when(request.getParameter("email")).thenReturn("john.doe@jh.edu");
    when(request.getParameter("phone")).thenReturn("234-555-6790");
    when(request.getParameter("password")).thenReturn("J0hnD03!");

    try {
      addServlet.doPost(request, response);
    } catch (ServletException | IOException e) {
      fail();
    }

    when(request.getSession()).thenReturn(session);
    when(request.getParameter("fullName")).thenReturn("");
    when(request.getParameter("dateOfBirth")).thenReturn("");
    when(request.getParameter("qualification")).thenReturn("");
    when(request.getParameter("specialist")).thenReturn("");
    when(request.getParameter("email")).thenReturn("");
    when(request.getParameter("phone")).thenReturn("");
    when(request.getParameter("password")).thenReturn("");
    when(request.getParameter("id")).thenReturn("1");
  
    try (MockedConstruction<DoctorDAO> daoMocks = mockConstruction(DoctorDAO.class, (mockDao, ctx) -> {
      when(mockDao.updateDoctor(any(Doctor.class))).thenReturn(false);
    })){
      servlet.doPost(request, response);

      verify(session).setAttribute(eq("errorMsg"), eq("Something went wrong on server!"));
      verify(response).sendRedirect("admin/view_doctor.jsp");
      verify(session, never()).setAttribute(eq("successMsg"), eq("Doctor update Successfully"));
    } catch (ServletException | IOException e) {
      fail();
    }
  }
  
  // getParameter() throws exception
  @Test
  public void testExceptionHandling() {
    when(request.getParameter("fullName")).thenThrow(new RuntimeException("Session error"));
    
    try {
      servlet.doPost(request, response);
  
      verify(session, never()).setAttribute(eq("errorMsg"), eq("Something went wrong on server!"));
      verify(response, never()).sendRedirect("admin/view_doctor.jsp");
      verify(session, never()).setAttribute(eq("successMsg"), eq("Doctor update Successfully"));
    } catch (ServletException | IOException e) {
      fail();
    }
  }
}