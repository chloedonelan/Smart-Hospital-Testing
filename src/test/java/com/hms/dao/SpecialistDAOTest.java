package com.hms.dao;

import com.hms.entity.Specialist;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;

import java.nio.file.*;
import java.sql.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class SpecialistDAOTest {

    private static Connection conn;
    private SpecialistDAO specialistDAO;

    @BeforeAll
    public static void setupDB() throws Exception {
        conn = DriverManager.getConnection(
                "jdbc:mysql://127.0.0.1:3306/hospital_db?allowMultiQueries=true",
                "root", "root"
        );

        String sql = new String(Files.readAllBytes(Paths.get("src/test/resources/setup.sql")));
        Statement stmt = conn.createStatement();
        stmt.execute(sql);

        conn = DriverManager.getConnection(
                "jdbc:mysql://127.0.0.1:3306/hospital_db",
                "root", "root"
        );
    }

    @BeforeEach
    public void setup() throws SQLException {
        conn.setAutoCommit(false);
        specialistDAO = new SpecialistDAO(conn);

        Statement stmt = conn.createStatement();
        stmt.execute("SET FOREIGN_KEY_CHECKS=0");
        stmt.execute("TRUNCATE TABLE specialist");
        stmt.execute("SET FOREIGN_KEY_CHECKS=1");
    }

    @AfterEach
    public void rollback() throws SQLException {
        conn.rollback();
    }

    @AfterAll
    public static void cleanup() throws SQLException {
        if (conn != null) conn.close();
    }

    @Test
    public void testAddSpecialist() {
        boolean result = specialistDAO.addSpecialist("Cardiologist");
        assertTrue(result);

        List<Specialist> specialists = specialistDAO.getAllSpecialist();
        assertEquals(1, specialists.size());
        assertEquals("Cardiologist", specialists.get(0).getSpecialistName());
    }

    @Test
    public void testAddSpecialist_NullName() {
        boolean result = specialistDAO.addSpecialist(null);
        assertFalse(result, "Should fail with null specialist name");

        List<Specialist> specialists = specialistDAO.getAllSpecialist();
        assertEquals(0, specialists.size(), "No specialists should be added");
    }

    @Test
    public void testAddSpecialist_EmptyName() {
        boolean result = specialistDAO.addSpecialist("");
        assertFalse(result, "Should fail with empty specialist name");

        List<Specialist> specialists = specialistDAO.getAllSpecialist();
        assertEquals(0, specialists.size(), "No specialists should be added");
    }

    @Test
    public void testAddSpecialist_VeryLongName() {
        // Create a very long string (over 255 chars)
        StringBuilder longString = new StringBuilder();
        for (int i = 0; i < 300; i++) {
            longString.append("a");
        }

        boolean result = specialistDAO.addSpecialist(longString.toString());
        assertFalse(result, "Should fail with very long specialist name");

        List<Specialist> specialists = specialistDAO.getAllSpecialist();
        assertEquals(0, specialists.size(), "No specialists should be added");
    }

    @Test
    public void testAddSpecialist_DuplicateName() throws SQLException {
        PreparedStatement pstmt = conn.prepareStatement("INSERT INTO specialist (specialist_name) VALUES (?)");
        pstmt.setString(1, "Cardiologist");
        pstmt.executeUpdate();

        boolean result = specialistDAO.addSpecialist("Cardiologist");
        assertFalse(result, "Should fail with duplicate specialist name");

        List<Specialist> specialists = specialistDAO.getAllSpecialist();
        assertEquals(1, specialists.size(), "Only one specialist should exist");
    }

    @Test
    public void testAddSpecialist_SQLException() throws SQLException {
        // Test SQL exception handling by using a mock Connection that throws exception
        Connection mockConn = Mockito.mock(Connection.class);
        PreparedStatement mockPstmt = Mockito.mock(PreparedStatement.class);

        Mockito.when(mockConn.prepareStatement(Mockito.anyString())).thenReturn(mockPstmt);
        Mockito.when(mockPstmt.executeUpdate()).thenThrow(new SQLException("Test SQL Exception"));

        SpecialistDAO mockSpecialistDAO = new SpecialistDAO(mockConn);
        boolean result = mockSpecialistDAO.addSpecialist("Cardiologist");

        assertFalse(result, "Should return false when SQL exception occurs");
    }

    @Test
    public void testAddMultipleSpecialists() {
        assertTrue(specialistDAO.addSpecialist("Cardiologist"));
        assertTrue(specialistDAO.addSpecialist("Neurologist"));
        assertTrue(specialistDAO.addSpecialist("Dermatologist"));

        List<Specialist> specialists = specialistDAO.getAllSpecialist();
        assertEquals(3, specialists.size());

        Set<String> names = new HashSet<>();
        for (Specialist s : specialists) {
            names.add(s.getSpecialistName());
        }

        assertTrue(names.contains("Cardiologist"));
        assertTrue(names.contains("Neurologist"));
        assertTrue(names.contains("Dermatologist"));
    }

    @Test
    public void testGetAllSpecialists_Empty() {
        List<Specialist> specialists = specialistDAO.getAllSpecialist();
        assertNotNull(specialists);
        assertEquals(0, specialists.size());
    }

    @Test
    public void testGetAllSpecialists_WithData() throws SQLException {
        PreparedStatement pstmt = conn.prepareStatement("INSERT INTO specialist (specialist_name) VALUES (?)");
        pstmt.setString(1, "Cardiologist");
        pstmt.executeUpdate();
        pstmt.setString(1, "Neurologist");
        pstmt.executeUpdate();

        List<Specialist> specialists = specialistDAO.getAllSpecialist();
        assertNotNull(specialists);
        assertEquals(2, specialists.size());

        Set<String> names = new HashSet<>();
        for (Specialist s : specialists) {
            names.add(s.getSpecialistName());
        }

        assertTrue(names.contains("Cardiologist"));
        assertTrue(names.contains("Neurologist"));
    }

    @Test
    public void testGetAllSpecialists_PropertiesCorrect() throws SQLException {
        PreparedStatement pstmt = conn.prepareStatement("INSERT INTO specialist (id, specialist_name) VALUES (?, ?)");
        pstmt.setInt(1, 100);
        pstmt.setString(2, "TestSpecialist");
        pstmt.executeUpdate();

        List<Specialist> specialists = specialistDAO.getAllSpecialist();
        assertEquals(1, specialists.size());

        Specialist specialist = specialists.get(0);
        assertEquals(100, specialist.getId(), "ID should match");
        assertEquals("TestSpecialist", specialist.getSpecialistName(), "Name should match");
    }

    @Test
    public void testGetAllSpecialists_SQLException() throws SQLException {
        // Test SQL exception handling by using a mock Connection that throws exception
        Connection mockConn = Mockito.mock(Connection.class);
        PreparedStatement mockPstmt = Mockito.mock(PreparedStatement.class);

        Mockito.when(mockConn.prepareStatement(Mockito.anyString())).thenReturn(mockPstmt);
        Mockito.when(mockPstmt.executeQuery()).thenThrow(new SQLException("Test SQL Exception"));

        SpecialistDAO mockSpecialistDAO = new SpecialistDAO(mockConn);
        List<Specialist> specialists = mockSpecialistDAO.getAllSpecialist();

        assertNotNull(specialists, "Should return an empty list, not null");
        assertTrue(specialists.isEmpty(), "List should be empty when SQL exception occurs");
    }
}