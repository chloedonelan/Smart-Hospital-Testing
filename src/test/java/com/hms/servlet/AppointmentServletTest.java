package com.hms.user.servlet;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.junit.jupiter.api.Test;

import com.hms.dao.AppointmentDAO;
import com.hms.entity.Appointment;

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

        AppointmentDAO mockedAppointmentDAO = mock(AppointmentDAO.class);
        when(mockedAppointmentDAO.addAppointment(any(Appointment.class))).thenReturn(true);

        AppointmentServlet servlet = new AppointmentServlet() {
            AppointmentDAO appointmentDAOOverride = mockedAppointmentDAO;

            @Override
            protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
                int userId = Integer.parseInt(req.getParameter("userId"));
                String fullName = req.getParameter("fullName");
                String gender = req.getParameter("gender");
                String age = req.getParameter("age");
                String appointmentDate = req.getParameter("appointmentDate");
                String email = req.getParameter("email");
                String phone = req.getParameter("phone");
                String diseases = req.getParameter("diseases");
                int doctorId = Integer.parseInt(req.getParameter("doctorNameSelect"));
                String address = req.getParameter("address");

                Appointment appointment = new Appointment(userId, fullName, gender, age, appointmentDate, email, phone, diseases, doctorId, address, "Pending");

                boolean f = appointmentDAOOverride.addAppointment(appointment);

                HttpSession session = req.getSession();
                if (f) {
                    session.setAttribute("successMsg", "Appointment is recorded Successfully.");
                    resp.sendRedirect("user_appointment.jsp");
                } else {
                    session.setAttribute("errorMsg", "Something went wrong on server!");
                    resp.sendRedirect("user_appointment.jsp");
                }
            }
        };

        servlet.doPost(request, response);

        verify(session).setAttribute("successMsg", "Appointment is recorded Successfully.");
        verify(response).sendRedirect("user_appointment.jsp");
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

        AppointmentDAO mockedAppointmentDAO = mock(AppointmentDAO.class);
        when(mockedAppointmentDAO.addAppointment(any(Appointment.class))).thenReturn(false);

        AppointmentServlet servlet = new AppointmentServlet() {
            AppointmentDAO appointmentDAOOverride = mockedAppointmentDAO;

            @Override
            protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
                int userId = Integer.parseInt(req.getParameter("userId"));
                String fullName = req.getParameter("fullName");
                String gender = req.getParameter("gender");
                String age = req.getParameter("age");
                String appointmentDate = req.getParameter("appointmentDate");
                String email = req.getParameter("email");
                String phone = req.getParameter("phone");
                String diseases = req.getParameter("diseases");
                int doctorId = Integer.parseInt(req.getParameter("doctorNameSelect"));
                String address = req.getParameter("address");

                Appointment appointment = new Appointment(userId, fullName, gender, age, appointmentDate, email, phone, diseases, doctorId, address, "Pending");

                boolean f = appointmentDAOOverride.addAppointment(appointment);

                HttpSession session = req.getSession();
                if (f) {
                    session.setAttribute("successMsg", "Appointment is recorded Successfully.");
                    resp.sendRedirect("user_appointment.jsp");
                } else {
                    session.setAttribute("errorMsg", "Something went wrong on server!");
                    resp.sendRedirect("user_appointment.jsp");
                }
            }
        };

        servlet.doPost(request, response);

        verify(session).setAttribute("errorMsg", "Something went wrong on server!");
        verify(response).sendRedirect("user_appointment.jsp");
    }
}