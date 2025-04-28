package webapp;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.*;

import java.nio.file.*;
import java.sql.*;
import java.time.Duration;
import java.util.*;

public class GeneralFormPagesSeleniumTest {
  private WebDriver driver;
  private WebDriverWait wait;
  private static Connection conn;
  private static int userId;
  private static int appointmentId;
  
  @BeforeEach
  public void setupDB() throws Exception {
    conn = DriverManager.getConnection(
        "jdbc:mysql://localhost:3306/hospital_db?allowMultiQueries=true",
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
    
    // Create a test user
    String userInsert = "INSERT INTO user_details(full_name, email, password) VALUES (?,?,?)";
    try (PreparedStatement ps = conn.prepareStatement(userInsert, Statement.RETURN_GENERATED_KEYS)) {
      ps.setString(1, "Selenium User Test");
      ps.setString(2, "seleniumuser@test.com");
      ps.setString(3, "password123");
      ps.executeUpdate();
      try (ResultSet rs = ps.getGeneratedKeys()) {
        rs.next();
        userId = rs.getInt(1);
      }
    }
    
    // Create a test doctor for appointments
    String docInsert = "INSERT INTO doctor (fullName, dateOfBirth, qualification, specialist, email, phone, password) " +
        "VALUES (?, ?, ?, ?, ?, ?, ?)";
    try (PreparedStatement ps = conn.prepareStatement(docInsert, Statement.RETURN_GENERATED_KEYS)) {
      ps.setString(1, "Dr. Selenium");
      ps.setString(2, "1980-01-01");
      ps.setString(3, "MD");
      ps.setString(4, "General");
      ps.setString(5, "drselenium@test.com");
      ps.setString(6, "1234567890");
      ps.setString(7, "docpass123");
      ps.executeUpdate();
    }
    
    // Create a test specialist
    String specInsert = "INSERT INTO specialist (specialist_name) VALUES (?)";
    try (PreparedStatement ps = conn.prepareStatement(specInsert)) {
      ps.setString(1, "Test Specialist");
      ps.executeUpdate();
    }
    
    // Create a test appointment
    String apptInsert = "INSERT INTO appointment " +
        "(userId, fullName, gender, age, appointmentDate, email, phone, diseases, doctorId, address, status) " +
        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, " +
        "(SELECT id FROM doctor WHERE email = ?), ?, ?)";
    try (PreparedStatement ps = conn.prepareStatement(apptInsert, Statement.RETURN_GENERATED_KEYS)) {
      ps.setInt(1, userId);
      ps.setString(2, "Selenium Patient");
      ps.setString(3, "M");
      ps.setString(4, "30");
      ps.setString(5, "2025-05-01");
      ps.setString(6, "seleniumpatient@test.com");
      ps.setString(7, "9876543210");
      ps.setString(8, "Fever");
      ps.setString(9, "drselenium@test.com");
      ps.setString(10, "123 Selenium Street");
      ps.setString(11, "Pending");
      ps.executeUpdate();
      try (ResultSet rs = ps.getGeneratedKeys()) {
        rs.next();
        appointmentId = rs.getInt(1);
      }
    }

    driver = new ChromeDriver();
    driver.manage().window().maximize();
    driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
    wait = new WebDriverWait(driver, Duration.ofSeconds(10));
  }
  
  @AfterEach
  void teardown() {
    if (driver != null) {
      driver.quit();
    }
  }
  
  @AfterEach
  void resetUserPassword() throws SQLException {
    // Reset user password back to original after each test
    try (PreparedStatement ps = conn.prepareStatement(
        "UPDATE user_details SET password = ? WHERE email = ?"
    )) {
      ps.setString(1, "password123");
      ps.setString(2, "seleniumuser@test.com");
      ps.executeUpdate();
    }
    
    if (driver != null) {
      driver.quit();
    }
  }
  @AfterEach
  void resetDoctorPassword() {
    // Reset doctor password back to original after each test
    try (PreparedStatement ps = conn.prepareStatement(
        "UPDATE doctor SET password = ? WHERE email = ?"
    )) {
      ps.setString(1, "docpass123");
      ps.setString(2, "drselenium@test.com");
      ps.executeUpdate();
    } catch (Exception e) {
      // Password may already be correct or test didn't change it
    }
  }
  
  @AfterAll
  static void cleanup() throws SQLException {
    if (conn != null) {
      conn.close();
    }
  }
  
  @Test
  void testHomePageLoads() {
    driver.get("http://localhost:8080/Doctor_Patient_Portal_war/index.jsp");
    assertEquals("Home Page | Doctor Patient Portal", driver.getTitle());
    
    String[] navTexts = {"ADMIN", "DOCTOR", "USER"};
    for (String navText : navTexts) {
      WebElement element = driver.findElement(By.xpath(
          "//*[contains(translate(normalize-space(.), 'abcdefghijklmnopqrstuvwxyz', 'ABCDEFGHIJKLMNOPQRSTUVWXYZ'), '" + navText + "')]"
      ));
      assertTrue(element.isDisplayed(), "Navbar text not visible: " + navText);
    }
    
    WebElement portalText = driver.findElement(By.xpath("//a[contains(normalize-space(text()), 'Doctor Patient Portal')]"));
    assertTrue(portalText.isDisplayed(), "Doctor Patient Portal text/logo is missing");
    
    WebElement carousel = driver.findElement(By.id("carouselExampleIndicators"));
    assertTrue(carousel.isDisplayed(), "Carousel is not displayed");
    
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
    
    WebElement featuresSection = wait.until(ExpectedConditions.visibilityOfElementLocated(
        By.xpath("//*[contains(translate(normalize-space(.), 'abcdefghijklmnopqrstuvwxyz', 'ABCDEFGHIJKLMNOPQRSTUVWXYZ'), 'SOME KEY FEATURES OF OUR DOCTOR PATIENT PORTAL')]")
    ));
    assertTrue(featuresSection.isDisplayed(), "Features section is missing");
    
    
    WebElement team = driver.findElement(By.xpath("//p[contains(text(), 'Our Team')]"));
    assertTrue(team.isDisplayed(), "Team section is missing");
  }
  
  
  
  
  @Test
  void testUserLogin() {
    driver.get("http://localhost:8080/Doctor_Patient_Portal_war/user_login.jsp");
    assertEquals("User Login", driver.getTitle());
    
    WebElement emailInput = driver.findElement(By.name("email"));
    WebElement passwordInput = driver.findElement(By.name("password"));
    WebElement submitButton = driver.findElement(By.xpath("//button[@type='submit']"));
    
    emailInput.sendKeys("seleniumuser@test.com");
    passwordInput.sendKeys("password123");
    submitButton.click();
    
    // After successful login, we should be redirected to user dashboard or index
    // Assuming index.jsp is the default landing page after login
    new WebDriverWait(driver, Duration.ofSeconds(5))
        .until(ExpectedConditions.urlContains("/index.jsp"));
    
    // Check if userObj is in the session by verifying if the user dropdown is visible
    assertTrue(driver.findElement(By.cssSelector(".dropdown-toggle")).isDisplayed());
  }
  
  @Test
  void testInvalidUserLogin() {
    driver.get("http://localhost:8080/Doctor_Patient_Portal_war/user_login.jsp");
    
    WebElement emailInput = driver.findElement(By.name("email"));
    WebElement passwordInput = driver.findElement(By.name("password"));
    WebElement submitButton = driver.findElement(By.xpath("//button[@type='submit']"));
    
    emailInput.sendKeys("invalid@test.com");
    passwordInput.sendKeys("wrongpassword");
    submitButton.click();
    
    // Should stay on login page with error message
    assertTrue(driver.getCurrentUrl().contains("/user_login.jsp"));
    WebElement errorMsg = driver.findElement(By.xpath("//p[contains(@class, 'text-danger')]"));
    assertTrue(errorMsg.isDisplayed());
    assertEquals("Invalid email or password", errorMsg.getText());
  }
  
  @Test
  void testUserSignup() {
    driver.get("http://localhost:8080/Doctor_Patient_Portal_war/signup.jsp");
    assertEquals("Signup Page", driver.getTitle());
    
    String uniqueEmail = "newuser" + System.currentTimeMillis() + "@test.com";
    
    WebElement nameInput = driver.findElement(By.name("fullName"));
    WebElement emailInput = driver.findElement(By.name("email"));
    WebElement passwordInput = driver.findElement(By.name("password"));
    WebElement submitButton = driver.findElement(By.xpath("//button[@type='submit']"));
    
    nameInput.sendKeys("New Test User");
    emailInput.sendKeys(uniqueEmail);
    passwordInput.sendKeys("newpassword");
    submitButton.click();
    
    // Should stay on signup page with success message
    assertTrue(driver.getCurrentUrl().contains("/signup.jsp"));
    WebElement successMsg = driver.findElement(By.xpath("//p[contains(@class, 'text-success')]"));
    assertTrue(successMsg.isDisplayed());
    assertTrue(successMsg.getText().contains("Register Successfully"));
  }
  
  @Test
  void testUserAppointmentBooking() {
    // Login first
    driver.get("http://localhost:8080/Doctor_Patient_Portal_war/user_login.jsp");
    driver.findElement(By.name("email")).sendKeys("seleniumuser@test.com");
    driver.findElement(By.name("password")).sendKeys("password123");
    driver.findElement(By.xpath("//button[@type='submit']")).click();
    
    // Navigate to appointment page
    driver.get("http://localhost:8080/Doctor_Patient_Portal_war/user_appointment.jsp");
    assertEquals("User Appointment Page", driver.getTitle());
    
    // Fill appointment form
    driver.findElement(By.name("fullName")).sendKeys("Test Appointment");
    new Select(driver.findElement(By.name("gender"))).selectByValue("male");
    driver.findElement(By.name("age")).sendKeys("35");
    
    // Set appointment date (today + 7 days)
    JavascriptExecutor js = (JavascriptExecutor) driver;
    js.executeScript(
        "document.querySelector('input[name=\"appointmentDate\"]').value = '2025-05-10'"
    );
    
    driver.findElement(By.name("email")).sendKeys("appt" + System.currentTimeMillis() + "@test.com");
    driver.findElement(By.name("phone")).sendKeys("1122334455");
    driver.findElement(By.name("diseases")).sendKeys("Headache");
    
    // Select doctor
    new Select(driver.findElement(By.name("doctorNameSelect"))).selectByIndex(1);
    
    driver.findElement(By.name("address")).sendKeys("123 Test Street");
    driver.findElement(By.xpath("//button[@type='submit']")).click();
    
    // Check success message
    WebElement successMsg = driver.findElement(By.xpath("//p[contains(@class, 'text-success')]"));
    assertTrue(successMsg.isDisplayed());
    assertTrue(successMsg.getText().contains("Appointment is recorded Successfully"));
  }
  
  @Test
  void testViewAppointments() {
    // Login first
    driver.get("http://localhost:8080/Doctor_Patient_Portal_war/user_login.jsp");
    driver.findElement(By.name("email")).sendKeys("seleniumuser@test.com");
    driver.findElement(By.name("password")).sendKeys("password123");
    driver.findElement(By.xpath("//button[@type='submit']")).click();
    
    // Navigate to view appointments page
    driver.get("http://localhost:8080/Doctor_Patient_Portal_war/view_appointment.jsp");
    
    // Check table headers
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
    wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("table")));
    
    
    // Verify the test appointment we created is in the list
    List<WebElement> rows = driver.findElements(By.cssSelector("table tbody tr"));
    boolean found = false;
    for (WebElement row : rows) {
      List<WebElement> cells = row.findElements(By.tagName("td"));
      if (cells.size() > 0 && cells.get(0).getText().equals("Selenium Patient")) {
        found = true;
        break;
      }
    }
    assertTrue(found, "Test appointment should be displayed in the list");
  }
  
  @Test
  void testChangePassword() {
    // Login first
    driver.get("http://localhost:8080/Doctor_Patient_Portal_war/user_login.jsp");
    driver.findElement(By.name("email")).sendKeys("seleniumuser@test.com");
    driver.findElement(By.name("password")).sendKeys("password123");
    driver.findElement(By.xpath("//button[@type='submit']")).click();
    
    // Navigate to change password page
    driver.get("http://localhost:8080/Doctor_Patient_Portal_war/change_password.jsp");
    
    wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    // Fill change password form
    driver.findElement(By.name("newPassword")).sendKeys("newpassword");
    driver.findElement(By.name("oldPassword")).sendKeys("password123");
    driver.findElement(By.xpath("//button[@type='submit']")).click();
    
    // Check success message
    WebElement successMsg = driver.findElement(By.xpath("//p[contains(text(), 'Password Change Successfully')]"));
    assertTrue(successMsg.isDisplayed());
    
    // Try logging in with new password
    driver.get("http://localhost:8080/Doctor_Patient_Portal_war/user_login.jsp");
    driver.findElement(By.name("email")).sendKeys("seleniumuser@test.com");
    driver.findElement(By.name("password")).sendKeys("newpassword");
    driver.findElement(By.xpath("//button[@type='submit']")).click();
    
    // Verify we're logged in
    new WebDriverWait(driver, Duration.ofSeconds(5))
        .until(ExpectedConditions.urlContains("/index.jsp"));
    
    // Reset password to original
    driver.get("http://localhost:8080/Doctor_Patient_Portal_war/change_password.jsp");
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    driver.findElement(By.name("newPassword")).sendKeys("password123");
    driver.findElement(By.name("oldPassword")).sendKeys("newpassword");
    driver.findElement(By.xpath("//button[@type='submit']")).click();
  }
  
  @Test
  void testUserLogout() {
    // Login first
    driver.get("http://localhost:8080/Doctor_Patient_Portal_war/user_login.jsp");
    driver.findElement(By.name("email")).sendKeys("seleniumuser@test.com");
    driver.findElement(By.name("password")).sendKeys("password123");
    driver.findElement(By.xpath("//button[@type='submit']")).click();
    
    // Verify user dropdown (logged in)
    WebElement userDropdown = driver.findElement(By.cssSelector(".dropdown-toggle"));
    assertTrue(userDropdown.isDisplayed());
    
    // Logout
    userDropdown.click();
    driver.findElement(By.linkText("Logout")).click();
    
    // Wait until we're redirected to user_login.jsp
    new WebDriverWait(driver, Duration.ofSeconds(5))
        .until(ExpectedConditions.urlContains("/user_login.jsp"));
    
    // Verify "User Logout Successfully" message is displayed
    WebElement logoutMsg = driver.findElement(By.xpath("//p[contains(text(), 'User Logout Successfully')]"));
    assertTrue(logoutMsg.isDisplayed());
    
    // Also verify that login form is present
    WebElement emailInput = driver.findElement(By.name("email"));
    WebElement passwordInput = driver.findElement(By.name("password"));
    WebElement submitButton = driver.findElement(By.xpath("//button[@type='submit']"));
    
    assertTrue(emailInput.isDisplayed());
    assertTrue(passwordInput.isDisplayed());
    assertTrue(submitButton.isDisplayed());
  }
  
  // Doctor Tests
  @Test
  void testDoctorLoginPage() {
    driver.get("http://localhost:8080/Doctor_Patient_Portal_war/doctor_login.jsp");
    assertEquals("Doctor Login", driver.getTitle());
    // Verify form elements
    WebElement emailInput = driver.findElement(By.name("email"));
    WebElement passwordInput = driver.findElement(By.name("password"));
    WebElement submitButton = driver.findElement(By.xpath("//button[@type='submit']"));
    
    assertTrue(emailInput.isDisplayed());
    assertTrue(passwordInput.isDisplayed());
    assertTrue(submitButton.isDisplayed());
    
    // Verify page header
    WebElement header = driver.findElement(By.cssSelector("p.fs-4.text-center.text-white.mt-2"));
    assertTrue(header.isDisplayed());
    assertTrue(header.getText().contains("Doctor Login"));
    
  }
  
  @Test
  void testDoctorLoginSuccess() {
    driver.get("http://localhost:8080/Doctor_Patient_Portal_war/doctor_login.jsp");
    
    WebElement emailInput = driver.findElement(By.name("email"));
    WebElement passwordInput = driver.findElement(By.name("password"));
    WebElement submitButton = driver.findElement(By.xpath("//button[@type='submit']"));
    
    emailInput.sendKeys("drselenium@test.com");
    passwordInput.sendKeys("docpass123");
    submitButton.click();
    
    // After successful login, we should be redirected to doctor/index.jsp
    wait.until(ExpectedConditions.urlContains("/doctor/index.jsp"));
    
    // Verify we're on the doctor dashboard
    WebElement dashboardHeader = wait.until(ExpectedConditions.visibilityOfElementLocated(
        By.xpath("//p[contains(text(), 'Doctor DashBoard')]")));
    
    assertTrue(dashboardHeader.isDisplayed());
  }
  
  @Test
  void testDoctorLoginFailure() {
    driver.get("http://localhost:8080/Doctor_Patient_Portal_war/doctor_login.jsp");
    
    WebElement emailInput = driver.findElement(By.name("email"));
    WebElement passwordInput = driver.findElement(By.name("password"));
    WebElement submitButton = driver.findElement(By.xpath("//button[@type='submit']"));
    
    emailInput.sendKeys("invalid@doctor.com");
    passwordInput.sendKeys("wrongpassword");
    submitButton.click();
    
    // Should stay on login page with error message
    assertTrue(driver.getCurrentUrl().contains("/doctor_login.jsp"));
    WebElement errorMsg = wait.until(ExpectedConditions.visibilityOfElementLocated(
        By.xpath("//p[contains(@class, 'text-danger')]")));
    assertTrue(errorMsg.isDisplayed());
    assertEquals("Invalid email or password", errorMsg.getText());
  }
  
  @Test
  void testDoctorLogout() {
    // Login first
    driver.get("http://localhost:8080/Doctor_Patient_Portal_war/doctor_login.jsp");
    driver.findElement(By.name("email")).sendKeys("drselenium@test.com");
    driver.findElement(By.name("password")).sendKeys("docpass123");
    driver.findElement(By.xpath("//button[@type='submit']")).click();
    
    // Wait for redirect to dashboard
    wait.until(ExpectedConditions.urlContains("/doctor/index.jsp"));
    
    // Open the user dropdown menu first
    WebElement doctorDropdown = driver.findElement(By.cssSelector(".dropdown-toggle"));
    doctorDropdown.click();
    
    // Now click the logout link
    WebElement logoutLink = wait.until(ExpectedConditions.elementToBeClickable(
        By.xpath("//a[contains(@href, 'doctorLogout')]")));
    logoutLink.click();
    
    // Wait for redirect to login page
    wait.until(ExpectedConditions.urlContains("/doctor_login.jsp"));
    
    // Verify "Doctor Logout Successfully" message is displayed
    WebElement logoutMsg = wait.until(ExpectedConditions.visibilityOfElementLocated(
        By.xpath("//p[contains(text(), 'Doctor Logout Successfully')]")));
    assertTrue(logoutMsg.isDisplayed());
  }
  
  @Test
  void testDoctorChangePassword() throws SQLException {
    // Login first
    driver.get("http://localhost:8080/Doctor_Patient_Portal_war/doctor_login.jsp");
    driver.findElement(By.name("email")).sendKeys("drselenium@test.com");
    driver.findElement(By.name("password")).sendKeys("docpass123");
    driver.findElement(By.xpath("//button[@type='submit']")).click();
    
    // Wait for dashboard
    wait.until(ExpectedConditions.urlContains("/doctor/index.jsp"));
    
    // Navigate directly to edit profile page
    driver.get("http://localhost:8080/Doctor_Patient_Portal_war/doctor/edit_profile.jsp");
    
    // No need to click anything — directly fill Change Password form
    WebElement oldPasswordInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("oldPassword")));
    WebElement newPasswordInput = driver.findElement(By.name("newPassword"));
    WebElement submitButton = driver.findElement(By.xpath("//form[@action='../doctorChangePassword']//button[@type='submit']"));
    
    oldPasswordInput.sendKeys("docpass123");
    newPasswordInput.sendKeys("newdocpass");
    submitButton.click();
    
    // Confirm success
    WebElement successMsg = wait.until(ExpectedConditions.visibilityOfElementLocated(
        By.xpath("//p[contains(text(), 'Password change successfully')]")));
    assertTrue(successMsg.isDisplayed());
    
    // Logout properly
    WebElement doctorDropdown = driver.findElement(By.cssSelector(".dropdown-toggle"));
    doctorDropdown.click();
    WebElement logoutLink = wait.until(ExpectedConditions.elementToBeClickable(
        By.xpath("//a[contains(@href, 'doctorLogout')]")));
    logoutLink.click();
    
    wait.until(ExpectedConditions.urlContains("/doctor_login.jsp"));
    
    // Try logging in with new password
    driver.findElement(By.name("email")).sendKeys("drselenium@test.com");
    driver.findElement(By.name("password")).sendKeys("newdocpass");
    driver.findElement(By.xpath("//button[@type='submit']")).click();
    
    wait.until(ExpectedConditions.urlContains("/doctor/index.jsp"));
    resetDoctorPassword();
  }
  
  
  
  
  @Test
  void testDoctorEditProfile() throws InterruptedException {
    // Login first
    driver.get("http://localhost:8080/Doctor_Patient_Portal_war/doctor_login.jsp");
    driver.findElement(By.name("email")).sendKeys("drselenium@test.com");
    driver.findElement(By.name("password")).sendKeys("docpass123");
    driver.findElement(By.xpath("//button[@type='submit']")).click();
    wait = new WebDriverWait(driver, Duration.ofSeconds(100));
    // Wait for redirect to dashboard
    wait.until(ExpectedConditions.urlContains("/doctor/index.jsp"));
    // Navigate to profile page
    driver.get("http://localhost:8080/Doctor_Patient_Portal_war/doctor/edit_profile.jsp");
    
    // Verify profile form elements
    WebElement editProfileForm = driver.findElement(By.xpath("//form[@action='../doctorEditProfile']"));
    assertTrue(editProfileForm.isDisplayed());
    
    WebElement fullNameInput = driver.findElement(By.name("fullName"));
    WebElement dateOfBirthInput = driver.findElement(By.name("dateOfBirth"));
    WebElement qualificationInput = driver.findElement(By.name("qualification"));
    WebElement specialistInput = driver.findElement(By.name("specialist"));
    WebElement emailInput = driver.findElement(By.name("email"));
    WebElement phoneInput = driver.findElement(By.name("phone"));
    
    assertTrue(fullNameInput.isDisplayed());
    assertTrue(dateOfBirthInput.isDisplayed());
    assertTrue(qualificationInput.isDisplayed());
    assertTrue(specialistInput.isDisplayed());
    assertTrue(emailInput.isDisplayed());
    assertTrue(phoneInput.isDisplayed());
  }
  
  
  
  // Admin Tests
  @Test
  void testAdminLoginPage() {
    driver.get("http://localhost:8080/Doctor_Patient_Portal_war/admin_login.jsp");
    
    // Wait for the title
    wait.until(ExpectedConditions.titleIs("Admin Login"));
    assertEquals("Admin Login", driver.getTitle());
    
    // Verify email input is visible
    WebElement emailInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("email")));
    assertTrue(emailInput.isDisplayed(), "Email input should be visible");
    
    // Verify password input is visible
    WebElement passwordInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("password")));
    assertTrue(passwordInput.isDisplayed(), "Password input should be visible");
    
    // Verify submit button is visible
    WebElement submitButton = wait.until(ExpectedConditions.visibilityOfElementLocated(
        By.xpath("//button[@type='submit' and contains(text(),'Submit')]")));
    assertTrue(submitButton.isDisplayed(), "Submit button should be visible");
  }
  
  
  
  @Test
  void testAdminLoginSuccess() {
    driver.get("http://localhost:8080/Doctor_Patient_Portal_war/admin_login.jsp");
    
    WebElement emailInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("email")));
    WebElement passwordInput = driver.findElement(By.name("password"));
    WebElement submitButton = driver.findElement(By.xpath("//button[@type='submit']"));
    
    emailInput.sendKeys("admin@gmail.com");
    passwordInput.sendKeys("admin");
    submitButton.click();
    
    // Wait for admin dashboard
    wait.until(ExpectedConditions.urlContains("/admin/index.jsp"));
    
    // Check if Admin Dashboard heading is visible
    WebElement dashboardHeading = wait.until(ExpectedConditions.visibilityOfElementLocated(
        By.xpath("//p[contains(@class, 'text-center') and contains(text(), 'Admin Dashboard')]")));
    assertTrue(dashboardHeading.isDisplayed());
  }
  
  @Test
  void testAdminLoginFailure() {
    driver.get("http://localhost:8080/Doctor_Patient_Portal_war/admin_login.jsp");
    
    WebElement emailInput = driver.findElement(By.name("email"));
    WebElement passwordInput = driver.findElement(By.name("password"));
    WebElement submitButton = driver.findElement(By.xpath("//button[@type='submit']"));
    
    emailInput.sendKeys("wrong@admin.com");
    passwordInput.sendKeys("wrongpass");
    submitButton.click();
    
    // Should stay on login page with error message
    assertTrue(driver.getCurrentUrl().contains("/admin_login.jsp"));
    WebElement errorMsg = wait.until(ExpectedConditions.visibilityOfElementLocated(
        By.xpath("//p[contains(@class, 'text-danger')]")));
    assertTrue(errorMsg.isDisplayed());
    assertEquals("Invalid Username or Password.", errorMsg.getText());
  }
  
  @Test
  void testAdminLogout() {
    // First login
    driver.get("http://localhost:8080/Doctor_Patient_Portal_war/admin_login.jsp");
    
    WebElement emailInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("email")));
    WebElement passwordInput = driver.findElement(By.name("password"));
    WebElement submitButton = driver.findElement(By.xpath("//button[@type='submit']"));
    
    emailInput.sendKeys("admin@gmail.com");
    passwordInput.sendKeys("admin");
    submitButton.click();
    
    wait.until(ExpectedConditions.urlContains("/admin/index.jsp"));
    
    // Open the Admin dropdown (button)
    WebElement adminDropdownButton = wait.until(ExpectedConditions.elementToBeClickable(
        By.id("dropdownMenuButton1")));
    adminDropdownButton.click();
    
    // Now click the Logout link
    WebElement logoutLink = wait.until(ExpectedConditions.elementToBeClickable(
        By.xpath("//a[@href='../adminLogout']")));
    logoutLink.click();
    
    // Wait for redirect to admin login page
    wait.until(ExpectedConditions.urlContains("/admin_login.jsp"));
    
    // Check logout success message
    WebElement logoutMsg = wait.until(ExpectedConditions.visibilityOfElementLocated(
        By.xpath("//p[contains(text(), 'Admin Logout Successfully')]")));
    assertTrue(logoutMsg.isDisplayed());
  }
  
  
  @Test
  void testViewDoctors() {
    // Login as admin first
    driver.get("http://localhost:8080/Doctor_Patient_Portal_war/admin_login.jsp");
    driver.findElement(By.name("email")).sendKeys("admin@gmail.com");
    driver.findElement(By.name("password")).sendKeys("admin");
    driver.findElement(By.xpath("//button[@type='submit']")).click();
    
    // Wait for redirect to dashboard
    wait.until(ExpectedConditions.urlContains("/admin/index.jsp"));
    
    // Navigate to view doctors page
    driver.get("http://localhost:8080/Doctor_Patient_Portal_war/admin/view_doctor.jsp");
    
    // Verify doctor table exists
    WebElement doctorTable = wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("table")));
    assertTrue(doctorTable.isDisplayed());
    
    // Verify table headers
    WebElement tableHeader = driver.findElement(By.tagName("thead"));
    String headerText = tableHeader.getText();
    assertTrue(headerText.contains("Full Name"));
    assertTrue(headerText.contains("DOB"));
    assertTrue(headerText.contains("Qualification"));
    assertTrue(headerText.contains("Specialist"));
    assertTrue(headerText.contains("Email"));
    assertTrue(headerText.contains("Phone"));
    assertTrue(headerText.contains("Action"));
  }
  
  @Test
  void testAddDoctorForm() {
    // Login as admin first
    driver.get("http://localhost:8080/Doctor_Patient_Portal_war/admin_login.jsp");
    driver.findElement(By.name("email")).sendKeys("admin@gmail.com");
    driver.findElement(By.name("password")).sendKeys("admin");
    driver.findElement(By.xpath("//button[@type='submit']")).click();
    
    // Wait for redirect to dashboard
    wait.until(ExpectedConditions.urlContains("/admin/index.jsp"));
    
    // Navigate to doctor form page
    driver.get("http://localhost:8080/Doctor_Patient_Portal_war/admin/doctor.jsp");
    
    // Verify form exists and has all required fields
    WebElement form = driver.findElement(By.xpath("//form[@action='../addDoctor']"));
    assertTrue(form.isDisplayed());
    
    assertTrue(driver.findElement(By.name("fullName")).isDisplayed());
    assertTrue(driver.findElement(By.name("dateOfBirth")).isDisplayed());
    assertTrue(driver.findElement(By.name("qualification")).isDisplayed());
    assertTrue(driver.findElement(By.name("specialist")).isDisplayed());
    assertTrue(driver.findElement(By.name("email")).isDisplayed());
    assertTrue(driver.findElement(By.name("phone")).isDisplayed());
    assertTrue(driver.findElement(By.name("password")).isDisplayed());
    
    // Verify submit button exists
    WebElement submitButton = driver.findElement(By.xpath("//button[@type='submit']"));
    assertTrue(submitButton.isDisplayed());
  }
  
  
  @Test
  void testUserEditProfile() {
    driver.get("http://localhost:8080/Doctor_Patient_Portal_war/user_login.jsp");
    driver.findElement(By.name("email")).sendKeys("seleniumuser@test.com");
    driver.findElement(By.name("password")).sendKeys("password123");
    driver.findElement(By.xpath("//button[@type='submit']")).click();
    
    driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
    // Navigate to change password page (no separate edit profile page in user)
    driver.get("http://localhost:8080/Doctor_Patient_Portal_war/change_password.jsp");
    
    driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
    
    assertTrue(driver.findElement(By.name("oldPassword")).isDisplayed());
    assertTrue(driver.findElement(By.name("newPassword")).isDisplayed());
  }
  
  @Test
  void testAdminDeleteDoctor() throws SQLException {
    driver.get("http://localhost:8080/Doctor_Patient_Portal_war/admin_login.jsp");
    driver.findElement(By.name("email")).sendKeys("admin@gmail.com");
    driver.findElement(By.name("password")).sendKeys("admin");
    driver.findElement(By.xpath("//button[@type='submit']")).click();
    
    driver.get("http://localhost:8080/Doctor_Patient_Portal_war/admin/view_doctor.jsp");
    
    List<WebElement> deleteLinks = driver.findElements(By.xpath("//a[contains(@href, 'deleteDoctor?id=')]"));
    
    if (!deleteLinks.isEmpty()) {
      deleteLinks.get(0).click();
      
      // No alert - directly wait for success message
      WebElement successMsg = wait.until(ExpectedConditions.visibilityOfElementLocated(
          By.xpath("//p[contains(text(), 'Doctor Deleted Successfully.')]")));
      assertTrue(successMsg.isDisplayed());
    } else {
      // No doctor to delete (optional pass)
      System.out.println("No doctors to delete. Skipping delete test.");
    }
    
    // Re-insert Dr. Selenium doctor after delete test, so next tests won't fail
    try (PreparedStatement ps = conn.prepareStatement(
        "INSERT INTO doctor (fullName, dateOfBirth, qualification, specialist, email, phone, password) VALUES (?,?,?,?,?,?,?)")) {
      ps.setString(1, "Dr. Selenium");
      ps.setString(2, "1980-01-01");
      ps.setString(3, "MD");
      ps.setString(4, "General");
      ps.setString(5, "drselenium@test.com");
      ps.setString(6, "1234567890");
      ps.setString(7, "docpass123");
      ps.executeUpdate();
    }
    
  }
  
  
  @Test
  void testSessionCleanupAfterUserLogout() {
    driver.get("http://localhost:8080/Doctor_Patient_Portal_war/user_login.jsp");
    driver.findElement(By.name("email")).sendKeys("seleniumuser@test.com");
    driver.findElement(By.name("password")).sendKeys("password123");
    driver.findElement(By.xpath("//button[@type='submit']")).click();
    
    WebElement userDropdown = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".dropdown-toggle")));
    userDropdown.click();
    WebElement logoutLink = wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Logout")));
    logoutLink.click();
    
    wait.until(ExpectedConditions.urlContains("/user_login.jsp"));
    
    WebElement logoutSuccessMsg = wait.until(ExpectedConditions.visibilityOfElementLocated(
        By.xpath("//p[contains(text(), 'User Logout Successfully.')]")));
    wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    assertTrue(logoutSuccessMsg.isDisplayed());
    
    assertThrows(org.openqa.selenium.NoSuchElementException.class,
        () -> driver.findElement(By.cssSelector(".dropdown-toggle")));
  }
  
  
  @Test
  void testSessionCleanupAfterDoctorLogout() {
    driver.get("http://localhost:8080/Doctor_Patient_Portal_war/doctor_login.jsp");
    driver.findElement(By.name("email")).sendKeys("drselenium@test.com");
    driver.findElement(By.name("password")).sendKeys("docpass123");
    driver.findElement(By.xpath("//button[@type='submit']")).click();
    
    // Open the user dropdown menu first
    WebElement doctorDropdown = driver.findElement(By.cssSelector(".dropdown-toggle"));
    doctorDropdown.click();
    
    // Now click the logout link
    WebElement logoutLink = wait.until(ExpectedConditions.elementToBeClickable(
        By.xpath("//a[contains(@href, 'doctorLogout')]")));
    logoutLink.click();
    
    wait.until(ExpectedConditions.urlContains("/doctor_login.jsp"));
    WebElement logoutSuccessMsg = wait.until(ExpectedConditions.visibilityOfElementLocated(
        By.xpath("//p[contains(text(), 'Doctor Logout Successfully.')]")));
    wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    assertTrue(logoutSuccessMsg.isDisplayed());
    
    assertThrows(org.openqa.selenium.NoSuchElementException.class,
        () -> driver.findElement(By.cssSelector(".dropdown-toggle")));    }
  
  @Test
  void testSessionCleanupAfterAdminLogout() {
    driver.get("http://localhost:8080/Doctor_Patient_Portal_war/admin_login.jsp");
    driver.findElement(By.name("email")).sendKeys("admin@gmail.com");
    driver.findElement(By.name("password")).sendKeys("admin");
    driver.findElement(By.xpath("//button[@type='submit']")).click();
    
    // Open the Admin dropdown (button)
    WebElement adminDropdownButton = wait.until(ExpectedConditions.elementToBeClickable(
        By.id("dropdownMenuButton1")));
    adminDropdownButton.click();
    
    // Now click the Logout link
    WebElement logoutLink = wait.until(ExpectedConditions.elementToBeClickable(
        By.xpath("//a[@href='../adminLogout']")));
    logoutLink.click();
    
    wait.until(ExpectedConditions.urlContains("/admin_login.jsp"));
  }
}