package com.hms.doctor.servlet;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import com.hms.dao.DoctorDAO;
import com.hms.entity.Doctor;

public class DoctorEditProfileServletTest {

    @Test
    public void testDoPostProfileUpdateSuccess() throws ServletException, IOException {
        // Arrange
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);

        when(request.getParameter("doctorId")).thenReturn("1");
        when(request.getParameter("fullName")).thenReturn("Dr. Smith");
        when(request.getParameter("dateOfBirth")).thenReturn("1980-01-01");
        when(request.getParameter("qualification")).thenReturn("MD");
        when(request.getParameter("specialist")).thenReturn("Cardiologist");
        when(request.getParameter("email")).thenReturn("doctor@example.com");
        when(request.getParameter("phone")).thenReturn("1234567890");
        when(request.getSession()).thenReturn(session);

        Doctor updatedDoctor = new Doctor();
        updatedDoctor.setId(1);
        updatedDoctor.setFullName("Dr. Smith");
        updatedDoctor.setEmail("doctor@example.com");

        DoctorDAO mockedDoctorDAO = mock(DoctorDAO.class);
        when(mockedDoctorDAO.editDoctorProfile(any(Doctor.class))).thenReturn(true);
        when(mockedDoctorDAO.getDoctorById(1)).thenReturn(updatedDoctor);

