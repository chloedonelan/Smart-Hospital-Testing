package webapp.admin;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

public class IndexTest {
  WebDriver driver;
  WebDriverWait wait;
  
  @BeforeEach
  public void setup() {
    driver = new ChromeDriver();
    wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    
    // Page redirects if not logged in; need to login in as admin first
    driver.get("http://localhost:8080/Doctor_Patient_Portal_war/admin_login.jsp");
    driver.findElement(By.name("email")).sendKeys("admin@gmail.com");
    driver.findElement(By.name("password")).sendKeys("admin");
    driver.findElement(By.className("btn")).click();
    
    // Wait for login to complete
    wait.until(ExpectedConditions.urlContains("admin/index.jsp"));
  
    driver.get("http://localhost:8080/Doctor_Patient_Portal_war/admin/index.jsp");
  }
  
  @AfterEach
  public void teardown() {
    driver.quit();
  }
  
  @Nested
  public class NavigationBarTests {
    // Verify that page title is correct
    @Test
    public void testPageTitle() {
      assertEquals("Admin page", driver.getTitle());
    }
  
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
  public class DashboardTests {
    // Verify that the dashboard title is displayed
    @Test
    public void testTitle() {
      WebElement title = driver.findElement(By.xpath("//*[contains(text(),'Admin Dashboard')]"));
      assertTrue(title.isDisplayed());
    }
    
    // Verify that the doctor card is displayed
    @Test
    public void testDoctorCard() {
      WebElement card = driver.findElement(By.xpath("//div[contains(@class, 'card')][.//i[contains(@class, 'fa-user-doctor')]]"));
      assertTrue(card.isDisplayed());
      
      WebElement text = card.findElement(By.className("text-center"));
      assertTrue(text.getText().contains("Doctor"));
    }
  
    // Verify that the user card is displayed
    @Test
    public void testUserCard() {
      WebElement card = driver.findElement(By.xpath("//div[contains(@class, 'card')][.//i[contains(@class, 'fa-user-circle')]]"));
      assertTrue(card.isDisplayed());
  
      WebElement text = card.findElement(By.className("text-center"));
      assertTrue(text.getText().contains("User"));
    }
  
    // Verify that the total appointment card is displayed
    @Test
    public void testTotalAppointmentCard() {
      WebElement card = driver.findElement(By.xpath("//div[contains(@class, 'card')][.//i[contains(@class, 'fa-calendar-check')]]"));
      assertTrue(card.isDisplayed());
    
      WebElement text = card.findElement(By.className("text-center"));
      assertTrue(text.getText().contains("Total Appointment"));
    }
  
    // Verify that the specialist card is displayed
    @Test
    public void testSpecialistCard() {
      WebElement card = driver.findElement(By.xpath("//div[contains(@class, 'card')][.//i[contains(@class, 'fa-user-doctor')]][.//p[contains(text(), 'Specialist')]]"));
      assertTrue(card.isDisplayed());
    
      WebElement text = card.findElement(By.className("text-center"));
      assertTrue(text.getText().contains("Specialist"));
    }
    
    // Verify that the specialist modal appears, a name can be entered, it can be submitted, and the modal disappears
    @Test
    public void testSpecialistFormSubmission() {
      WebElement card = driver.findElement(By.xpath("//div[contains(@class, 'card')][.//i[contains(@class, 'fa-user-doctor')]][.//p[contains(text(), 'Specialist')]]"));
      card.click();
      
      WebElement modal = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("modal")));
      assertTrue(modal.isDisplayed());
      
      WebElement header = modal.findElement(By.className("modal-header"));
      assertTrue(header.getText().contains("Add Specialist"));
      
      WebElement body = modal.findElement(By.className("modal-body"));
      body.findElement(By.name("specialistName")).sendKeys("Dr. John Doe");
      body.findElement(By.tagName("button")).submit();
  
      wait.until(ExpectedConditions.invisibilityOfElementLocated(By.className("modal")));
      assertFalse(driver.findElement(By.className("modal")).isDisplayed());
    }
  
    // Verify that the specialist modal appears and can be closed
    @Test
    public void testSpecialistFormClose() {
      WebElement card = driver.findElement(By.xpath("//div[contains(@class, 'card')][.//i[contains(@class, 'fa-user-doctor')]][.//p[contains(text(), 'Specialist')]]"));
      card.click();
    
      WebElement modal = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("modal")));
      assertTrue(modal.isDisplayed());
    
      WebElement header = modal.findElement(By.className("modal-header"));
      assertTrue(header.getText().contains("Add Specialist"));
      
      WebElement footer = modal.findElement(By.className("modal-footer"));
      footer.findElement(By.tagName("button")).click();
      
      wait.until(ExpectedConditions.invisibilityOfElementLocated(By.className("modal")));
      assertFalse(driver.findElement(By.className("modal")).isDisplayed());
    }
  
    // Verify that the specialist modal appears and can be dismissed
    @Test
    public void testSpecialistFormDismiss() {
      WebElement card = driver.findElement(By.xpath("//div[contains(@class, 'card')][.//i[contains(@class, 'fa-user-doctor')]][.//p[contains(text(), 'Specialist')]]"));
      card.click();
    
      WebElement modal = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("modal")));
      assertTrue(modal.isDisplayed());
    
      WebElement header = modal.findElement(By.className("modal-header"));
      assertTrue(header.getText().contains("Add Specialist"));
      header.findElement(By.tagName("button")).click();
    
      wait.until(ExpectedConditions.invisibilityOfElementLocated(By.className("modal")));
      assertFalse(driver.findElement(By.className("modal")).isDisplayed());
    }
  }
}
