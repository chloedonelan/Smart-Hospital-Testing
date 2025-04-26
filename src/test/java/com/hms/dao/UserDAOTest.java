package com.hms.dao;

import com.hms.entity.User;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;

import static org.junit.jupiter.api.Assertions.*;

public class UserDAOTest {

    private static Connection conn;
    private UserDAO userDAO;

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
        userDAO = new UserDAO(conn);

        Statement stmt = conn.createStatement();
        stmt.execute("SET FOREIGN_KEY_CHECKS=0");
        stmt.execute("TRUNCATE TABLE appointment");
        stmt.execute("TRUNCATE TABLE user_details");

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

    private User createTestUser(String name, String email, String password) {
        User user = new User();
        user.setFullName(name);
        user.setEmail(email);
        user.setPassword(password);
        return user;
    }

    @Test
    public void testUserRegister() {
        User user = createTestUser("Test User", "testuser@example.com", "password123");
        boolean result = userDAO.userRegister(user);
        assertTrue(result);

        User loggedInUser = userDAO.loginUser("testuser@example.com", "password123");
        assertNotNull(loggedInUser);
        assertEquals("Test User", loggedInUser.getFullName());
        assertEquals("testuser@example.com", loggedInUser.getEmail());
    }

    @Test
    public void testUserRegister_NullValues() {
        // Test with null name
        User user1 = createTestUser(null, "email@example.com", "password");
        boolean result1 = userDAO.userRegister(user1);
        assertFalse(result1, "Should fail with null name");

        // Test with null email
        User user2 = createTestUser("Test User", null, "password");
        boolean result2 = userDAO.userRegister(user2);
        assertFalse(result2, "Should fail with null email");

        // Test with null password
        User user3 = createTestUser("Test User", "email@example.com", null);
        boolean result3 = userDAO.userRegister(user3);
        assertFalse(result3, "Should fail with null password");
    }

    @Test
    public void testUserRegister_EmptyValues() {
        // Test with empty name
        User user1 = createTestUser("", "email@example.com", "password");
        boolean result1 = userDAO.userRegister(user1);
        assertFalse(result1, "Should fail with empty name");

        // Test with empty email
        User user2 = createTestUser("Test User", "", "password");
        boolean result2 = userDAO.userRegister(user2);
        assertFalse(result2, "Should fail with empty email");

        // Test with empty password
        User user3 = createTestUser("Test User", "email@example.com", "");
        boolean result3 = userDAO.userRegister(user3);
        assertFalse(result3, "Should fail with empty password");
    }

    @Test
    public void testUserRegister_DuplicateEmail() throws SQLException {
        PreparedStatement pstmt = conn.prepareStatement("INSERT INTO user_details (full_name, email, password) VALUES (?, ?, ?)");
        pstmt.setString(1, "Existing User");
        pstmt.setString(2, "duplicate@example.com");
        pstmt.setString(3, "pass123");
        pstmt.executeUpdate();

        User user = createTestUser("New User", "duplicate@example.com", "newpass");
        boolean result = userDAO.userRegister(user);
        assertFalse(result, "Should fail with duplicate email");
    }

    @Test
    public void testUserRegister_VeryLongValues() {
        // Create a very long string (over 255 chars)
        StringBuilder longString = new StringBuilder();
        for (int i = 0; i < 300; i++) {
            longString.append("a");
        }

        // Test with very long name
        User user1 = createTestUser(longString.toString(), "email@example.com", "password");
        boolean result1 = userDAO.userRegister(user1);
        assertFalse(result1, "Should fail with very long name");

        // Test with very long email
        User user2 = createTestUser("Test User", longString.toString() + "@example.com", "password");
        boolean result2 = userDAO.userRegister(user2);
        assertFalse(result2, "Should fail with very long email");

        // Test with very long password
        User user3 = createTestUser("Test User", "email@example.com", longString.toString());
        boolean result3 = userDAO.userRegister(user3);
        assertFalse(result3, "Should fail with very long password");
    }

