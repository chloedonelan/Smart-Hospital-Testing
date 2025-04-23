package com.hms.user.servlet;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.junit.jupiter.api.Test;

public class UserLogoutServletTest {

    @Test
    public void testDoGet() throws ServletException, IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);

        when(request.getSession()).thenReturn(session);

        UserLogoutServlet servlet = new UserLogoutServlet();

        servlet.doGet(request, response);

        verify(session).removeAttribute("userObj");
        verify(session).setAttribute("successMsg", "User Logout Successfully.");
        verify(response).sendRedirect("user_login.jsp");
    }
}