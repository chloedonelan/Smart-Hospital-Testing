package com.hms.entity;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

public class UserTest {

    private User user;

    @BeforeEach
    public void setUp() {
        user = new User();
    }

    @Test
    public void testDefaultId() {
        assertEquals(0, user.getId(), "default id should be 0");
    }

    @Test
    public void testDefaultFullName() {
        assertNull(user.getFullName(), "default fullName should be null");
    }

    @Test
    public void testDefaultEmail() {
        assertNull(user.getEmail(), "default email should be null");
    }

    @Test
    public void testDefaultPassword() {
        assertNull(user.getPassword(), "default password should be null");
    }

    @Test
    public void testSetAndGetIdPositive() {
        user.setId(123);
        assertEquals(123, user.getId());
    }

    @Test
    public void testSetAndGetIdBounds() {
        user.setId(Integer.MAX_VALUE);
        assertEquals(Integer.MAX_VALUE, user.getId());
        user.setId(Integer.MIN_VALUE);
        assertEquals(Integer.MIN_VALUE, user.getId());
    }

    @Test
    public void testSetAndGetFullNameNormal() {
        user.setFullName("Alice Smith");
        assertEquals("Alice Smith", user.getFullName());
    }

    @Test
    public void testSetFullNameNull() {
        user.setFullName("nullname");
        user.setFullName(null);
        assertNull(user.getFullName());
    }

    @Test
    public void testSetFullNameEmpty() {
        user.setFullName("");
        assertEquals("", user.getFullName());
    }

    @Test
    public void testSetFullNameSpecialChars() {
        String special = "!specname";
        user.setFullName(special);
        assertEquals(special, user.getFullName());
    }

    @Test
    public void testSetFullNameLong() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 500; i++) sb.append('u');
        String longName = sb.toString();
        user.setFullName(longName);
        assertEquals(500, user.getFullName().length());
    }

    @Test
    public void testSetAndGetEmailNormal() {
        user.setEmail("user@gmail.com");
        assertEquals("user@gmail.com", user.getEmail());
    }

    @Test
    public void testSetEmailNull() {
        user.setEmail("user@gmail.com");
        user.setEmail(null);
        assertNull(user.getEmail());
    }

    @Test
    public void testSetEmailEmpty() {
        user.setEmail("");
        assertEquals("", user.getEmail());
    }

    @Test
    public void testSetEmailLong() {
        StringBuilder sb = new StringBuilder();
        sb.append("user@");
        for (int i = 0; i < 200; i++) sb.append('e');
        sb.append(".com");
        String longEmail = sb.toString();
        user.setEmail(longEmail);
        assertTrue(user.getEmail().length() > 200);
    }

    @Test
    public void testSetAndGetPasswordNormal() {
        user.setPassword("normalpass");
        assertEquals("normalpass", user.getPassword());
    }

    @Test
    public void testSetPasswordNull() {
        user.setPassword("nullpass");
        user.setPassword(null);
        assertNull(user.getPassword());
    }

    @Test
    public void testSetPasswordEmpty() {
        user.setPassword("");
        assertEquals("", user.getPassword());
    }

    @Test
    public void testSetPasswordLong() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 100; i++) sb.append('p');
        String longPass = sb.toString();
        user.setPassword(longPass);
        assertEquals(100, user.getPassword().length());
    }

    @Test
    public void testParameterizedConstructorWithId() {
        User u = new User(5, "Bob", "bob@gmail.com", "bobpw");
        assertEquals(5, u.getId());
        assertEquals("Bob", u.getFullName());
        assertEquals("bob@gmail.com", u.getEmail());
        assertEquals("bobpw", u.getPassword());
    }

    @Test
    public void testParameterizedConstructorWithoutId() {
        User u = new User("Carol", "carol@gmail.com", "carolpw");
        assertEquals(0, u.getId(), "id should default to 0");
        assertEquals("Carol", u.getFullName());
        assertEquals("carol@gmail.com", u.getEmail());
        assertEquals("carolpw", u.getPassword());
    }

    @Test
    public void testToStringContainsFields() {
        User u = new User(9, "Dan", "dan@gmail.com", "danpw");
        String s = u.toString();
        assertTrue(s.contains("id=9"));
        assertTrue(s.contains("fullName=Dan"));
        assertTrue(s.contains("email=dan@gmail.com"));
        assertTrue(s.contains("password=danpw"));
    }

    @Test
    public void testToStringEmptyFields() {
        user = new User();
        String s = user.toString();
        assertTrue(s.contains("id=0"));
        assertTrue(s.contains("fullName=null"));
    }
}