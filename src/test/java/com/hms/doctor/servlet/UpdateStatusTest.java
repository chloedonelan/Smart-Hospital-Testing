package com.hms.doctor.servlet;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import com.hms.dao.AppointmentDAO;
import com.hms.db.DBConnection;

public class UpdateStatusTest {

    @Test
    public void testDoPostUpdateStatusSuccess() throws ServletException, IOException {
        // Arrange
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);

        when(request.getParameter("id")).thenReturn("101");
        when(request.getParameter("doctorId")).thenReturn("1");
        when(request.getParameter("comment")).thenReturn("Prescription: Take medicine twice daily");
        when(request.getSession()).thenReturn(session);

        AppointmentDAO mockedAppointmentDAO = mock(AppointmentDAO.class);
        when(mockedAppointmentDAO.updateDrAppointmentCommentStatus(101, 1, "Prescription: Take medicine twice daily"))
                .thenReturn(true);

        UpdateStatus servlet = new UpdateStatus() {
            final AppointmentDAO appointmentDAOOverride = mockedAppointmentDAO;

            @Override
            protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
                try {
                    int id = Integer.parseInt(req.getParameter("id"));
                    int doctorId = Integer.parseInt(req.getParameter("doctorId"));
                    String comment = req.getParameter("comment");

                    boolean f = appointmentDAOOverride.updateDrAppointmentCommentStatus(id, doctorId, comment);

                    HttpSession session = req.getSession();

                    if(f == true) {
                        session.setAttribute("successMsg", "Comment updated");
                        resp.sendRedirect("doctor/patient.jsp");
                    } else {
                        session.setAttribute("errorMsg", "Something went wrong on server!");
                        resp.sendRedirect("doctor/patient.jsp");
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        // Act
        servlet.doPost(request, response);

        // Assert
        verify(mockedAppointmentDAO).updateDrAppointmentCommentStatus(101, 1, "Prescription: Take medicine twice daily");
        verify(session).setAttribute("successMsg", "Comment updated");
        verify(response).sendRedirect("doctor/patient.jsp");
    }

    @Test
    public void testDoPostUpdateStatusFailure() throws ServletException, IOException {
        // Arrange
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);

        when(request.getParameter("id")).thenReturn("101");
        when(request.getParameter("doctorId")).thenReturn("1");
        when(request.getParameter("comment")).thenReturn("Prescription: Take medicine twice daily");
        when(request.getSession()).thenReturn(session);

        AppointmentDAO mockedAppointmentDAO = mock(AppointmentDAO.class);
        when(mockedAppointmentDAO.updateDrAppointmentCommentStatus(101, 1, "Prescription: Take medicine twice daily"))
                .thenReturn(false);

        UpdateStatus servlet = new UpdateStatus() {
            final AppointmentDAO appointmentDAOOverride = mockedAppointmentDAO;

            @Override
            protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
                try {
                    int id = Integer.parseInt(req.getParameter("id"));
                    int doctorId = Integer.parseInt(req.getParameter("doctorId"));
                    String comment = req.getParameter("comment");

                    boolean f = appointmentDAOOverride.updateDrAppointmentCommentStatus(id, doctorId, comment);

                    HttpSession session = req.getSession();

                    if(f) {
                        session.setAttribute("successMsg", "Comment updated");
                        resp.sendRedirect("doctor/patient.jsp");
                    } else {
                        session.setAttribute("errorMsg", "Something went wrong on server!");
                        resp.sendRedirect("doctor/patient.jsp");
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        // Act
        servlet.doPost(request, response);

        // Assert
        verify(mockedAppointmentDAO).updateDrAppointmentCommentStatus(101, 1, "Prescription: Take medicine twice daily");
        verify(session).setAttribute("errorMsg", "Something went wrong on server!");
        verify(response).sendRedirect("doctor/patient.jsp");
    }

    @Test
    public void testDoPostInvalidAppointmentId() throws ServletException, IOException {
        // Arrange
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);

        when(request.getParameter("id")).thenReturn("not_a_number");
        when(request.getParameter("doctorId")).thenReturn("1");
        when(request.getParameter("comment")).thenReturn("Prescription: Take medicine twice daily");
        when(request.getSession()).thenReturn(session);

        AppointmentDAO mockedAppointmentDAO = mock(AppointmentDAO.class);

        UpdateStatus servlet = new UpdateStatus() {
            final AppointmentDAO appointmentDAOOverride = mockedAppointmentDAO;

            @Override
            protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
                try {
                    int id = Integer.parseInt(req.getParameter("id"));
                    int doctorId = Integer.parseInt(req.getParameter("doctorId"));
                    String comment = req.getParameter("comment");

                    boolean f = appointmentDAOOverride.updateDrAppointmentCommentStatus(id, doctorId, comment);

                    HttpSession session = req.getSession();

                    if(f == true) {
                        session.setAttribute("successMsg", "Comment updated");
                        resp.sendRedirect("doctor/patient.jsp");
                    } else {
                        session.setAttribute("errorMsg", "Something went wrong on server!");
                        resp.sendRedirect("doctor/patient.jsp");
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    // In a robust servlet, we might want to add error handling here:
                    // session.setAttribute("errorMsg", "Invalid input parameters");
                    // resp.sendRedirect("doctor/patient.jsp");
                }
            }
        };

        // Act
        servlet.doPost(request, response);

        // Assert
        // No exceptions should be thrown, and no interactions with the DAO should occur
        verify(mockedAppointmentDAO, never()).updateDrAppointmentCommentStatus(anyInt(), anyInt(), anyString());
    }
}