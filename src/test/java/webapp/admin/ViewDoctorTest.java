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

import static org.junit.jupiter.api.Assertions.*;

public class ViewDoctorTest {
  private static Connection conn;
  WebDriver driver;
  WebDriverWait wait;
  
  @BeforeEach
  public void setup() throws Exception {
    conn = DriverManager.getConnection(
        "jdbc:mysql://localhost:3306/hospital_db?allowMultiQueries=true",
        "root", "rootuser"
    );
  
    String sql = new String(Files.readAllBytes(Paths.get("src/test/resources/ViewDoctorTestSetup.sql")));
  
    // Execute setup
    Statement stmt = conn.createStatement();
    stmt.execute(sql);
  
    // Now switch connection to hospital_db
    conn = DriverManager.getConnection(
        "jdbc:mysql://localhost:3306/hospital_db",
        "root", "rootuser"
    );
    
    driver = new ChromeDriver();
    wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    
    // Page redirects if not logged in; need to login in as admin first
    driver.get("http://localhost:8080/Doctor_Patient_Portal_war/admin_login.jsp");
    driver.findElement(By.name("email")).sendKeys("admin@gmail.com");
    driver.findElement(By.name("password")).sendKeys("admin");
    driver.findElement(By.className("btn")).click();
    
    // Wait for login to complete
    wait.until(ExpectedConditions.urlContains("admin/index.jsp"));
    
    driver.get("http://localhost:8080/Doctor_Patient_Portal_war/admin/view_doctor.jsp");
  }
  
  @AfterEach
  public void teardown() {
    driver.quit();
  }
  
  @AfterAll
  public static void cleanup() throws SQLException {
    if (conn != null) conn.close();
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
  public class DoctorTableTests {
    // Verify that the grid title is displayed
    @Test
    public void testTitle() {
      WebElement title = driver.findElement(By.xpath("//*[contains(text(),'List of Doctors')]"));
      assertTrue(title.isDisplayed());
    }
    
    // Verify that the grid has the correct headers displayed
    @Test
    public void testHeaders() {
      WebElement headerRow = driver.findElement(By.className("table-info"));
      
      WebElement name = headerRow.findElement(By.xpath("//*[contains(text(),'Full Name')]"));
      assertTrue(name.isDisplayed());
      
      WebElement dob = headerRow.findElement(By.xpath("//*[contains(text(),'DOB')]"));
      assertTrue(dob.isDisplayed());
  
      WebElement qualification = headerRow.findElement(By.xpath("//*[contains(text(),'Qualification')]"));
      assertTrue(qualification.isDisplayed());
  
      WebElement specialist = headerRow.findElement(By.xpath("//*[contains(text(),'Specialist')]"));
      assertTrue(specialist.isDisplayed());
  
      WebElement email = headerRow.findElement(By.xpath("//*[contains(text(),'Email')]"));
      assertTrue(email.isDisplayed());
  
      WebElement phone = headerRow.findElement(By.xpath("//*[contains(text(),'Phone')]"));
      assertTrue(phone.isDisplayed());
  
      WebElement action = headerRow.findElement(By.xpath("//*[contains(text(),'Action')]"));
      assertTrue(action.isDisplayed());
    }
    
    // Verifies that the sample data is correctly displayed in the table
    @Test
    public void testSampleRowData() {
      List<WebElement> tableRows = driver.findElement(By.tagName("tbody")).findElements(By.tagName("tr"));
      WebElement steveRow = tableRows.get(0);
      assertEquals("Steve Winthrop", steveRow.findElement(By.tagName("th")).getText());
      List<WebElement> steveData = steveRow.findElements(By.tagName("td"));
      assertEquals("1995-05-17", steveData.get(0).getText());
      assertEquals("MD", steveData.get(1).getText());
      assertEquals("Plastic Surgery", steveData.get(2).getText());
      assertEquals("stevew@gmail.com", steveData.get(3).getText());
      assertEquals("322-555-6123", steveData.get(4).getText());
      assertTrue(steveData.get(5).findElement(By.className("btn")).isDisplayed());
      assertTrue(steveData.get(6).findElement(By.className("btn")).isDisplayed());
      
      WebElement sallyRow = tableRows.get(1);
      assertEquals("Sally Smith", sallyRow.findElement(By.tagName("th")).getText());
      List<WebElement> sallyData = sallyRow.findElements(By.tagName("td"));
      assertEquals("1978-01-06", sallyData.get(0).getText());
      assertEquals("MD/PhD", sallyData.get(1).getText());
      assertEquals("Cardiology", sallyData.get(2).getText());
      assertEquals("ssmith@gmail.com", sallyData.get(3).getText());
      assertEquals("567-555-4673", sallyData.get(4).getText());
      assertTrue(sallyData.get(5).findElement(By.className("btn")).isDisplayed());
      assertTrue(sallyData.get(6).findElement(By.className("btn")).isDisplayed());
    }
    
    // Verifies that edit button redirects user accordingly
    @Test
    public void testEditButton() {
      List<WebElement> editButtons = driver.findElements(By.className("btn-primary"));
      WebElement editButton = editButtons.get(0); // Work with the first one (Steve)
      editButton.click();
      assertTrue(driver.getCurrentUrl().contains("http://localhost:8080/Doctor_Patient_Portal_war/admin/edit_doctor.jsp"));
    }
    
    // Verifies that delete button removes doctor row correctly
    @Test
    public void testDeleteButton() {
      List<WebElement> deleteButtons = driver.findElements(By.className("btn-danger"));
      WebElement deleteButton = deleteButtons.get(0); // Work with the first one (Steve)
      deleteButton.click();
  
      WebElement message = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("text-success")));
      assertTrue(message.isDisplayed());
      
      assertThrows(org.openqa.selenium.NoSuchElementException.class, () -> {
        driver.findElement(By.xpath("//*[contains(text(),'Steve')]"));
      });
  
      WebElement row = driver.findElement(By.tagName("tbody")).findElement(By.tagName("tr"));
      assertEquals("Sally Smith", row.findElement(By.tagName("th")).getText());
    }
  }
}