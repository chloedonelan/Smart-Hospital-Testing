package com.hms.user.servlet;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.junit.jupiter.api.Test;

import com.hms.dao.UserDAO;
import org.mockito.MockedConstruction;

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

        try (MockedConstruction<UserDAO> daoMocks = mockConstruction(UserDAO.class, (mockDao, ctx) -> {
                when(mockDao.checkOldPassword(1, "oldpass")).thenReturn(true);
                when(mockDao.changePassword(1, "newpass")).thenReturn(true);
            })
        ) {
            ChangePasswordServlet servlet = new ChangePasswordServlet();
            servlet.doPost(request, response);

            verify(session).setAttribute("successMsg", "Password Change Successfully.");
            verify(response).sendRedirect("change_password.jsp");
        }
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

        try (MockedConstruction<UserDAO> daoMocks = mockConstruction(UserDAO.class, (mockDao, ctx) -> {
                when(mockDao.checkOldPassword(1, "wrongold")).thenReturn(false);
            })
        ) {
            ChangePasswordServlet servlet = new ChangePasswordServlet();
            servlet.doPost(request, response);

            verify(session).setAttribute("errorMsg", "Old password incorrect");
            verify(response).sendRedirect("change_password.jsp");
        }
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

        try ( MockedConstruction<UserDAO> daoMocks = mockConstruction(UserDAO.class, (mockDao, ctx) -> {
                when(mockDao.checkOldPassword(1, "oldpass")).thenReturn(true);
                when(mockDao.changePassword(1, "newpass")).thenReturn(false);
            })
        ) {
            ChangePasswordServlet servlet = new ChangePasswordServlet();
            servlet.doPost(request, response);

            verify(session).setAttribute("errorMsg", "Something wrong on server!");
            verify(response).sendRedirect("change_password.jsp");
        }
    }
}