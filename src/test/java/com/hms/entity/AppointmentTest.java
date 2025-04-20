package com.hms.entity;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AppointmentTest {
  private Appointment appt;
  
  @BeforeEach
  public void setUp() {
    appt = new Appointment();
  }
  @Nested
  public class DefaultConstructorTests {
    // ensure that the default constructor returns a non-null Appointment object
    @Test
    public void testNotNull() {
      assertNotNull(appt);
    }
  }
  
  @Nested
  public class ParameterizedConstructorOneTests {
    // ensure that the first parameterized constructor returns a non-null Appointment object
    @Test
    public void testNotNull() {
      appt = new Appointment(
          1, 101, "John Doe", "Male", "35", "2023-05-15",
          "john@example.com", "1234567890", "Fever", 201,
          "123 Main St", "Scheduled"
      );
      assertNotNull(appt);
    }
  }
  
  @Nested
  public class ParameterizedConstructorTwoTests {
    // ensure that the second parameterized constructor returns a non-null Appointment object
    @Test
    public void testNotNull() {
      appt = new Appointment(
          101, "John Doe", "Male", "35", "2023-05-15",
          "john@example.com", "1234567890", "Fever", 201,
          "123 Main St", "Scheduled"
      );
      assertNotNull(appt);
    }
  }
  
  @Nested
  public class IdTests {
    // test initialization of default id value
    @Test
    public void testGetDefaultIdValue() {
      assertEquals(0, appt.getId());
    }
  
    // test modification of default id value
    @Test
    public void testModifyIdValue() {
      appt.setId(300);
      assertEquals(300, appt.getId());
    }
  
    // test modification of default id value (set to MAX_VALUE to stress test)
    @Test
    public void testModifyIdValueUpperBound() {
      appt.setId(Integer.MAX_VALUE);
      assertEquals(Integer.MAX_VALUE, appt.getId());
    }
  
    // test modification of default id value (set to MINVALUE to stress test)
    @Test
    public void testModifyIdValueLowerBound() {
      appt.setId(Integer.MIN_VALUE);
      assertEquals(Integer.MIN_VALUE, appt.getId());
    }
  }
  
  @Nested
  public class UserIdTests {
    // test initialization of default userId value
    @Test
    public void testGetDefaultUserIdValue() {
      assertEquals(0, appt.getId());
    }
  
    // test modification of default userId value
    @Test
    public void testModifyUserIdValue() {
      appt.setId(300);
      assertEquals(300, appt.getId());
    }
  
    // test modification of default userId value (set to MAX_VALUE to stress test)
    @Test
    public void testModifyUserIdValueUpperBound() {
      appt.setId(Integer.MAX_VALUE);
      assertEquals(Integer.MAX_VALUE, appt.getId());
    }
  
    // test modification of default userId value (set to MINVALUE to stress test)
    @Test
    public void testModifyUserIdValueLowerBound() {
      appt.setId(Integer.MIN_VALUE);
      assertEquals(Integer.MIN_VALUE, appt.getId());
    }
  }
  
  @Nested
  public class FullNameTests {
    // test initialization of default fullName value
    // Not exactly a fault, but this should really be initialized to an empty String for best practice
    @Test
    public void testGetDefaultFullNameValue() {
      assertNull(appt.getFullName());
    }
  
    // test modification of default fullName value
    @Test
    public void testModifyFullNameValue() {
      appt.setFullName("John Doe");
      assertEquals("John Doe", appt.getFullName());
    }
    
    // test modification of default fullName value from not null to null
    // Not exactly a fault, but this probably should not be allowed for best practices
    @Test
    public void testModifyFullNameValueNull() {
      appt.setFullName("Placeholder Name");
      appt.setFullName(null);
      assertNull(appt.getFullName());
    }
  
    // test modification of default fullName value to empty String
    @Test
    public void testModifyFullNameValueEmptyString() {
      appt.setFullName("");
      assertEquals("", appt.getFullName());
    }
  }
  
  @Nested
  public class GenderTests {
    // Note: does not validate value of gender at all. Not exactly a fault, but an improvement to consider.
    
    // test initialization of default gender value
    // Not exactly a fault, but this should really be initialized to an empty String for best practice
    @Test
    public void testGetDefaultGenderValue() {
      assertNull(appt.getGender());
    }
  
    // test modification of default gender value
    @Test
    public void testModifyGenderValue() {
      appt.setGender("Male");
      assertEquals("Male", appt.getGender());
    }
  
    // test modification of default gender value from not null to null
    // Not exactly a fault, but this probably should not be allowed for best practices
    @Test
    public void testModifyGenderValueNull() {
      appt.setGender("Female");
      appt.setGender(null);
      assertNull(appt.getGender());
    }
  
    // test modification of default gender value to empty String
    @Test
    public void testModifyGenderValueEmptyString() {
      appt.setGender("");
      assertEquals("", appt.getGender());
    }
  }
  
  @Nested
  public class AgeTests {
    // Note: does not validate value of age at all. Not exactly a fault, but an improvement to consider.
    
    // test initialization of default age value
    // Not exactly a fault, but this should really be initialized to an empty String for best practice
    @Test
    public void testGetDefaultAgeValue() {
      assertNull(appt.getAge());
    }
  
    // test modification of default age value
    @Test
    public void testModifyAgeValue() {
      appt.setAge("30");
      assertEquals("30", appt.getAge());
    }
  
    // test modification of default age value from not null to null
    // Not exactly a fault, but this probably should not be allowed for best practices
    @Test
    public void testModifyAgeValueNull() {
      appt.setAge("28");
      appt.setAge(null);
      assertNull(appt.getAge());
    }
  
    // test modification of default age value to empty String
    @Test
    public void testModifyAgeValueEmptyString() {
      appt.setAge("");
      assertEquals("", appt.getAge());
    }
  }
  
  @Nested
  public class AppointmentDateTests {
    // TODO
  }
  
  @Nested
  public class EmailTests {
    // TODO
  }
  
  @Nested
  public class PhoneTests {
    // TODO
  }
  
  @Nested
  public class DiseasesTests {
    // TODO
  }
  
  @Nested
  public class DoctorIdTests {
    // TODO
  }
  
  @Nested
  public class AddressTests {
    // TODO
  }
  
  @Nested
  public class StatusTests {
    // TODO
  }
}