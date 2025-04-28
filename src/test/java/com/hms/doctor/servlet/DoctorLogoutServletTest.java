package com.hms.doctor.servlet;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.junit.jupiter.api.Test;

public class DoctorLogoutServletTest {
    
    @Test
    public void testDoGet_Logout() throws ServletException, IOException {
        // Arrange
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);
        
        when(request.getSession()).thenReturn(session);
        
        // Act
        DoctorLogoutServlet servlet = new DoctorLogoutServlet();
        servlet.doGet(request, response);
        
        // Assert
        verify(session).removeAttribute("doctorObj");
        verify(session).setAttribute("successMsg", "Doctor Logout Successfully.");
        verify(response).sendRedirect("doctor_login.jsp");
    }
    
    @Test
    public void testDoGet_ExceptionHandling() throws ServletException, IOException {
        // Arrange
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);
        
        when(request.getSession()).thenReturn(session);
        doThrow(new IOException("Network error")).when(response).sendRedirect(anyString());
        
        // Act & Assert
        DoctorLogoutServlet servlet = new DoctorLogoutServlet();
        assertThrows(IOException.class, () -> {
            servlet.doGet(request, response);
        });
        
        // Verify the session attributes were still set
        verify(session).removeAttribute("doctorObj");
        verify(session).setAttribute("successMsg", "Doctor Logout Successfully.");
    }
    
    @Test
    public void testDoGet_SessionIsNull() throws ServletException, IOException {
        // Arrange
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        
        when(request.getSession()).thenReturn(null);
        
        // Act & Assert
        DoctorLogoutServlet servlet = new DoctorLogoutServlet();
        assertThrows(NullPointerException.class, () -> {
            servlet.doGet(request, response);
        });
    }
}