package com.hms.web;

import com.hms.dao.DoctorDAO;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.*;

import java.nio.file.*;
import java.sql.*;
import java.time.Duration;
import java.util.*;

public class DoctorPagesSeleniumTest {
    private WebDriver driver;
    private static Connection conn;

    static int apptId;

    @BeforeAll
    static void setupDB() throws Exception {
        conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/?allowMultiQueries=true",
                "root", "rootuser"
        );

        String sql = new String(Files.readAllBytes(
                Paths.get("src/test/resources/setup.sql")
        ));

        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        }

        conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/hospital_db",
                "root", "rootuser"
        );

        String docInsert = "INSERT INTO doctor\n" +
                        "  (fullName,dateOfBirth,qualification,specialist,email,phone,password)\n" +
                        "VALUES (?,?,?,?,?,?,?)\n" +
                        "ON DUPLICATE KEY UPDATE\n" +
                        "  fullName = VALUES(fullName),\n" +
                        "  password = VALUES(password)";

        try (PreparedStatement ps = conn.prepareStatement(docInsert)) {
            ps.setString(1, "Selenium Test Doctor"); // name
            ps.setString(2, "1980-01-01"); // birth
            ps.setString(3, "selenium"); // qual
            ps.setString(4, "web test"); // spec
            ps.setString(5, "doc1@gmail.com"); // email
            ps.setString(6, "1234567899"); // phone
            ps.setString(7, "pass"); // password
            ps.executeUpdate();
        }

        int userId;
        String userInsert =
                "INSERT INTO user_details(full_name, email, password) VALUES (?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(
                userInsert, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, "selenium test user");
            ps.setString(2, "user1@gmail.com");
            ps.setString(3, "upass");
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                rs.next();
                userId = rs.getInt(1);
            }
        }

        String apptInsert =
                "INSERT INTO appointment "
                        + "(userId, fullName, gender, age, appointmentDate, email, phone, diseases, doctorId, address, status) "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, "
                        + "(SELECT id FROM doctor WHERE email = ?), ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(
                apptInsert, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, userId);
            ps.setString(2, "selenium test patient");
            ps.setString(3, "F");
            ps.setString(4, "28");
            ps.setString(5, "2025-04-19");
            ps.setString(6, "patient@test.com");
            ps.setString(7, "555-4321");
            ps.setString(8, "cough");
            ps.setString(9, "doc1@gmail.com");
            ps.setString(10, "address street");
            ps.setString(11, "Pending");
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                rs.next();
                apptId = rs.getInt(1);
            }
        }
    }

    @BeforeEach
    void setup() {
        driver = new ChromeDriver();
    }

    @AfterEach
    void teardown() {
        driver.quit();
    }

    @AfterAll
    public static void cleanup() throws SQLException {
        if (conn != null) conn.close();
    }

    @Test
    void dashboardHasAccurateCounts() {
        driver.get("http://localhost:8080/Doctor_Patient_Portal_war/doctor_login.jsp");
        driver.findElement(By.name("email")).sendKeys("doc1@gmail.com");
        driver.findElement(By.name("password")).sendKeys("pass");
        driver.findElement(By.tagName("form")).submit();

        driver.get("http://localhost:8080/Doctor_Patient_Portal_war/doctor/index.jsp");
        List<WebElement> stats = driver.findElements(By.cssSelector("div.card .fs-4"));
        assertEquals("Doctor\n1", stats.get(0).getText());
        assertEquals("Total Appointment\n1", stats.get(1).getText());
    }

    @Test
    void canLeaveCommentWhenLoggedIn() {
        driver.get("http://localhost:8080/Doctor_Patient_Portal_war/doctor_login.jsp");
        driver.findElement(By.name("email")).sendKeys("doc1@gmail.com");
        driver.findElement(By.name("password")).sendKeys("pass");
        driver.findElement(By.tagName("form")).submit();

        // dashboard -> comments
        driver.get("http://localhost:8080/Doctor_Patient_Portal_war/doctor/comment.jsp?id=" + apptId);
        WebElement textarea = driver.findElement(By.name("comment"));
        assertTrue(textarea.isDisplayed(), "comment box is present");
    }

    @Test
    void editProfileShowsAllFields() {
        driver.get("http://localhost:8080/Doctor_Patient_Portal_war/doctor_login.jsp");
        driver.findElement(By.name("email")).sendKeys("doc1@gmail.com");
        driver.findElement(By.name("password")).sendKeys("pass");
        driver.findElement(By.tagName("form")).submit();

        // edit profile
        driver.get("http://localhost:8080/Doctor_Patient_Portal_war/doctor/edit_profile.jsp");
        assertEquals("Edit Doctor | Doctor", driver.getTitle());
        assertNotNull(driver.findElement(By.name("fullName")));
        assertNotNull(driver.findElement(By.name("qualification")));
    }

    @Test
    void navbarLogout() {
        driver.get("http://localhost:8080/Doctor_Patient_Portal_war/doctor_login.jsp");
        driver.findElement(By.name("email")).sendKeys("doc1@gmail.com");
        driver.findElement(By.name("password")).sendKeys("pass");
        driver.findElement(By.tagName("form")).submit();

        // get dropdown for curr doctor
        WebElement toggle = driver.findElement(By.cssSelector(".dropdown-toggle"));
        toggle.click();
        driver.findElement(By.linkText("Edit Profile"));
        driver.findElement(By.linkText("Logout")).click();
        new WebDriverWait(driver, Duration.ofSeconds(3))
                .until(ExpectedConditions.urlContains("/doctor_login.jsp"));
    }

    @Test
    void verifyPatientListPresent() {
        driver.get("http://localhost:8080/Doctor_Patient_Portal_war/doctor_login.jsp");
        driver.findElement(By.name("email")).sendKeys("doc1@gmail.com");
        driver.findElement(By.name("password")).sendKeys("pass");
        driver.findElement(By.tagName("form")).submit();
        driver.get("http://localhost:8080/Doctor_Patient_Portal_war//doctor/patient.jsp");
        List<WebElement> rows = driver.findElements(By.cssSelector("table tbody tr"));
        assertFalse(rows.isEmpty());
        for (WebElement row : rows) {
            String status = row.findElement(By.xpath("./td[last()-1]")).getText(); // status is second to last td
            WebElement btn = row.findElement(By.xpath("./td[last()]/a")); // action button is last
            if ("Pending".equals(status)) {
                assertTrue(btn.isEnabled());
            } else {
                assertFalse(btn.isEnabled());
            }
        }
    }
}
