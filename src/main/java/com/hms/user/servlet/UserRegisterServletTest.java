package com.hms.user.servlet;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.junit.jupiter.api.Test;

import com.hms.dao.UserDAO;
import com.hms.entity.User;
import org.mockito.MockedConstruction;

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

        try ( MockedConstruction<UserDAO> daoMock = mockConstruction(
                UserDAO.class,
                (mockDao, ctx) -> {
                    when(mockDao.userRegister(any(User.class))).thenReturn(true);
                })
        ) {
            UserRegisterServlet servlet = new UserRegisterServlet();
            servlet.doPost(request, response);

            verify(session).setAttribute("successMsg", "Register Successfully");
            verify(response).sendRedirect("signup.jsp");
        }
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

        try ( MockedConstruction<UserDAO> daoMock = mockConstruction(
                UserDAO.class,
                (mockDao, ctx) -> {
                    when(mockDao.userRegister(any(User.class))).thenReturn(false);
                })
        ) {
            UserRegisterServlet servlet = new UserRegisterServlet();
            servlet.doPost(request, response);

            verify(session).setAttribute("errorMsg", "Something went wrong!");
            verify(response).sendRedirect("signup.jsp");
        }
    }

    @Test
    void testDoPostHandleException() throws ServletException, IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);

        when(request.getSession()).thenReturn(session);
        when(request.getParameter("fullName")).thenReturn("Name");
        when(request.getParameter("email")).thenReturn("email@example.com");
        when(request.getParameter("password")).thenReturn("pw");

        try (MockedConstruction<UserDAO> mocks = mockConstruction(UserDAO.class, (mockDao, context) -> {
                when(mockDao.userRegister(any())).thenThrow(new RuntimeException("boom"));
            })
        ) {
            UserRegisterServlet servlet = new UserRegisterServlet();

            assertDoesNotThrow(() -> servlet.doPost(request, response));

            verify(response, never()).sendRedirect(anyString());
        }
    }
}