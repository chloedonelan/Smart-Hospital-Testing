package com.hms.doctor.servlet;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.junit.jupiter.api.Test;
import java.util.*;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import com.hms.dao.DoctorDAO;
import com.hms.entity.Doctor;

public class DoctorLoginServletTest {
    private DoctorDAO mockedDoctorDAO;

    @BeforeEach
    public void setup() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);
        when(request.getSession()).thenReturn(session);
        mockedDoctorDAO = mock(DoctorDAO.class);
        DoctorLoginServletTest servlet = new DoctorLoginServletTest() {
            final DoctorDAO doctorDAOOverride = mockedDoctorDAO;
        };
    }


    @Test
    public void testDoPostSuccessfulLogin() throws ServletException, IOException {
        // Arrange
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);

        when(request.getParameter("email")).thenReturn("doctor@example.com");
        when(request.getParameter("password")).thenReturn("password123");
        when(request.getSession()).thenReturn(session);

        Doctor doctor = new Doctor();
        doctor.setId(1);
        doctor.setFullName("Dr. Smith");
        doctor.setEmail("doctor@example.com");

        DoctorDAO mockedDoctorDAO = mock(DoctorDAO.class);
        when(mockedDoctorDAO.loginDoctor("doctor@example.com", "password123")).thenReturn(doctor);

        DoctorLoginServlet servlet = new DoctorLoginServlet() {
            final DoctorDAO doctorDAOOverride = mockedDoctorDAO;

            @Override
            protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
                String email = req.getParameter("email");
                String password = req.getParameter("password");

                HttpSession session = req.getSession();

                Doctor doctor = doctorDAOOverride.loginDoctor(email, password);

                if (doctor != null) {
                    session.setAttribute("doctorObj", doctor);
                    resp.sendRedirect("doctor/index.jsp");
                } else {
                    session.setAttribute("errorMsg", "Invalid email or password");
                    resp.sendRedirect("doctor_login.jsp");
                }
            }
        };

        // Act
        servlet.doPost(request, response);

        // Assert
        verify(session).setAttribute("doctorObj", doctor);
        verify(response).sendRedirect("doctor/index.jsp");
        verify(session, never()).setAttribute(eq("errorMsg"), anyString());
    }

    @Test
    public void testDoPostFailedLogin() throws ServletException, IOException {
        // Arrange
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);

        when(request.getParameter("email")).thenReturn("doctor@example.com");
        when(request.getParameter("password")).thenReturn("wrongpassword");
        when(request.getSession()).thenReturn(session);

        DoctorDAO mockedDoctorDAO = mock(DoctorDAO.class);
        when(mockedDoctorDAO.loginDoctor("doctor@example.com", "wrongpassword")).thenReturn(null);

        DoctorLoginServlet servlet = new DoctorLoginServlet() {
            final DoctorDAO doctorDAOOverride = mockedDoctorDAO;

            @Override
            protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
                String email = req.getParameter("email");
                String password = req.getParameter("password");

                HttpSession session = req.getSession();

                Doctor doctor = doctorDAOOverride.loginDoctor(email, password);

                if (doctor != null) {
                    session.setAttribute("doctorObj", doctor);
                    resp.sendRedirect("doctor/index.jsp");
                } else {
                    session.setAttribute("errorMsg", "Invalid email or password");
                    resp.sendRedirect("doctor_login.jsp");
                }
            }
        };

        // Act
        servlet.doPost(request, response);

        // Assert
        verify(session).setAttribute("errorMsg", "Invalid email or password");
        verify(response).sendRedirect("doctor_login.jsp");
        verify(session, never()).setAttribute(eq("doctorObj"), any());
    }
}