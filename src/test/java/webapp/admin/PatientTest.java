package webapp.admin;

import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PatientTest {
  private static Connection conn;
  WebDriver driver;
  WebDriverWait wait;
  @BeforeAll
  public static void setupDB() throws Exception {
    conn = DriverManager.getConnection(
        "jdbc:mysql://localhost:3306/hospital_db?allowMultiQueries=true",
        "root", "rootuser"
    );
    
    String sql = new String(Files.readAllBytes(Paths.get("src/test/resources/PatientTestSetup.sql")));
    
    // Execute setup
    Statement stmt = conn.createStatement();
    stmt.execute(sql);
    
    // Now switch connection to hospital_db
    conn = DriverManager.getConnection(
        "jdbc:mysql://localhost:3306/hospital_db",
        "root", "rootuser"
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
    
    driver = new ChromeDriver();
    wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    
    // Page redirects if not logged in; need to login in as admin first
    driver.get("http://localhost:8080/Doctor_Patient_Portal_war/admin_login.jsp");
    driver.findElement(By.name("email")).sendKeys("admin@gmail.com");
    driver.findElement(By.name("password")).sendKeys("admin");
    driver.findElement(By.className("btn")).click();
    
    // Wait for login to complete
    wait.until(ExpectedConditions.urlContains("admin/index.jsp"));
    
    driver.get("http://localhost:8080/Doctor_Patient_Portal_war/admin/patient.jsp");
  }
  
  // Verify that page title is correct
  @Test
  public void testPageTitle() {
    assertEquals("Patient Details | Admin", driver.getTitle());
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
  public class PatientTableTests {
    // Verify that the grid title is displayed
    @Test
    public void testTitle() {
      WebElement title = driver.findElement(By.xpath("//*[contains(text(),'Patient Details')]"));
      assertTrue(title.isDisplayed());
    }
  
    // Verify that the grid has the correct headers displayed
    @Test
    public void testHeaders() {
      WebElement headerRow = driver.findElement(By.className("table"));
    
      WebElement name = headerRow.findElement(By.xpath("//*[contains(text(),'Full Name')]"));
      assertTrue(name.isDisplayed());
    
      WebElement gender = headerRow.findElement(By.xpath("//*[contains(text(),'Gender')]"));
      assertTrue(gender.isDisplayed());
    
      WebElement age = headerRow.findElement(By.xpath("//*[contains(text(),'Age')]"));
      assertTrue(age.isDisplayed());
    
      WebElement appointment = headerRow.findElement(By.xpath("//*[contains(text(),'Appointment')]"));
      assertTrue(appointment.isDisplayed());
    
      WebElement email = headerRow.findElement(By.xpath("//*[contains(text(),'Email')]"));
      assertTrue(email.isDisplayed());
    
      WebElement phone = headerRow.findElement(By.xpath("//*[contains(text(),'Phone')]"));
      assertTrue(phone.isDisplayed());
    
      WebElement diseases = headerRow.findElement(By.xpath("//*[contains(text(),'Diseases')]"));
      assertTrue(diseases.isDisplayed());
  
      WebElement doc = headerRow.findElement(By.xpath("//*[contains(text(),'Doctor Name')]"));
      assertTrue(doc.isDisplayed());
  
      WebElement address = headerRow.findElement(By.xpath("//*[contains(text(),'Address')]"));
      assertTrue(address.isDisplayed());
  
      WebElement status = headerRow.findElement(By.xpath("//*[contains(text(),'Address')]"));
      assertTrue(status.isDisplayed());
    }
  
    // Verifies that the sample data is correctly displayed in the table
    @Test
    public void testSampleRowData() {
      List<WebElement> tableRows = driver.findElement(By.tagName("tbody")).findElements(By.tagName("tr"));
      WebElement rebeccaRow = tableRows.get(0);
      assertEquals("Rebecca Millstone", rebeccaRow.findElement(By.tagName("th")).getText());
      List<WebElement> rebeccaData = rebeccaRow.findElements(By.tagName("td"));
      assertEquals("2000-04-01", rebeccaData.get(0).getText());
      assertEquals("25", rebeccaData.get(1).getText());
      assertEquals("2025-05-14", rebeccaData.get(2).getText());
      assertEquals("becmill@gmail.com", rebeccaData.get(3).getText());
      assertEquals("609-555-1221", rebeccaData.get(4).getText());
      assertEquals("Unknown", rebeccaData.get(5).getText());
      assertEquals("Steve Winthrop", rebeccaData.get(6).getText());
      assertEquals("457 First St.", rebeccaData.get(7).getText());
      assertEquals("Scheduled", rebeccaData.get(8).getText());
    
      WebElement graceRow = tableRows.get(1);
      assertEquals("Grace Green", graceRow.findElement(By.tagName("th")).getText());
      List<WebElement> graceData = graceRow.findElements(By.tagName("td"));
      assertEquals("2003-12-18", graceData.get(0).getText());
      assertEquals("21", graceData.get(1).getText());
      assertEquals("2025-05-02", graceData.get(2).getText());
      assertEquals("ggreen@gmail.com", graceData.get(3).getText());
      assertEquals("200-555-6789", graceData.get(4).getText());
      assertEquals("Unknown", graceData.get(5).getText());
      assertEquals("Sally Smith", graceData.get(6).getText());
      assertEquals("321 Elm St.", graceData.get(7).getText());
      assertEquals("Scheduled", graceData.get(8).getText());
    }
  }
}
