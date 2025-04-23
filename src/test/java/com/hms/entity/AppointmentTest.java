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
      assertEquals(0, appt.getUserId());
    }
  
    // test modification of default userId value
    @Test
    public void testModifyUserIdValue() {
      appt.setUserId(300);
      assertEquals(300, appt.getUserId());
    }
  
    // test modification of default userId value (set to MAX_VALUE to stress test)
    @Test
    public void testModifyUserIdValueUpperBound() {
      appt.setUserId(Integer.MAX_VALUE);
      assertEquals(Integer.MAX_VALUE, appt.getUserId());
    }
  
    // test modification of default userId value (set to MINVALUE to stress test)
    @Test
    public void testModifyUserIdValueLowerBound() {
      appt.setUserId(Integer.MIN_VALUE);
      assertEquals(Integer.MIN_VALUE, appt.getUserId());
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
    // Note: does not validate appointmentDate at all. Not exactly a fault, but an improvement to consider.
  
    // test initialization of default appointmentDate value
    // Not exactly a fault, but this should really be initialized to an empty String for best practice
    @Test
    public void testGetDefaultAppointmentDateValue() {
      assertNull(appt.getAppointmentDate());
    }
  
    // test modification of default appointmentDate value
    @Test
    public void testModifyAppointmentDateValue() {
      appt.setAppointmentDate("4-30-2025");
      assertEquals("4-30-2025", appt.getAppointmentDate());
    }
  
    // test modification of default appointmentDate value from not null to null
    // Not exactly a fault, but this probably should not be allowed for best practices
    @Test
    public void testModifyAppointmentDateValueNull() {
      appt.setAppointmentDate("4-30-2025");
      appt.setAppointmentDate(null);
      assertNull(appt.getAppointmentDate());
    }
  
    // test modification of default appointmentDate value to empty String
    @Test
    public void testModifyAppointmentDateValueEmptyString() {
      appt.setAppointmentDate("");
      assertEquals("", appt.getAppointmentDate());
    }
  }
  
  @Nested
  public class EmailTests {
    // Note: does not validate email at all. Not exactly a fault, but an improvement to consider.
  
    // test initialization of default email value
    // Not exactly a fault, but this should really be initialized to an empty String for best practice
    @Test
    public void testGetDefaultEmailValue() {
      assertNull(appt.getEmail());
    }
  
    // test modification of default email value
    @Test
    public void testModifyEmailValue() {
      appt.setEmail("patient@gmail.com");
      assertEquals("patient@gmail.com", appt.getEmail());
    }
  
    // test modification of default email value from not null to null
    // Not exactly a fault, but this probably should not be allowed for best practices
    @Test
    public void testModifyEmailValueNull() {
      appt.setEmail("patient@gmail.com");
      appt.setEmail(null);
      assertNull(appt.getEmail());
    }
  
    // test modification of default email value to empty String
    @Test
    public void testModifyEmailValueEmptyString() {
      appt.setEmail("");
      assertEquals("", appt.getEmail());
    }
  }
  
  @Nested
  public class PhoneTests {
    // Note: does not validate phone number at all. Not exactly a fault, but an improvement to consider.
  
    // test initialization of default phone value
    // Not exactly a fault, but this should really be initialized to an empty String for best practice
    @Test
    public void testGetDefaultPhoneValue() {
      assertNull(appt.getPhone());
    }
  
    // test modification of default phone value
    @Test
    public void testModifyPhoneValue() {
      appt.setPhone("555-555-5555");
      assertEquals("555-555-5555", appt.getPhone());
    }
  
    // test modification of default phone value from not null to null
    // Not exactly a fault, but this probably should not be allowed for best practices
    @Test
    public void testModifyPhoneValueNull() {
      appt.setPhone("555-555-5555");
      appt.setPhone(null);
      assertNull(appt.getPhone());
    }
  
    // test modification of default phone value to empty String
    @Test
    public void testModifyPhoneValueEmptyString() {
      appt.setPhone("");
      assertEquals("", appt.getPhone());
    }
  }
  
  @Nested
  public class DiseasesTests {
    // test initialization of default diseases value
    // Not exactly a fault, but this should really be initialized to an empty String for best practice
    @Test
    public void testGetDefaultDiseasesValue() {
      assertNull(appt.getDiseases());
    }
  
    // test modification of default diseases value
    @Test
    public void testModifyDiseasesValue() {
      appt.setDiseases("Flu");
      assertEquals("Flu", appt.getDiseases());
    }
  
    // test modification of default diseases value from not null to null
    // Not exactly a fault, but this probably should not be allowed for best practices
    @Test
    public void testModifyDiseasesValueNull() {
      appt.setDiseases("Measles");
      appt.setDiseases(null);
      assertNull(appt.getDiseases());
    }
  
    // test modification of default diseases value to empty String
    @Test
    public void testModifyDiseasesValueEmptyString() {
      appt.setDiseases("");
      assertEquals("", appt.getDiseases());
    }
  }
  
  @Nested
  public class DoctorIdTests {
    // test initialization of default doctorId value
    @Test
    public void testGetDefaultDoctorIdValue() {
      assertEquals(0, appt.getDoctorId());
    }
  
    // test modification of default doctorId value
    @Test
    public void testModifyDoctorIdValue() {
      appt.setDoctorId(578);
      assertEquals(578, appt.getDoctorId());
    }
  
    // test modification of default doctorId value (set to MAX_VALUE to stress test)
    @Test
    public void testModifyDoctorIdValueUpperBound() {
      appt.setDoctorId(Integer.MAX_VALUE);
      assertEquals(Integer.MAX_VALUE, appt.getDoctorId());
    }
  
    // test modification of default doctorId value (set to MINVALUE to stress test)
    @Test
    public void testModifyDoctorIdValueLowerBound() {
      appt.setDoctorId(Integer.MIN_VALUE);
      assertEquals(Integer.MIN_VALUE, appt.getDoctorId());
    }
  }
  
  @Nested
  public class AddressTests {
    // test initialization of default address value
    // Not exactly a fault, but this should really be initialized to an empty String for best practice
    @Test
    public void testGetDefaultAddressValue() {
      assertNull(appt.getAddress());
    }
  
    // test modification of default address value
    @Test
    public void testModifyAddressValue() {
      appt.setAddress("9 E. 33rd St.");
      assertEquals("9 E. 33rd St.", appt.getAddress());
    }
  
    // test modification of default address value from not null to null
    // Not exactly a fault, but this probably should not be allowed for best practices
    @Test
    public void testModifyAddressValueNull() {
      appt.setAddress("2700 N. Charles St.");
      appt.setAddress(null);
      assertNull(appt.getAddress());
    }
  
    // test modification of default address value to empty String
    @Test
    public void testModifyAddressValueEmptyString() {
      appt.setAddress("");
      assertEquals("", appt.getAddress());
    }
  }
  
  @Nested
  public class StatusTests {
    // test initialization of default status value
    // Not exactly a fault, but this should really be initialized to an empty String for best practice
    @Test
    public void testGetDefaultStatusValue() {
      assertNull(appt.getStatus());
    }
  
    // test modification of default status value
    @Test
    public void testModifyStatusValue() {
      appt.setStatus("Scheduled");
      assertEquals("Scheduled", appt.getStatus());
    }
  
    // test modification of default status value from not null to null
    // Not exactly a fault, but this probably should not be allowed for best practices
    @Test
    public void testModifyStatusValueNull() {
      appt.setStatus("Confirmed");
      appt.setStatus(null);
      assertNull(appt.getStatus());
    }
  
    // test modification of default status value to empty String
    @Test
    public void testModifyStatusValueEmptyString() {
      appt.setStatus("");
      assertEquals("", appt.getStatus());
    }
  }
}