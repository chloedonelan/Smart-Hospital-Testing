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

public class DoctorChangePasswordTest {
    
    @Test
    public void testDoPost_ChangeSuccessful() throws ServletException, IOException {
        // Arrange
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);
        
        int doctorId = 123;
        String oldPassword = "oldpass123";
        String newPassword = "newpass456";
        
        when(request.getSession()).thenReturn(session);
        when(request.getParameter("doctorId")).thenReturn(String.valueOf(doctorId));
        when(request.getParameter("oldPassword")).thenReturn(oldPassword);
        when(request.getParameter("newPassword")).thenReturn(newPassword);
        
        try (MockedConstruction<DoctorDAO> daoMocks = mockConstruction(DoctorDAO.class, (mockDao, ctx) -> {
            when(mockDao.checkOldPassword(doctorId, oldPassword)).thenReturn(true);
            when(mockDao.changePassword(doctorId, newPassword)).thenReturn(true);
        })) {
            // Act
            DoctorChangePassword servlet = new DoctorChangePassword();
            servlet.doPost(request, response);
            
            // Assert
            DoctorDAO constructedDao = daoMocks.constructed().get(0);
            verify(constructedDao).checkOldPassword(doctorId, oldPassword);
            verify(constructedDao).changePassword(doctorId, newPassword);
            verify(session).setAttribute("successMsg", "Password change successfully.");
            verify(response).sendRedirect("doctor/edit_profile.jsp");
        }
    }
    
    @Test
    public void testDoPost_OldPasswordIncorrect() throws ServletException, IOException {
        // Arrange
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);
        
        int doctorId = 123;
        String oldPassword = "wrongpass";
        String newPassword = "newpass456";
        
        when(request.getSession()).thenReturn(session);
        when(request.getParameter("doctorId")).thenReturn(String.valueOf(doctorId));
        when(request.getParameter("oldPassword")).thenReturn(oldPassword);
        when(request.getParameter("newPassword")).thenReturn(newPassword);
        
        try (MockedConstruction<DoctorDAO> daoMocks = mockConstruction(DoctorDAO.class, (mockDao, ctx) -> {
            when(mockDao.checkOldPassword(doctorId, oldPassword)).thenReturn(false);
        })) {
            // Act
            DoctorChangePassword servlet = new DoctorChangePassword();
            servlet.doPost(request, response);
            
            // Assert
            DoctorDAO constructedDao = daoMocks.constructed().get(0);
            verify(constructedDao).checkOldPassword(doctorId, oldPassword);
            verify(constructedDao, never()).changePassword(anyInt(), anyString());
            verify(session).setAttribute("errorMsg", "Old Password not match");
            verify(response).sendRedirect("doctor/edit_profile.jsp");
        }
    }
    
    @Test
    public void testDoPost_ChangePasswordFailed() throws ServletException, IOException {
        // Arrange
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);
        
        int doctorId = 123;
        String oldPassword = "oldpass123";
        String newPassword = "newpass456";
        
        when(request.getSession()).thenReturn(session);
        when(request.getParameter("doctorId")).thenReturn(String.valueOf(doctorId));
        when(request.getParameter("oldPassword")).thenReturn(oldPassword);
        when(request.getParameter("newPassword")).thenReturn(newPassword);
        
        try (MockedConstruction<DoctorDAO> daoMocks = mockConstruction(DoctorDAO.class, (mockDao, ctx) -> {
            when(mockDao.checkOldPassword(doctorId, oldPassword)).thenReturn(true);
            when(mockDao.changePassword(doctorId, newPassword)).thenReturn(false);
        })) {
            // Act
            DoctorChangePassword servlet = new DoctorChangePassword();
            servlet.doPost(request, response);
            
            // Assert
            DoctorDAO constructedDao = daoMocks.constructed().get(0);
            verify(constructedDao).checkOldPassword(doctorId, oldPassword);
            verify(constructedDao).changePassword(doctorId, newPassword);
            verify(session).setAttribute("errorMsg", "Something went wrong on server!");
            verify(response).sendRedirect("doctor/edit_profile.jsp");
        }
    }
    
    @Test
    public void testDoPost_InvalidDoctorId() throws ServletException, IOException {
        // Arrange
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);
        
        when(request.getSession()).thenReturn(session);
        when(request.getParameter("doctorId")).thenReturn("not_a_number");
        when(request.getParameter("oldPassword")).thenReturn("oldpass");
        when(request.getParameter("newPassword")).thenReturn("newpass");
        
        // Act & Assert
        DoctorChangePassword servlet = new DoctorChangePassword();
        assertThrows(NumberFormatException.class, () -> {
            servlet.doPost(request, response);
        });
    }
    
    @Test
    public void testDoPost_NullParameters() throws ServletException, IOException {
        // Arrange
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);
        
        when(request.getSession()).thenReturn(session);
        when(request.getParameter("doctorId")).thenReturn("123");
        when(request.getParameter("oldPassword")).thenReturn(null);
        when(request.getParameter("newPassword")).thenReturn(null);
        
        try (MockedConstruction<DoctorDAO> daoMocks = mockConstruction(DoctorDAO.class, (mockDao, ctx) -> {
            when(mockDao.checkOldPassword(123, null)).thenReturn(false);
        })) {
            // Act
            DoctorChangePassword servlet = new DoctorChangePassword();
            servlet.doPost(request, response);
            
            // Assert
            DoctorDAO constructedDao = daoMocks.constructed().get(0);
            verify(constructedDao).checkOldPassword(123, null);
            verify(session).setAttribute("errorMsg", "Old Password not match");
        }
    }
    
    @Test
    public void testDoPost_DAOCheckThrowsException() throws ServletException, IOException {
        // Arrange
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);
        
        when(request.getSession()).thenReturn(session);
        when(request.getParameter("doctorId")).thenReturn("123");
        when(request.getParameter("oldPassword")).thenReturn("oldpass");
        when(request.getParameter("newPassword")).thenReturn("newpass");
        
        try (MockedConstruction<DoctorDAO> daoMocks = mockConstruction(DoctorDAO.class, (mockDao, ctx) -> {
            when(mockDao.checkOldPassword(anyInt(), anyString()))
                .thenThrow(new RuntimeException("Database error"));
        })) {
            // Act & Assert
            DoctorChangePassword servlet = new DoctorChangePassword();
            assertThrows(RuntimeException.class, () -> {
                servlet.doPost(request, response);
            });
        }
    }
}