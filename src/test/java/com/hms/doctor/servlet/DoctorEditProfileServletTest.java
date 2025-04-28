package com.hms.doctor.servlet;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedConstruction;

import com.hms.dao.DoctorDAO;
import com.hms.entity.Doctor;

public class DoctorEditProfileServletTest {
    
    @Test
    public void testDoPost_UpdateSuccessful() throws ServletException, IOException {
        // Arrange
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);
        
        int doctorId = 123;
        String fullName = "Dr. John Smith";
        String dateOfBirth = "1980-01-01";
        String qualification = "MD";
        String specialist = "Cardiology";
        String email = "john.smith@example.com";
        String phone = "1234567890";
        
        when(request.getSession()).thenReturn(session);
        when(request.getParameter("doctorId")).thenReturn(String.valueOf(doctorId));
        when(request.getParameter("fullName")).thenReturn(fullName);
        when(request.getParameter("dateOfBirth")).thenReturn(dateOfBirth);
        when(request.getParameter("qualification")).thenReturn(qualification);
        when(request.getParameter("specialist")).thenReturn(specialist);
        when(request.getParameter("email")).thenReturn(email);
        when(request.getParameter("phone")).thenReturn(phone);
        
        Doctor updatedDoctor = new Doctor(doctorId, fullName, dateOfBirth, qualification, specialist, email, phone, "");
        
        try (MockedConstruction<DoctorDAO> daoMocks = mockConstruction(DoctorDAO.class, (mockDao, ctx) -> {
            when(mockDao.editDoctorProfile(any(Doctor.class))).thenReturn(true);
            when(mockDao.getDoctorById(doctorId)).thenReturn(updatedDoctor);
        })) {
            // Act
            DoctorEditProfileServlet servlet = new DoctorEditProfileServlet();
            servlet.doPost(request, response);
            
            // Assert
            ArgumentCaptor<Doctor> doctorCaptor = ArgumentCaptor.forClass(Doctor.class);
            DoctorDAO constructedDao = daoMocks.constructed().get(0);
            verify(constructedDao).editDoctorProfile(doctorCaptor.capture());
            
            Doctor capturedDoctor = doctorCaptor.getValue();
            assertEquals(doctorId, capturedDoctor.getId());
            assertEquals(fullName, capturedDoctor.getFullName());
            assertEquals(dateOfBirth, capturedDoctor.getDateOfBirth());
            assertEquals(qualification, capturedDoctor.getQualification());
            assertEquals(specialist, capturedDoctor.getSpecialist());
            assertEquals(email, capturedDoctor.getEmail());
            assertEquals(phone, capturedDoctor.getPhone());
            
            verify(constructedDao).getDoctorById(doctorId);
            verify(session).setAttribute("successMsgForD", "Doctor update Successfully");
            verify(session).setAttribute("doctorObj", updatedDoctor);
            verify(response).sendRedirect("doctor/edit_profile.jsp");
        }
    }
    
    @Test
    public void testDoPost_UpdateFailed() throws ServletException, IOException {
        // Arrange
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);
        
        when(request.getSession()).thenReturn(session);
        when(request.getParameter("doctorId")).thenReturn("123");
        when(request.getParameter("fullName")).thenReturn("Dr. John Smith");
        when(request.getParameter("dateOfBirth")).thenReturn("1980-01-01");
        when(request.getParameter("qualification")).thenReturn("MD");
        when(request.getParameter("specialist")).thenReturn("Cardiology");
        when(request.getParameter("email")).thenReturn("john.smith@example.com");
        when(request.getParameter("phone")).thenReturn("1234567890");
        
        try (MockedConstruction<DoctorDAO> daoMocks = mockConstruction(DoctorDAO.class, (mockDao, ctx) -> {
            when(mockDao.editDoctorProfile(any(Doctor.class))).thenReturn(false);
        })) {
            // Act
            DoctorEditProfileServlet servlet = new DoctorEditProfileServlet();
            servlet.doPost(request, response);
            
            // Assert
            DoctorDAO constructedDao = daoMocks.constructed().get(0);
            verify(constructedDao, never()).getDoctorById(anyInt());
            verify(session).setAttribute("errorMsgForD", "Something went wrong on server!");
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
        
        // Act
        DoctorEditProfileServlet servlet = new DoctorEditProfileServlet();
        servlet.doPost(request, response);
        
        // Assert - No validations needed as the exception is caught in the servlet
    }
    
    @Test
    public void testDoPost_NullParameters() throws ServletException, IOException {
        // Arrange
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);
        
        when(request.getSession()).thenReturn(session);
        when(request.getParameter("doctorId")).thenReturn("123");
        when(request.getParameter("fullName")).thenReturn(null);
        when(request.getParameter("dateOfBirth")).thenReturn(null);
        when(request.getParameter("qualification")).thenReturn(null);
        when(request.getParameter("specialist")).thenReturn(null);
        when(request.getParameter("email")).thenReturn(null);
        when(request.getParameter("phone")).thenReturn(null);
        
        Doctor updatedDoctor = new Doctor(123, null, null, null, null, null, null, "");
        
        try (MockedConstruction<DoctorDAO> daoMocks = mockConstruction(DoctorDAO.class, (mockDao, ctx) -> {
            when(mockDao.editDoctorProfile(any(Doctor.class))).thenReturn(true);
            when(mockDao.getDoctorById(123)).thenReturn(updatedDoctor);
        })) {
            // Act
            DoctorEditProfileServlet servlet = new DoctorEditProfileServlet();
            servlet.doPost(request, response);
            
            // Assert
            ArgumentCaptor<Doctor> doctorCaptor = ArgumentCaptor.forClass(Doctor.class);
            DoctorDAO constructedDao = daoMocks.constructed().get(0);
            verify(constructedDao).editDoctorProfile(doctorCaptor.capture());
            
            Doctor capturedDoctor = doctorCaptor.getValue();
            assertEquals(123, capturedDoctor.getId());
            assertNull(capturedDoctor.getFullName());
            assertNull(capturedDoctor.getDateOfBirth());
            assertNull(capturedDoctor.getQualification());
            assertNull(capturedDoctor.getSpecialist());
            assertNull(capturedDoctor.getEmail());
            assertNull(capturedDoctor.getPhone());
        }
    }
    
    @Test
    public void testDoPost_DAOThrowsException() throws ServletException, IOException {
        // Arrange
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);
        
        when(request.getSession()).thenReturn(session);
        when(request.getParameter("doctorId")).thenReturn("123");
        when(request.getParameter("fullName")).thenReturn("Dr. John Smith");
        when(request.getParameter("dateOfBirth")).thenReturn("1980-01-01");
        when(request.getParameter("qualification")).thenReturn("MD");
        when(request.getParameter("specialist")).thenReturn("Cardiology");
        when(request.getParameter("email")).thenReturn("john.smith@example.com");
        when(request.getParameter("phone")).thenReturn("1234567890");
        
        try (MockedConstruction<DoctorDAO> daoMocks = mockConstruction(DoctorDAO.class, (mockDao, ctx) -> {
            when(mockDao.editDoctorProfile(any(Doctor.class))).thenThrow(new RuntimeException("Database error"));
        })) {
            // Act
            DoctorEditProfileServlet servlet = new DoctorEditProfileServlet();
            servlet.doPost(request, response);
            
            // Assert - Since the servlet catches exceptions, we verify no further interactions
            verifyNoMoreInteractions(session, response);
        }
    }
}