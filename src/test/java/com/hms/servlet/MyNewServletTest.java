package com.hms.user.servlet;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.junit.jupiter.api.Test;

import java.io.PrintWriter;
import java.io.StringWriter;

public class MyNewServletTest {

    @Test
    public void testDoGet() throws ServletException, IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(request.getContextPath()).thenReturn("myapp");

        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(printWriter);

        MyNewServlet servlet = new MyNewServlet();
        servlet.doGet(request, response);

        printWriter.flush();

        String output = stringWriter.toString();
        assertTrue(output.contains("Served at: myapp"));
    }

    @Test
    public void testDoPost() throws ServletException, IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        when(request.getContextPath()).thenReturn("myapp");

        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(printWriter);

        MyNewServlet servlet = new MyNewServlet();
        servlet.doPost(request, response);

        printWriter.flush();
        String output = stringWriter.toString();
        assertTrue(output.contains("Served at: myapp"));
    }
}