        DoctorEditProfileServlet servlet = new DoctorEditProfileServlet() {
            DoctorDAO doctorDAOOverride = mockedDoctorDAO;

            @Override
            protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
                try {
                    // get all data which is coming from doctor.jsp doctor details
                    String fullName = req.getParameter("fullName");
                    String dateOfBirth = req.getParameter("dateOfBirth");
                    String qualification = req.getParameter("qualification");
                    String specialist = req.getParameter("specialist");
                    String email = req.getParameter("email");
                    String phone = req.getParameter("phone");

                    int id = Integer.parseInt(req.getParameter("doctorId"));

                    Doctor doctor = new Doctor(id, fullName, dateOfBirth, qualification, specialist, email, phone, "");

                    boolean f = doctorDAOOverride.editDoctorProfile(doctor);

                    HttpSession session = req.getSession();

                    if (f == true) {
                        Doctor updateDoctorObj = doctorDAOOverride.getDoctorById(id);
                        session.setAttribute("successMsgForD", "Doctor update Successfully");
                        session.setAttribute("doctorObj", updateDoctorObj); // over ride or update old session value to new updated doctor value.
                        resp.sendRedirect("doctor/edit_profile.jsp");

                    } else {
                        session.setAttribute("errorMsgForD", "Something went wrong on server!");
                        resp.sendRedirect("doctor/edit_profile.jsp");
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        // Act
        servlet.doPost(request, response);

        // Assert
        // Capture the Doctor object passed to editDoctorProfile
        ArgumentCaptor<Doctor> doctorCaptor = ArgumentCaptor.forClass(Doctor.class);
        verify(mockedDoctorDAO).editDoctorProfile(doctorCaptor.capture());

        // Verify the Doctor object has the correct values
        Doctor capturedDoctor = doctorCaptor.getValue();
        assertEquals(1, capturedDoctor.getId());
        assertEquals("Dr. Smith", capturedDoctor.getFullName());
        assertEquals("1980-01-01", capturedDoctor.getDateOfBirth());
        assertEquals("MD", capturedDoctor.getQualification());
        assertEquals("Cardiologist", capturedDoctor.getSpecialist());
        assertEquals("doctor@example.com", capturedDoctor.getEmail());
        assertEquals("1234567890", capturedDoctor.getPhone());

        verify(mockedDoctorDAO).getDoctorById(1);
        verify(session).setAttribute("successMsgForD", "Doctor update Successfully");
        verify(session).setAttribute("doctorObj", updatedDoctor);
        verify(response).sendRedirect("doctor/edit_profile.jsp");
    }

    @Test
    public void testDoPostProfileUpdateFailure() throws ServletException, IOException {
        // Arrange
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);

        when(request.getParameter("doctorId")).thenReturn("1");
        when(request.getParameter("fullName")).thenReturn("Dr. Smith");
        when(request.getParameter("dateOfBirth")).thenReturn("1980-01-01");
        when(request.getParameter("qualification")).thenReturn("MD");
        when(request.getParameter("specialist")).thenReturn("Cardiologist");
        when(request.getParameter("email")).thenReturn("doctor@example.com");
        when(request.getParameter("phone")).thenReturn("1234567890");
        when(request.getSession()).thenReturn(session);

        DoctorDAO mockedDoctorDAO = mock(DoctorDAO.class);
        when(mockedDoctorDAO.editDoctorProfile(any(Doctor.class))).thenReturn(false);

        DoctorEditProfileServlet servlet = new DoctorEditProfileServlet() {
            DoctorDAO doctorDAOOverride = mockedDoctorDAO;

            @Override
            protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
                try {
                    // get all data which is coming from doctor.jsp doctor details
                    String fullName = req.getParameter("fullName");
                    String dateOfBirth = req.getParameter("dateOfBirth");
                    String qualification = req.getParameter("qualification");
                    String specialist = req.getParameter("specialist");
                    String email = req.getParameter("email");
                    String phone = req.getParameter("phone");

                    int id = Integer.parseInt(req.getParameter("doctorId"));

                    Doctor doctor = new Doctor(id, fullName, dateOfBirth, qualification, specialist, email, phone, "");

                    boolean f = doctorDAOOverride.editDoctorProfile(doctor);

                    HttpSession session = req.getSession();

                    if (f == true) {
                        Doctor updateDoctorObj = doctorDAOOverride.getDoctorById(id);
                        session.setAttribute("successMsgForD", "Doctor update Successfully");
                        session.setAttribute("doctorObj", updateDoctorObj); // over ride or update old session value to new updated doctor value.
                        resp.sendRedirect("doctor/edit_profile.jsp");

                    } else {
                        session.setAttribute("errorMsgForD", "Something went wrong on server!");
                        resp.sendRedirect("doctor/edit_profile.jsp");
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        // Act
        servlet.doPost(request, response);

        // Assert
        verify(mockedDoctorDAO).editDoctorProfile(any(Doctor.class));
        verify(mockedDoctorDAO, never()).getDoctorById(anyInt());
        verify(session).setAttribute("errorMsgForD", "Something went wrong on server!");
        verify(session, never()).setAttribute(eq("doctorObj"), any());
        verify(response).sendRedirect("doctor/edit_profile.jsp");
    }

    @Test
    public void testDoPostExceptionHandling() throws ServletException, IOException {
        // Arrange
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);

        when(request.getParameter("doctorId")).thenReturn("not_a_number"); // This will cause NumberFormatException
        when(request.getParameter("fullName")).thenReturn("Dr. Smith");
        when(request.getParameter("dateOfBirth")).thenReturn("1980-01-01");
        when(request.getParameter("qualification")).thenReturn("MD");
        when(request.getParameter("specialist")).thenReturn("Cardiologist");
        when(request.getParameter("email")).thenReturn("doctor@example.com");
        when(request.getParameter("phone")).thenReturn("1234567890");
        when(request.getSession()).thenReturn(session);

        DoctorDAO mockedDoctorDAO = mock(DoctorDAO.class);

        DoctorEditProfileServlet servlet = new DoctorEditProfileServlet() {
            DoctorDAO doctorDAOOverride = mockedDoctorDAO;

            @Override
            protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
                try {
                    // get all data which is coming from doctor.jsp doctor details
                    String fullName = req.getParameter("fullName");
                    String dateOfBirth = req.getParameter("dateOfBirth");
                    String qualification = req.getParameter("qualification");
                    String specialist = req.getParameter("specialist");
                    String email = req.getParameter("email");
                    String phone = req.getParameter("phone");

                    int id = Integer.parseInt(req.getParameter("doctorId"));

                    Doctor doctor = new Doctor(id, fullName, dateOfBirth, qualification, specialist, email, phone, "");

                    boolean f = doctorDAOOverride.editDoctorProfile(doctor);

                    HttpSession session = req.getSession();

                    if (f == true) {
                        Doctor updateDoctorObj = doctorDAOOverride.getDoctorById(id);
                        session.setAttribute("successMsgForD", "Doctor update Successfully");
                        session.setAttribute("doctorObj", updateDoctorObj); // over ride or update old session value to new updated doctor value.
                        resp.sendRedirect("doctor/edit_profile.jsp");

                    } else {
                        session.setAttribute("errorMsgForD", "Something went wrong on server!");
                        resp.sendRedirect("doctor/edit_profile.jsp");
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        // Act
        servlet.doPost(request, response);

        // Assert
        // No exceptions should be thrown, and no interactions with the DAO should occur
        verify(mockedDoctorDAO, never()).editDoctorProfile(any(Doctor.class));
        verify(mockedDoctorDAO, never()).getDoctorById(anyInt());
    }

    private void assertEquals(int expected, int actual) {
        if (expected != actual) {
            throw new AssertionError("Expected: " + expected + ", but was: " + actual);
        }
    }

    private void assertEquals(String expected, String actual) {
        if (!expected.equals(actual)) {
            throw new AssertionError("Expected: " + expected + ", but was: " + actual);
        }
    }
}