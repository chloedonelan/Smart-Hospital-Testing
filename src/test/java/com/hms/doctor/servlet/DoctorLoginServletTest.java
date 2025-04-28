package com.hms.doctor.servlet;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;

import com.hms.dao.DoctorDAO;
import com.hms.entity.Doctor;

public class DoctorLoginServletTest {
    
    @Test
    public void testDoPost_ValidCredentials() throws ServletException, IOException {
        // Arrange
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);
        
        String email = "doctor@example.com";
        String password = "password123";
        
        Doctor mockDoctor = new Doctor();
        mockDoctor.setId(1);
        mockDoctor.setFullName("Dr. Smith");
        mockDoctor.setEmail(email);
        
        when(request.getSession()).thenReturn(session);
        when(request.getParameter("email")).thenReturn(email);
        when(request.getParameter("password")).thenReturn(password);
        
        try (MockedConstruction<DoctorDAO> daoMocks = mockConstruction(DoctorDAO.class, (mockDao, ctx) -> {
            when(mockDao.loginDoctor(email, password)).thenReturn(mockDoctor);
        })) {
            // Act
            DoctorLoginServlet servlet = new DoctorLoginServlet();
            servlet.doPost(request, response);
            
            // Assert
            verify(session).setAttribute("doctorObj", mockDoctor);
            verify(response).sendRedirect("doctor/index.jsp");
            verify(session, never()).setAttribute(eq("errorMsg"), anyString());
        }
    }
    
    @Test
    public void testDoPost_InvalidCredentials() throws ServletException, IOException {
        // Arrange
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);
        
        String email = "invalid@example.com";
        String password = "wrongpassword";
        
        when(request.getSession()).thenReturn(session);
        when(request.getParameter("email")).thenReturn(email);
        when(request.getParameter("password")).thenReturn(password);
        
        try (MockedConstruction<DoctorDAO> daoMocks = mockConstruction(DoctorDAO.class, (mockDao, ctx) -> {
            when(mockDao.loginDoctor(email, password)).thenReturn(null);
        })) {
            // Act
            DoctorLoginServlet servlet = new DoctorLoginServlet();
            servlet.doPost(request, response);
            
            // Assert
            verify(session).setAttribute("errorMsg", "Invalid email or password");
            verify(response).sendRedirect("doctor_login.jsp");
            verify(session, never()).setAttribute(eq("doctorObj"), any());
        }
    }
    
    @Test
    public void testDoPost_NullEmail() throws ServletException, IOException {
        // Arrange
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);
        
        when(request.getSession()).thenReturn(session);
        when(request.getParameter("email")).thenReturn(null);
        when(request.getParameter("password")).thenReturn("password123");
        
        try (MockedConstruction<DoctorDAO> daoMocks = mockConstruction(DoctorDAO.class, (mockDao, ctx) -> {
            when(mockDao.loginDoctor(null, "password123")).thenReturn(null);
        })) {
            // Act
            DoctorLoginServlet servlet = new DoctorLoginServlet();
            servlet.doPost(request, response);
            
            // Assert
            verify(session).setAttribute("errorMsg", "Invalid email or password");
            verify(response).sendRedirect("doctor_login.jsp");
        }
    }
    
    @Test
    public void testDoPost_NullPassword() throws ServletException, IOException {
        // Arrange
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);
        
        when(request.getSession()).thenReturn(session);
        when(request.getParameter("email")).thenReturn("doctor@example.com");
        when(request.getParameter("password")).thenReturn(null);
        
        try (MockedConstruction<DoctorDAO> daoMocks = mockConstruction(DoctorDAO.class, (mockDao, ctx) -> {
            when(mockDao.loginDoctor("doctor@example.com", null)).thenReturn(null);
        })) {
            // Act
            DoctorLoginServlet servlet = new DoctorLoginServlet();
            servlet.doPost(request, response);
            
            // Assert
            verify(session).setAttribute("errorMsg", "Invalid email or password");
            verify(response).sendRedirect("doctor_login.jsp");
        }
    }
    
    @Test
    public void testDoPost_ExceptionHandling() throws ServletException, IOException {
        // Arrange
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);
        
        when(request.getSession()).thenReturn(session);
        when(request.getParameter("email")).thenReturn("doctor@example.com");
        when(request.getParameter("password")).thenReturn("password123");
        
        try (MockedConstruction<DoctorDAO> daoMocks = mockConstruction(DoctorDAO.class, (mockDao, ctx) -> {
            when(mockDao.loginDoctor(anyString(), anyString())).thenThrow(new RuntimeException("Database error"));
        })) {
            // Act & Assert
            DoctorLoginServlet servlet = new DoctorLoginServlet();
            assertThrows(RuntimeException.class, () -> {
                servlet.doPost(request, response);
            });
        }
    }
}