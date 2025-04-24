package com.hms.admin.servlet;

import com.hms.entity.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AdminLoginServletTest {
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private HttpSession session;
    @InjectMocks
    private AdminLoginServlet servlet;

    @BeforeEach
    public void setup() {
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        session = mock(HttpSession.class);
    }

    // Successful login credentials provided
    @Test
    public void testSuccess() {
        when(request.getSession()).thenReturn(session);
        when(request.getParameter("email")).thenReturn("admin@gmail.com");
        when(request.getParameter("password")).thenReturn("admin");

        try {
            servlet.doPost(request, response);

            verify(session).setAttribute(eq("adminObj"), any(User.class));
            verify(response).sendRedirect("admin/index.jsp");
            verify(session, never()).setAttribute(eq("errorMsg"), eq("Invalid Username or Password."));
            verify(response, never()).sendRedirect("admin_login.jsp");
        } catch (ServletException | IOException e) {
            fail();
        }
    }

    // Unsuccessful login credential provided (incorrect email)
    @Test
    public void testFailureInvalidEmail() {
        when(request.getSession()).thenReturn(session);
        when(request.getParameter("email")).thenReturn("incorrect@gmail.com");
        when(request.getParameter("password")).thenReturn("admin");

        try {
            servlet.doPost(request, response);

            verify(session).setAttribute(eq("errorMsg"), eq("Invalid Username or Password."));
            verify(response).sendRedirect("admin_login.jsp");
            verify(session, never()).setAttribute(eq("adminObj"), any(User.class));
            verify(response, never()).sendRedirect("admin/index.jsp");
        } catch (ServletException | IOException e) {
            fail();
        }
    }

    // Unsuccessful login credential provided (incorrect password)
    @Test
    public void testFailureInvalidPassword() {
        when(request.getSession()).thenReturn(session);
        when(request.getParameter("email")).thenReturn("admin@gmail.com");
        when(request.getParameter("password")).thenReturn("incorrect");

        try {
            servlet.doPost(request, response);

            verify(session).setAttribute(eq("errorMsg"), eq("Invalid Username or Password."));
            verify(response).sendRedirect("admin_login.jsp");
            verify(session, never()).setAttribute(eq("adminObj"), any(User.class));
            verify(response, never()).sendRedirect("admin/index.jsp");
        } catch (ServletException | IOException e) {
            fail();
        }
    }

    // Unsuccessful login credential provided (incorrect email & password)
    @Test
    public void testFailureInvalidEmailAndPassword() {
        when(request.getSession()).thenReturn(session);
        when(request.getParameter("email")).thenReturn("incorrect@gmail.com");
        when(request.getParameter("password")).thenReturn("incorrect");

        try {
            servlet.doPost(request, response);

            verify(session).setAttribute(eq("errorMsg"), eq("Invalid Username or Password."));
            verify(response).sendRedirect("admin_login.jsp");
            verify(session, never()).setAttribute(eq("adminObj"), any(User.class));
            verify(response, never()).sendRedirect("admin/index.jsp");
        } catch (ServletException | IOException e) {
            fail();
        }
    }

    // Unsuccessful login (credentials are empty)
    @Test
    public void testFailureEmptyCredentials() {
        when(request.getSession()).thenReturn(session);
        when(request.getParameter("email")).thenReturn("");
        when(request.getParameter("password")).thenReturn("");

        try {
            servlet.doPost(request, response);

            verify(session).setAttribute(eq("errorMsg"), eq("Invalid Username or Password."));
            verify(response).sendRedirect("admin_login.jsp");
            verify(session, never()).setAttribute(eq("adminObj"), any(User.class));
            verify(response, never()).sendRedirect("admin/index.jsp");
        } catch (ServletException | IOException e) {
            fail();
        }
    }

    // Unsuccessful login (credentials are null)
    @Test
    public void testFailureNullCredentials() {
        when(request.getSession()).thenReturn(session);
        when(request.getParameter("email")).thenReturn(null);
        when(request.getParameter("password")).thenReturn(null);

        try {
            servlet.doPost(request, response);

            verify(session).setAttribute(eq("errorMsg"), eq("Invalid Username or Password."));
            verify(response).sendRedirect("admin_login.jsp");
            verify(session, never()).setAttribute(eq("adminObj"), any(User.class));
            verify(response, never()).sendRedirect("admin/index.jsp");
        } catch (ServletException | IOException e) {
            fail();
        }
    }

    // getParameter() throws exception
    @Test
    public void testExceptionHandling() {
        when(request.getParameter("email")).thenThrow(new RuntimeException("Session error"));

        try {
            servlet.doPost(request, response);

            verify(session, never()).setAttribute(eq("errorMsg"), eq("Invalid Username or Password."));
            verify(response, never()).sendRedirect("admin_login.jsp");
            verify(session, never()).setAttribute(eq("adminObj"), any(User.class));
            verify(response, never()).sendRedirect("admin/index.jsp");
        } catch (ServletException | IOException e) {
            fail();
        }
    }
}