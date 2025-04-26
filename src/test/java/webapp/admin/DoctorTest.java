package webapp.admin;

import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DoctorTest {
  private static Connection conn;
  WebDriver driver;
  WebDriverWait wait;
  @BeforeAll
  public static void setupDB() throws Exception {
    conn = DriverManager.getConnection(
        "jdbc:mysql://localhost:3306/hospital_db?allowMultiQueries=true",
        "root", "root"
    );
  
    String sql = new String(Files.readAllBytes(Paths.get("src/test/resources/DoctorTestSetup.sql")));
  
    // Execute setup
    Statement stmt = conn.createStatement();
    stmt.execute(sql);
  
    // Now switch connection to hospital_db
    conn = DriverManager.getConnection(
        "jdbc:mysql://localhost:3306/hospital_db",
        "root", "root"
    );
  }
  
  @AfterEach
  public void rollback() throws SQLException {
    conn.rollback(); // undo db changes
    Statement stmt = conn.createStatement();
    stmt.execute("SET FOREIGN_KEY_CHECKS = 1");
  }
  
  @AfterAll
  public static void cleanup() throws SQLException {
    if (conn != null) conn.close();
  }
  
  
  @BeforeEach
  public void setup() throws SQLException {
    conn.setAutoCommit(false);
  
    Statement stmt = conn.createStatement();
    stmt.execute("SET FOREIGN_KEY_CHECKS=0");
  
    // reset auto-increment and clear data in tables
    // stmt.execute("TRUNCATE TABLE appointment");
    // stmt.execute("TRUNCATE TABLE user_details");
    // stmt.execute("TRUNCATE TABLE specialist");
    stmt.execute("TRUNCATE TABLE doctor");
    
    driver = new ChromeDriver();
    wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    
    // Page redirects if not logged in; need to login in as admin first
    driver.get("http://localhost:8080/Doctor_Patient_Portal_war/admin_login.jsp");
    driver.findElement(By.name("email")).sendKeys("admin@gmail.com");
    driver.findElement(By.name("password")).sendKeys("admin");
    driver.findElement(By.className("btn")).click();
    
    // Wait for login to complete
    wait.until(ExpectedConditions.urlContains("admin/index.jsp"));
    
    driver.get("http://localhost:8080/Doctor_Patient_Portal_war/admin/doctor.jsp");
  }
  
  // Verify that page title is correct
  @Test
  public void testPageTitle() {
    assertEquals("Doctor page", driver.getTitle());
  }
  
  @Nested
  public class NavigationBarTests {
    // Verify that navigation bar is displayed
    @Test
    public void testNavigationBarDisplay() {
      WebElement navbar = driver.findElement(By.tagName("nav"));
      assertTrue(navbar.isDisplayed());
    }
    
    // Verify that navigation bar's site id is displayed and redirects correctly
    @Test
    public void testNavigationBarSiteId() {
      WebElement navbar = driver.findElement(By.tagName("nav"));
      
      WebElement siteId = navbar.findElement(By.linkText("Doctor Patient Portal"));
      assertTrue(siteId.isDisplayed());
      siteId.click();
      assertEquals("http://localhost:8080/Doctor_Patient_Portal_war/admin/index.jsp", driver.getCurrentUrl());
    }
    
    // Verify that navigation bar's home element is displayed and redirects correctly
    @Test
    public void testNavigationBarHomeElement() {
      WebElement navbar = driver.findElement(By.tagName("nav"));
      
      WebElement home = navbar.findElement(By.linkText("HOME"));
      assertTrue(home.isDisplayed());
      home.click();
      assertEquals("http://localhost:8080/Doctor_Patient_Portal_war/admin/index.jsp", driver.getCurrentUrl());
    }
    
    // Verify that navigation bar's doctor element is displayed and redirects correctly
    @Test
    public void testNavigationBarDoctorElement() {
      WebElement navbar = driver.findElement(By.tagName("nav"));
      
      WebElement doctor = navbar.findElement(By.linkText("DOCTOR"));
      assertTrue(doctor.isDisplayed());
      doctor.click();
      assertEquals("http://localhost:8080/Doctor_Patient_Portal_war/admin/doctor.jsp", driver.getCurrentUrl());
    }
    
    // Verify that navigation bar's view doctor element is displayed and redirects correctly
    @Test
    public void testNavigationBarViewDoctorElement() {
      WebElement navbar = driver.findElement(By.tagName("nav"));
      
      WebElement viewDoctor = navbar.findElement(By.linkText("VIEW DOCTOR"));
      assertTrue(viewDoctor.isDisplayed());
      viewDoctor.click();
      assertEquals("http://localhost:8080/Doctor_Patient_Portal_war/admin/view_doctor.jsp", driver.getCurrentUrl());
    }
    
    // Verify that navigation bar's patient element is displayed and redirects correctly
    @Test
    public void testNavigationBarPatientElement() {
      WebElement navbar = driver.findElement(By.tagName("nav"));
      
      WebElement patient = navbar.findElement(By.linkText("PATIENT"));
      assertTrue(patient.isDisplayed());
      patient.click();
      assertEquals("http://localhost:8080/Doctor_Patient_Portal_war/admin/patient.jsp", driver.getCurrentUrl());
    }
    
    // Verify that navigation bar's logout element is displayed and redirects correctly
    @Test
    public void testNavigationBarLogoutElement() {
      WebElement navbar = driver.findElement(By.tagName("nav"));
      
      WebElement button = navbar.findElement(By.id("dropdownMenuButton1"));
      assertTrue(button.isDisplayed());
      button.click();
      
      WebElement logout = navbar.findElement(By.className("dropdown-item"));
      assertTrue(logout.isDisplayed());
      logout.click();
      assertEquals("http://localhost:8080/Doctor_Patient_Portal_war/admin_login.jsp", driver.getCurrentUrl());
    }
  }
  
  @Nested
  public class AddDoctorFormTests {
    // Verify that the form title is displayed
    @Test
    public void testTitle() {
      WebElement title = driver.findElement(By.xpath("//*[contains(text(),'Add Doctor')]"));
      assertTrue(title.isDisplayed());
    }
    
    // Verify that form works and adds doctor successfully
    @Test
    public void testAddDoctorFormSuccess() {
      WebElement form = driver.findElement(By.tagName("form"));
      
      WebElement name = form.findElement(By.name("fullName"));
      WebElement bDay = form.findElement(By.name("dateOfBirth"));
      WebElement qualification = form.findElement(By.name("qualification"));
      WebElement specialist = form.findElement(By.name("specialist"));
      Select select = new Select(specialist);
      WebElement email = form.findElement(By.name("email"));
      WebElement phone = form.findElement(By.name("phone"));
      WebElement password = form.findElement(By.name("password"));
      
      name.sendKeys("Dr. Jonathan Doe");
      bDay.sendKeys("02/22/1969");
      qualification.sendKeys("MD");
      select.selectByIndex(1);
      email.sendKeys("jonathan.doe@hospital.com");
      phone.sendKeys("848-555-2442");
      password.sendKeys("J0n4th@nD03");
      
      WebElement submit = form.findElement(By.tagName("button"));
      submit.submit();
      
      WebElement success = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(text(),'Doctor added Successfully')]")));
      assertTrue(success.isDisplayed());
    }
    
    // Verify that form shows failure message when duplicate doctor is attempted to be added
    @Test
    public void testAddDoctorFailureDuplicate() {
      WebElement form = driver.findElement(By.tagName("form"));
  
      // First addition
      WebElement name = form.findElement(By.name("fullName"));
      WebElement bDay = form.findElement(By.name("dateOfBirth"));
      WebElement qualification = form.findElement(By.name("qualification"));
      WebElement specialist = form.findElement(By.name("specialist"));
      Select select = new Select(specialist);
      WebElement email = form.findElement(By.name("email"));
      WebElement phone = form.findElement(By.name("phone"));
      WebElement password = form.findElement(By.name("password"));
  
      name.sendKeys("Dr. Jonathan Doe");
      bDay.sendKeys("02/22/1969");
      qualification.sendKeys("MD");
      select.selectByIndex(1);
      email.sendKeys("jonathan.doe@hospital.com");
      phone.sendKeys("848-555-2442");
      password.sendKeys("J0n4th@nD03");
  
      WebElement submit = form.findElement(By.tagName("button"));
      submit.submit();
  
      // Second addition attempt
      form = driver.findElement(By.tagName("form"));
      name = form.findElement(By.name("fullName"));
      bDay = form.findElement(By.name("dateOfBirth"));
      qualification = form.findElement(By.name("qualification"));
      specialist = form.findElement(By.name("specialist"));
      select = new Select(specialist);
      email = form.findElement(By.name("email"));
      phone = form.findElement(By.name("phone"));
      password = form.findElement(By.name("password"));
      
      name.sendKeys("Dr. Jonathan Doe");
      bDay.sendKeys("02/22/1969");
      qualification.sendKeys("MD");
      select.selectByIndex(1);
      email.sendKeys("jonathan.doe@hospital.com");
      phone.sendKeys("848-555-2442");
      password.sendKeys("J0n4th@nD03");
  
      submit = form.findElement(By.tagName("button"));
      submit.submit();
  
      WebElement failure = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(text(),'Something went wrong on server!')]")));
      assertTrue(failure.isDisplayed());
    }
    
    // Verify that form shows failure message when submitted with all fields empty
    @Test
    public void testAddDoctorEmptyForm() {
      WebElement form = driver.findElement(By.tagName("form"));
      
      WebElement submit = form.findElement(By.tagName("button"));
      submit.submit();
      
      WebElement failure = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(text(),'Something went wrong on server!')]")));
      assertTrue(failure.isDisplayed());
    }
  }
}