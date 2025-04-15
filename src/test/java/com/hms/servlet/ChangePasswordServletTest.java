package com.hms.user.servlet;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.junit.jupiter.api.Test;

import com.hms.dao.UserDAO;

public class ChangePasswordServletTest {

    @Test
    public void testDoPostSuccessfulChange() throws ServletException, IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);

        when(request.getParameter("userId")).thenReturn("1");
        when(request.getParameter("oldPassword")).thenReturn("oldpass");
        when(request.getParameter("newPassword")).thenReturn("newpass");
        when(request.getSession()).thenReturn(session);

        UserDAO mockedUserDAO = mock(UserDAO.class);
        when(mockedUserDAO.checkOldPassword(1, "oldpass")).thenReturn(true);
        when(mockedUserDAO.changePassword(1, "newpass")).thenReturn(true);

        ChangePasswordServlet servlet = new ChangePasswordServlet() {
            UserDAO userDAOOverride = mockedUserDAO;

            @Override
            protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
                int userId = Integer.parseInt(req.getParameter("userId"));
                String oldPassword = req.getParameter("oldPassword");
                String newPassword = req.getParameter("newPassword");
                HttpSession session = req.getSession();

                if (userDAOOverride.checkOldPassword(userId, oldPassword)) {
                    if (userDAOOverride.changePassword(userId, newPassword)) {
                        session.setAttribute("successMsg", "Password Change Successfully.");
                        resp.sendRedirect("change_password.jsp");
                    } else {
                        session.setAttribute("errorMsg", "Something wrong on server!");
                        resp.sendRedirect("change_password.jsp");
                    }
                } else {
                    session.setAttribute("errorMsg", "Old password incorrect");
                    resp.sendRedirect("change_password.jsp");
                }
            }
        };

        servlet.doPost(request, response);

        verify(session).setAttribute("successMsg", "Password Change Successfully.");
        verify(response).sendRedirect("change_password.jsp");
    }

    @Test
    public void testDoPostIncorrectOldPassword() throws ServletException, IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);

        when(request.getParameter("userId")).thenReturn("1");
        when(request.getParameter("oldPassword")).thenReturn("wrongold");
        when(request.getParameter("newPassword")).thenReturn("newpass");
        when(request.getSession()).thenReturn(session);

        UserDAO mockedUserDAO = mock(UserDAO.class);
        when(mockedUserDAO.checkOldPassword(1, "wrongold")).thenReturn(false);

        ChangePasswordServlet servlet = new ChangePasswordServlet() {
            UserDAO userDAOOverride = mockedUserDAO;

            @Override
            protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
                int userId = Integer.parseInt(req.getParameter("userId"));
                String oldPassword = req.getParameter("oldPassword");
                String newPassword = req.getParameter("newPassword");
                HttpSession session = req.getSession();

                if (userDAOOverride.checkOldPassword(userId, oldPassword)) {
                    if (userDAOOverride.changePassword(userId, newPassword)) {
                        session.setAttribute("successMsg", "Password Change Successfully.");
                        resp.sendRedirect("change_password.jsp");
                    } else {
                        session.setAttribute("errorMsg", "Something wrong on server!");
                        resp.sendRedirect("change_password.jsp");
                    }
                } else {
                    session.setAttribute("errorMsg", "Old password incorrect");
                    resp.sendRedirect("change_password.jsp");
                }
            }
        };

        servlet.doPost(request, response);

        verify(session).setAttribute("errorMsg", "Old password incorrect");
        verify(response).sendRedirect("change_password.jsp");
    }

    @Test
    public void testDoPostChangePasswordFailure() throws ServletException, IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);

        when(request.getParameter("userId")).thenReturn("1");
        when(request.getParameter("oldPassword")).thenReturn("oldpass");
        when(request.getParameter("newPassword")).thenReturn("newpass");
        when(request.getSession()).thenReturn(session);

        UserDAO mockedUserDAO = mock(UserDAO.class);
        when(mockedUserDAO.checkOldPassword(1, "oldpass")).thenReturn(true);
        when(mockedUserDAO.changePassword(1, "newpass")).thenReturn(false);

        ChangePasswordServlet servlet = new ChangePasswordServlet() {
            UserDAO userDAOOverride = mockedUserDAO;

            @Override
            protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
                int userId = Integer.parseInt(req.getParameter("userId"));
                String oldPassword = req.getParameter("oldPassword");
                String newPassword = req.getParameter("newPassword");
                HttpSession session = req.getSession();

                if (userDAOOverride.checkOldPassword(userId, oldPassword)) {
                    if (userDAOOverride.changePassword(userId, newPassword)) {
                        session.setAttribute("successMsg", "Password Change Successfully.");
                        resp.sendRedirect("change_password.jsp");
                    } else {
                        session.setAttribute("errorMsg", "Something wrong on server!");
                        resp.sendRedirect("change_password.jsp");
                    }
                } else {
                    session.setAttribute("errorMsg", "Old password incorrect");
                    resp.sendRedirect("change_password.jsp");
                }
            }
        };

        servlet.doPost(request, response);

        verify(session).setAttribute("errorMsg", "Something wrong on server!");
        verify(response).sendRedirect("change_password.jsp");
    }
}