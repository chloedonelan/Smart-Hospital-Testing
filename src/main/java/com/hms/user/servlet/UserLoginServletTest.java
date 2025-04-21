package com.hms.user.servlet;

import static org.mockito.Mockito.*;
import org.mockito.MockedConstruction;
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

        UserLoginServlet servlet = new UserLoginServlet();
        User testUser = new User();
        testUser.setId(1);
        testUser.setFullName("Valid User");
        testUser.setEmail("successloginuser@gmail.com");
        testUser.setPassword("validpass");

        UserDAO userDAO = mock(UserDAO.class);
        when(userDAO.loginUser("successloginuser@gmail.com", "validpass")).thenReturn(testUser);

        try ( MockedConstruction<UserDAO> mocks = mockConstruction(UserDAO.class,
                (mockDao, context) -> {
                    when(mockDao.loginUser("successloginuser@gmail.com","validpass")).thenReturn(testUser);
                })
        ) {
            servlet.doPost(request, response);

            verify(session).setAttribute("userObj", testUser);
            verify(response).sendRedirect("index.jsp");
        }
    }

    @Test
    public void testDoPostFailedLogin() throws ServletException, IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);

        when(request.getSession()).thenReturn(session);
        when(request.getParameter("email")).thenReturn("failedloginuser@gmail.com");
        when(request.getParameter("password")).thenReturn("wrongpass");

        UserLoginServlet servlet = new UserLoginServlet();

        try ( MockedConstruction<UserDAO> mocks = mockConstruction(UserDAO.class,
                (mockDao, context) -> {
                    when(mockDao.loginUser("failedloginuser@gmail.com","wrongpass"))
                            .thenReturn(null);
                })
        ) {
            servlet.doPost(request, response);

            verify(session).setAttribute("errorMsg","Invalid email or password");
            verify(response).sendRedirect("user_login.jsp");
        }
    }
}