    @Test
    public void testLoginUser_ValidCredentials() throws SQLException {
        PreparedStatement pstmt = conn.prepareStatement("INSERT INTO user_details (full_name, email, password) VALUES (?, ?, ?)");
        pstmt.setString(1, "Login Test");
        pstmt.setString(2, "login@example.com");
        pstmt.setString(3, "loginpass");
        pstmt.executeUpdate();

        User loggedInUser = userDAO.loginUser("login@example.com", "loginpass");
        assertNotNull(loggedInUser);
        assertEquals("Login Test", loggedInUser.getFullName());
        assertEquals("login@example.com", loggedInUser.getEmail());
        assertEquals("loginpass", loggedInUser.getPassword()); // Testing that password is correctly fetched
    }

    @Test
    public void testLoginUser_CaseSensitivity() throws SQLException {
        PreparedStatement pstmt = conn.prepareStatement("INSERT INTO user_details (full_name, email, password) VALUES (?, ?, ?)");
        pstmt.setString(1, "Case Test");
        pstmt.setString(2, "case@example.com");
        pstmt.setString(3, "CaseSensitive");
        pstmt.executeUpdate();

        // Test with correct case
        User user1 = userDAO.loginUser("case@example.com", "CaseSensitive");
        assertNotNull(user1);

        // Test with incorrect case for email
        User user2 = userDAO.loginUser("CASE@example.com", "CaseSensitive");
        assertNull(user2, "Email should be case sensitive");

        // Test with incorrect case for password
        User user3 = userDAO.loginUser("case@example.com", "casesensitive");
        assertNull(user3, "Password should be case sensitive");
    }

    @Test
    public void testLoginUser_InvalidEmail() {
        User loggedInUser = userDAO.loginUser("nonexistent@example.com", "anypassword");
        assertNull(loggedInUser);
    }

    @Test
    public void testLoginUser_InvalidPassword() throws SQLException {
        PreparedStatement pstmt = conn.prepareStatement("INSERT INTO user_details (full_name, email, password) VALUES (?, ?, ?)");
        pstmt.setString(1, "Password Test");
        pstmt.setString(2, "password@example.com");
        pstmt.setString(3, "correctpass");
        pstmt.executeUpdate();

        User loggedInUser = userDAO.loginUser("password@example.com", "wrongpass");
        assertNull(loggedInUser);
    }

    @Test
    public void testLoginUser_NullCredentials() {
        // Test with null email
        User user1 = userDAO.loginUser(null, "password");
        assertNull(user1, "Should return null with null email");

        // Test with null password
        User user2 = userDAO.loginUser("email@example.com", null);
        assertNull(user2, "Should return null with null password");

        // Test with both null
        User user3 = userDAO.loginUser(null, null);
        assertNull(user3, "Should return null with null email and password");
    }

    @Test
    public void testLoginUser_EmptyCredentials() {
        // Test with empty email
        User user1 = userDAO.loginUser("", "password");
        assertNull(user1, "Should return null with empty email");

        // Test with empty password
        User user2 = userDAO.loginUser("email@example.com", "");
        assertNull(user2, "Should return null with empty password");

        // Test with both empty
        User user3 = userDAO.loginUser("", "");
        assertNull(user3, "Should return null with empty email and password");
    }

    @Test
    public void testCheckOldPassword_Correct() throws SQLException {
        PreparedStatement pstmt = conn.prepareStatement("INSERT INTO user_details (full_name, email, password) VALUES (?, ?, ?)");
        pstmt.setString(1, "Password Check");
        pstmt.setString(2, "pwcheck@example.com");
        pstmt.setString(3, "oldpass");
        pstmt.executeUpdate();

        pstmt = conn.prepareStatement("SELECT id FROM user_details WHERE email = ?");
        pstmt.setString(1, "pwcheck@example.com");
        ResultSet rs = pstmt.executeQuery();
        assertTrue(rs.next());
        int userId = rs.getInt("id");

        boolean result = userDAO.checkOldPassword(userId, "oldpass");
        assertTrue(result);
    }

