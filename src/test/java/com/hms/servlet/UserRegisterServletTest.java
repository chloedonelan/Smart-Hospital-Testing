package com.hms.user.servlet;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.junit.jupiter.api.Test;

import com.hms.dao.UserDAO;
import com.hms.entity.User;

public class UserRegisterServletTest {

    @Test
    public void testDoPostSuccessfulRegister() throws ServletException, IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);

        when(request.getSession()).thenReturn(session);

        when(request.getParameter("fullName")).thenReturn("Test User");
        when(request.getParameter("email")).thenReturn("testuser@example.com");
        when(request.getParameter("password")).thenReturn("testpass");

        UserDAO mockedUserDAO = mock(UserDAO.class);
        when(mockedUserDAO.userRegister(any(User.class))).thenReturn(true);

        UserRegisterServlet servlet = new UserRegisterServlet() {
            UserDAO userDAOOverride = mockedUserDAO;

            @Override
            protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
                try {
                    String fullName = req.getParameter("fullName");
                    String email = req.getParameter("email");
                    String password = req.getParameter("password");

                    User user = new User(fullName, email, password);

                    boolean f = userDAOOverride.userRegister(user);

                    HttpSession session = req.getSession();

                    if (f) {
                        session.setAttribute("successMsg", "Register Successfully");
                        resp.sendRedirect("signup.jsp");
                    } else {
                        session.setAttribute("errorMsg", "Something went wrong!");
                        resp.sendRedirect("signup.jsp");
                    }
                } catch (Exception e) {
                    throw new ServletException(e);
                }
            }
        };

        servlet.doPost(request, response);

        verify(session).setAttribute("successMsg", "Register Successfully");
        verify(response).sendRedirect("signup.jsp");
    }

    @Test
    public void testDoPostFailedRegister() throws ServletException, IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);
        when(request.getSession()).thenReturn(session);

        when(request.getParameter("fullName")).thenReturn("Test User");
        when(request.getParameter("email")).thenReturn("testuser@example.com");
        when(request.getParameter("password")).thenReturn("testpass");

        UserDAO mockedUserDAO = mock(UserDAO.class);
        when(mockedUserDAO.userRegister(any(User.class))).thenReturn(false);

        UserRegisterServlet servlet = new UserRegisterServlet() {
            UserDAO userDAOOverride = mockedUserDAO;

            @Override
            protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
                try {
                    String fullName = req.getParameter("fullName");
                    String email = req.getParameter("email");
                    String password = req.getParameter("password");

                    User user = new User(fullName, email, password);

                    boolean f = userDAOOverride.userRegister(user);

                    HttpSession session = req.getSession();
                    if (f) {
                        session.setAttribute("successMsg", "Register Successfully");
                        resp.sendRedirect("signup.jsp");
                    } else {
                        session.setAttribute("errorMsg", "Something went wrong!");
                        resp.sendRedirect("signup.jsp");
                    }
                } catch (Exception e) {
                    throw new ServletException(e);
                }
            }
        };

        servlet.doPost(request, response);

        verify(session).setAttribute("errorMsg", "Something went wrong!");
        verify(response).sendRedirect("signup.jsp");
    }
}