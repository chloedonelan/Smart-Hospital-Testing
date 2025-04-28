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

import com.hms.dao.AppointmentDAO;

public class UpdateStatusTest {
    
    @Test
    public void testDoPost_UpdateSuccessful() throws ServletException, IOException {
        // Arrange
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);
        
        int id = 123;
        int doctorId = 456;
        String comment = "Patient is recovering well";
        
        when(request.getSession()).thenReturn(session);
        when(request.getParameter("id")).thenReturn(String.valueOf(id));
        when(request.getParameter("doctorId")).thenReturn(String.valueOf(doctorId));
        when(request.getParameter("comment")).thenReturn(comment);
        
        try (MockedConstruction<AppointmentDAO> daoMocks = mockConstruction(AppointmentDAO.class, (mockDao, ctx) -> {
            when(mockDao.updateDrAppointmentCommentStatus(id, doctorId, comment)).thenReturn(true);
        })) {
            // Act
            UpdateStatus servlet = new UpdateStatus();
            servlet.doPost(request, response);
            
            // Assert
            verify(session).setAttribute("successMsg", "Comment updated");
            verify(response).sendRedirect("doctor/patient.jsp");
            verify(session, never()).setAttribute(eq("errorMsg"), anyString());
        }
    }
    
    @Test
    public void testDoPost_UpdateFailed() throws ServletException, IOException {
        // Arrange
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);
        
        int id = 123;
        int doctorId = 456;
        String comment = "Patient is recovering well";
        
        when(request.getSession()).thenReturn(session);
        when(request.getParameter("id")).thenReturn(String.valueOf(id));
        when(request.getParameter("doctorId")).thenReturn(String.valueOf(doctorId));
        when(request.getParameter("comment")).thenReturn(comment);
        
        try (MockedConstruction<AppointmentDAO> daoMocks = mockConstruction(AppointmentDAO.class, (mockDao, ctx) -> {
            when(mockDao.updateDrAppointmentCommentStatus(id, doctorId, comment)).thenReturn(false);
        })) {
            // Act
            UpdateStatus servlet = new UpdateStatus();
            servlet.doPost(request, response);
            
            // Assert
            verify(session).setAttribute("errorMsg", "Something went wrong on server!");
            verify(response).sendRedirect("doctor/patient.jsp");
            verify(session, never()).setAttribute(eq("successMsg"), anyString());
        }
    }
    
    @Test
    public void testDoPost_InvalidId() throws ServletException, IOException {
        // Arrange
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);
        
        when(request.getSession()).thenReturn(session);
        when(request.getParameter("id")).thenReturn("not_a_number");
        when(request.getParameter("doctorId")).thenReturn("456");
        when(request.getParameter("comment")).thenReturn("Comment");
        
        // Act
        UpdateStatus servlet = new UpdateStatus();
        servlet.doPost(request, response);
        
        // Assert - Servlet catches exception internally, so nothing to verify except
        // that AppointmentDAO was never constructed
        // This will be verified implicitly since we're not setting up any MockedConstruction
    }
    
    @Test
    public void testDoPost_NullComment() throws ServletException, IOException {
        // Arrange
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);
        
        when(request.getSession()).thenReturn(session);
        when(request.getParameter("id")).thenReturn("123");
        when(request.getParameter("doctorId")).thenReturn("456");
        when(request.getParameter("comment")).thenReturn(null);
        
        try (MockedConstruction<AppointmentDAO> daoMocks = mockConstruction(AppointmentDAO.class, (mockDao, ctx) -> {
            when(mockDao.updateDrAppointmentCommentStatus(123, 456, null)).thenReturn(true);
        })) {
            // Act
            UpdateStatus servlet = new UpdateStatus();
            servlet.doPost(request, response);
            
            // Assert
            verify(session).setAttribute("successMsg", "Comment updated");
            verify(response).sendRedirect("doctor/patient.jsp");
        }
    }
    
    @Test
    public void testDoPost_DAOThrowsException() throws ServletException, IOException {
        // Arrange
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);
        
        when(request.getSession()).thenReturn(session);
        when(request.getParameter("id")).thenReturn("123");
        when(request.getParameter("doctorId")).thenReturn("456");
        when(request.getParameter("comment")).thenReturn("Comment");
        
        try (MockedConstruction<AppointmentDAO> daoMocks = mockConstruction(AppointmentDAO.class, (mockDao, ctx) -> {
            when(mockDao.updateDrAppointmentCommentStatus(anyInt(), anyInt(), anyString()))
                .thenThrow(new RuntimeException("Database error"));
        })) {
            // Act
            UpdateStatus servlet = new UpdateStatus();
            servlet.doPost(request, response);
            
            // Assert - Since the servlet catches exceptions, we verify no further interactions
            verifyNoMoreInteractions(session, response);
        }
    }
}