    @Test
    public void testCheckOldPassword_Incorrect() throws SQLException {
        PreparedStatement pstmt = conn.prepareStatement("INSERT INTO user_details (full_name, email, password) VALUES (?, ?, ?)");
        pstmt.setString(1, "Password Check");
        pstmt.setString(2, "pwcheck@example.com");
        pstmt.setString(3, "oldpass");
        pstmt.executeUpdate();

        pstmt = conn.prepareStatement("SELECT id FROM user_details WHERE email = ?");
        pstmt.setString(1, "pwcheck@example.com");
        ResultSet rs = pstmt.executeQuery();
        assertTrue(rs.next());
        int userId = rs.getInt("id");

        boolean result = userDAO.checkOldPassword(userId, "wrongpass");
        assertFalse(result);
    }

    @Test
    public void testCheckOldPassword_NonexistentUser() {
        // Test with a user ID that doesn't exist
        boolean result = userDAO.checkOldPassword(999, "anypass");
        assertFalse(result, "Should return false for non-existent user");
    }

    @Test
    public void testCheckOldPassword_NullPassword() throws SQLException {
        PreparedStatement pstmt = conn.prepareStatement("INSERT INTO user_details (full_name, email, password) VALUES (?, ?, ?)");
        pstmt.setString(1, "Null Password");
        pstmt.setString(2, "null@example.com");
        pstmt.setString(3, "password");
        pstmt.executeUpdate();

        pstmt = conn.prepareStatement("SELECT id FROM user_details WHERE email = ?");
        pstmt.setString(1, "null@example.com");
        ResultSet rs = pstmt.executeQuery();
        assertTrue(rs.next());
        int userId = rs.getInt("id");

        boolean result = userDAO.checkOldPassword(userId, null);
        assertFalse(result, "Should return false for null password");
    }

    @Test
    public void testChangePassword() throws SQLException {
        PreparedStatement pstmt = conn.prepareStatement("INSERT INTO user_details (full_name, email, password) VALUES (?, ?, ?)");
        pstmt.setString(1, "Change PW");
        pstmt.setString(2, "changepw@example.com");
        pstmt.setString(3, "oldpass");
        pstmt.executeUpdate();

        pstmt = conn.prepareStatement("SELECT id FROM user_details WHERE email = ?");
        pstmt.setString(1, "changepw@example.com");
        ResultSet rs = pstmt.executeQuery();
        assertTrue(rs.next());
        int userId = rs.getInt("id");

        boolean result = userDAO.changePassword(userId, "newpass");
        assertTrue(result);

        assertTrue(userDAO.checkOldPassword(userId, "newpass"));
        assertFalse(userDAO.checkOldPassword(userId, "oldpass"));
    }

    @Test
    public void testChangePassword_NonexistentUser() {
        boolean result = userDAO.changePassword(999, "anypass");
        assertTrue(result, "Should return true even for non-existent user");

        // Verify user was not created
        User user = userDAO.loginUser("nonexistent@example.com", "anypass");
        assertNull(user, "User should not be created from changePassword");
    }

    @Test
    public void testChangePassword_NullPassword() throws SQLException {
        PreparedStatement pstmt = conn.prepareStatement("INSERT INTO user_details (full_name, email, password) VALUES (?, ?, ?)");
        pstmt.setString(1, "Null Change");
        pstmt.setString(2, "nullchange@example.com");
        pstmt.setString(3, "oldpass");
        pstmt.executeUpdate();

        pstmt = conn.prepareStatement("SELECT id FROM user_details WHERE email = ?");
        pstmt.setString(1, "nullchange@example.com");
        ResultSet rs = pstmt.executeQuery();
        assertTrue(rs.next());
        int userId = rs.getInt("id");

        boolean result = userDAO.changePassword(userId, null);
        assertFalse(result, "Should fail with null password");
    }

    @Test
    public void testChangePassword_SQLException() throws SQLException {
        // Test SQL exception handling by using a mock Connection that throws exception
        Connection mockConn = Mockito.mock(Connection.class);
        PreparedStatement mockPstmt = Mockito.mock(PreparedStatement.class);

        Mockito.when(mockConn.prepareStatement(Mockito.anyString())).thenReturn(mockPstmt);
        Mockito.when(mockPstmt.executeUpdate()).thenThrow(new SQLException("Test SQL Exception"));

        UserDAO mockUserDAO = new UserDAO(mockConn);
        boolean result = mockUserDAO.changePassword(1, "newpass");

        assertFalse(result, "Should return false when SQL exception occurs");
    }
}