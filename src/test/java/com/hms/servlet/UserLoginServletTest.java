package com.hms.user.servlet;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.junit.jupiter.api.Test;

import com.hms.dao.UserDAO;
import com.hms.entity.User;


// add tests for null or empty params?

public class UserLoginServletTest {
    @Test
    public void testDoPostSuccessfulLogin() throws ServletException, IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);

        when(request.getSession()).thenReturn(session);
        when(request.getParameter("email")).thenReturn("successloginuser@gmail.com");
        when(request.getParameter("password")).thenReturn("validpass");

        User testUser = new User();
        testUser.setId(1);
        testUser.setFullName("Valid User");
        testUser.setEmail("successloginuser@gmail.com");
        testUser.setPassword("validpass");

        UserDAO userDAO = mock(UserDAO.class);
        when(userDAO.loginUser("successloginuser@gmail.com", "validpass")).thenReturn(testUser);

        UserLoginServlet servlet = new UserLoginServlet() {
            @Override
            protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
                String email = req.getParameter("email");
                String password = req.getParameter("password");
                HttpSession session = req.getSession();

                User user = userDAO.loginUser(email, password);

                if (user != null) {
                    session.setAttribute("userObj", user);
                    resp.sendRedirect("index.jsp");
                } else {
                    session.setAttribute("errorMsg", "Invalid email or password");
                    resp.sendRedirect("user_login.jsp");
                }
            }
        };

        servlet.doPost(request, response);

        verify(session).setAttribute("userObj", testUser);
        verify(response).sendRedirect("index.jsp");
    }

    @Test
    public void testDoPostFailedLogin() throws ServletException, IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);

        when(request.getSession()).thenReturn(session);
        when(request.getParameter("email")).thenReturn("failedloginuser@gmail.com");
        when(request.getParameter("password")).thenReturn("wrongpass");

        UserDAO userDAO = mock(UserDAO.class);
        when(userDAO.loginUser("failedloginuser@gmail.com", "wrongpass")).thenReturn(null);

        UserLoginServlet servlet = new UserLoginServlet() {
            @Override
            protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
                String email = req.getParameter("email");
                String password = req.getParameter("password");
                HttpSession session = req.getSession();

                User user = userDAO.loginUser(email, password);

                if (user != null) {
                    session.setAttribute("userObj", user);
                    resp.sendRedirect("index.jsp");
                } else {
                    session.setAttribute("errorMsg", "Invalid email or password");
                    resp.sendRedirect("user_login.jsp");
                }
            }
        };

        servlet.doPost(request, response);

        verify(session).setAttribute("errorMsg", "Invalid email or password");
        verify(response).sendRedirect("user_login.jsp");
    }
}
