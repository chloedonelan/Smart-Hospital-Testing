package com.hms.user.servlet;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.junit.jupiter.api.Test;

import com.hms.dao.AppointmentDAO;
import com.hms.entity.Appointment;
import org.mockito.MockedConstruction;

public class AppointmentServletTest {

    @Test
    public void testDoPostSuccess() throws ServletException, IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);

        when(request.getSession()).thenReturn(session);
        when(request.getParameter("userId")).thenReturn("1");
        when(request.getParameter("fullName")).thenReturn("appt servlet success user");
        when(request.getParameter("gender")).thenReturn("Male");
        when(request.getParameter("age")).thenReturn("27");
        when(request.getParameter("appointmentDate")).thenReturn("2025-05-05");
        when(request.getParameter("email")).thenReturn("apptservletsuccessuser@gmail.com");
        when(request.getParameter("phone")).thenReturn("1234567890");
        when(request.getParameter("diseases")).thenReturn("Flu");
        when(request.getParameter("doctorNameSelect")).thenReturn("2");
        when(request.getParameter("address")).thenReturn("123 Main St");

        try (MockedConstruction<AppointmentDAO> daoMocks = mockConstruction(AppointmentDAO.class, (mockDao, ctx) -> {
                when(mockDao.addAppointment(any(Appointment.class))).thenReturn(true);
            })
        ) {
            AppointmentServlet servlet = new AppointmentServlet();
            servlet.doPost(request, response);

            verify(session).setAttribute("successMsg", "Appointment is recorded Successfully.");
            verify(response).sendRedirect("user_appointment.jsp");
        }
    }

    @Test
    public void testDoPostFailure() throws ServletException, IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);

        when(request.getSession()).thenReturn(session);
        when(request.getParameter("userId")).thenReturn("1");
        when(request.getParameter("fullName")).thenReturn("appt servlet failure user");
        when(request.getParameter("gender")).thenReturn("Male");
        when(request.getParameter("age")).thenReturn("27");
        when(request.getParameter("appointmentDate")).thenReturn("2025-05-05");
        when(request.getParameter("email")).thenReturn("apptservletfaileduser@gmail.com");
        when(request.getParameter("phone")).thenReturn("1234567890");
        when(request.getParameter("diseases")).thenReturn("Flu");
        when(request.getParameter("doctorNameSelect")).thenReturn("2");
        when(request.getParameter("address")).thenReturn("123 Main St");

        try (MockedConstruction<AppointmentDAO> daoMocks = mockConstruction(AppointmentDAO.class, (mockDao, ctx) -> {
                    when(mockDao.addAppointment(any(Appointment.class))).thenReturn(false);
                })
        ) {
            AppointmentServlet servlet = new AppointmentServlet();
            servlet.doPost(request, response);

            verify(session).setAttribute("errorMsg", "Something went wrong on server!");
            verify(response).sendRedirect("user_appointment.jsp");
        }
    }
}