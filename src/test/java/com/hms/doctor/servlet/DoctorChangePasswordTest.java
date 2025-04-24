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

public class DoctorChangePasswordTest {
    private HttpServletRequest request;
    private HttpServletResponse response;
    private HttpSession session;
    private DoctorDAO mockedDoctorDAO;
    private DoctorChangePasswordTest servlet;

    @BeforeEach
    public void setup() throws Exception {
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        session = mock(HttpSession.class);
        when(request.getSession()).thenReturn(session);
        mockedDoctorDAO = mock(DoctorDAO.class);
        servlet = new DoctorChangePasswordTest() {
            DoctorDAO doctorDAOOverride = mockedDoctorDAO;
        };
    }


    @Test
    public void testDoPostPasswordChangeSuccess() throws ServletException, IOException {
        // Arrange
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);

        when(request.getParameter("doctorId")).thenReturn("1");
        when(request.getParameter("oldPassword")).thenReturn("oldPassword123");
        when(request.getParameter("newPassword")).thenReturn("newPassword123");
        when(request.getSession()).thenReturn(session);

        DoctorDAO mockedDoctorDAO = mock(DoctorDAO.class);
        when(mockedDoctorDAO.checkOldPassword(1, "oldPassword123")).thenReturn(true);
        when(mockedDoctorDAO.changePassword(1, "newPassword123")).thenReturn(true);

        DoctorChangePassword servlet = new DoctorChangePassword() {
            DoctorDAO doctorDAOOverride = mockedDoctorDAO;

            @Override
            protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
                int doctorId = Integer.parseInt(req.getParameter("doctorId"));
                String newPassword = req.getParameter("newPassword");
                String oldPassword = req.getParameter("oldPassword");

                HttpSession session = req.getSession();

                if (doctorDAOOverride.checkOldPassword(doctorId, oldPassword)) {
                    if (doctorDAOOverride.changePassword(doctorId, newPassword)) {
                        session.setAttribute("successMsg", "Password change successfully.");
                        resp.sendRedirect("doctor/edit_profile.jsp");
                    } else {
                        session.setAttribute("errorMsg", "Something went wrong on server!");
                        resp.sendRedirect("doctor/edit_profile.jsp");
                    }
                } else {
                    session.setAttribute("errorMsg", "Old Password not match");
                    resp.sendRedirect("doctor/edit_profile.jsp");
                }
            }
        };

        // Act
        servlet.doPost(request, response);

        // Assert
        verify(mockedDoctorDAO).checkOldPassword(1, "oldPassword123");
        verify(mockedDoctorDAO).changePassword(1, "newPassword123");
        verify(session).setAttribute("successMsg", "Password change successfully.");
        verify(response).sendRedirect("doctor/edit_profile.jsp");
    }

    @Test
    public void testDoPostOldPasswordIncorrect() throws ServletException, IOException {
        // Arrange
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);

        when(request.getParameter("doctorId")).thenReturn("1");
        when(request.getParameter("oldPassword")).thenReturn("wrongPassword");
        when(request.getParameter("newPassword")).thenReturn("newPassword123");
        when(request.getSession()).thenReturn(session);

        DoctorDAO mockedDoctorDAO = mock(DoctorDAO.class);
        when(mockedDoctorDAO.checkOldPassword(1, "wrongPassword")).thenReturn(false);

        DoctorChangePassword servlet = new DoctorChangePassword() {
            DoctorDAO doctorDAOOverride = mockedDoctorDAO;

            @Override
            protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
                int doctorId = Integer.parseInt(req.getParameter("doctorId"));
                String newPassword = req.getParameter("newPassword");
                String oldPassword = req.getParameter("oldPassword");

                HttpSession session = req.getSession();

                if (doctorDAOOverride.checkOldPassword(doctorId, oldPassword)) {
                    if (doctorDAOOverride.changePassword(doctorId, newPassword)) {
                        session.setAttribute("successMsg", "Password change successfully.");
                        resp.sendRedirect("doctor/edit_profile.jsp");
                    } else {
                        session.setAttribute("errorMsg", "Something went wrong on server!");
                        resp.sendRedirect("doctor/edit_profile.jsp");
                    }
                } else {
                    session.setAttribute("errorMsg", "Old Password not match");
                    resp.sendRedirect("doctor/edit_profile.jsp");
                }
            }
        };

        // Act
        servlet.doPost(request, response);

        // Assert
        verify(mockedDoctorDAO).checkOldPassword(1, "wrongPassword");
        verify(mockedDoctorDAO, never()).changePassword(anyInt(), anyString());
        verify(session).setAttribute("errorMsg", "Old Password not match");
        verify(response).sendRedirect("doctor/edit_profile.jsp");
    }

    @Test
    public void testDoPostPasswordChangeFailure() throws ServletException, IOException {
        // Arrange
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);

        when(request.getParameter("doctorId")).thenReturn("1");
        when(request.getParameter("oldPassword")).thenReturn("oldPassword123");
        when(request.getParameter("newPassword")).thenReturn("newPassword123");
        when(request.getSession()).thenReturn(session);

        DoctorDAO mockedDoctorDAO = mock(DoctorDAO.class);
        when(mockedDoctorDAO.checkOldPassword(1, "oldPassword123")).thenReturn(true);
        when(mockedDoctorDAO.changePassword(1, "newPassword123")).thenReturn(false);

        DoctorChangePassword servlet = new DoctorChangePassword() {
            DoctorDAO doctorDAOOverride = mockedDoctorDAO;

            @Override
            protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
                int doctorId = Integer.parseInt(req.getParameter("doctorId"));
                String newPassword = req.getParameter("newPassword");
                String oldPassword = req.getParameter("oldPassword");

                HttpSession session = req.getSession();

                if (doctorDAOOverride.checkOldPassword(doctorId, oldPassword)) {
                    if (doctorDAOOverride.changePassword(doctorId, newPassword)) {
                        session.setAttribute("successMsg", "Password change successfully.");
                        resp.sendRedirect("doctor/edit_profile.jsp");
                    } else {
                        session.setAttribute("errorMsg", "Something went wrong on server!");
                        resp.sendRedirect("doctor/edit_profile.jsp");
                    }
                } else {
                    session.setAttribute("errorMsg", "Old Password not match");
                    resp.sendRedirect("doctor/edit_profile.jsp");
                }
            }
        };

        // Act
        servlet.doPost(request, response);

        // Assert
        verify(mockedDoctorDAO).checkOldPassword(1, "oldPassword123");
        verify(mockedDoctorDAO).changePassword(1, "newPassword123");
        verify(session).setAttribute("errorMsg", "Something went wrong on server!");
        verify(response).sendRedirect("doctor/edit_profile.jsp");
    }
}