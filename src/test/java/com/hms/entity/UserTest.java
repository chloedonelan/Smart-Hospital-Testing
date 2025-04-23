package com.hms.entity;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class UserTest {

    @Test
    public void testDefaultConstructor() {
        User user = new User();

        assertEquals(0, user.getId(), "Default id should be 0");
        assertNull(user.getFullName(), "Default fullName should be null");
        assertNull(user.getEmail(), "Default email should be null");
        assertNull(user.getPassword(), "Default password should be null");

        user.setId(1);
        user.setFullName("default constructor user");
        user.setEmail("default@gmail.com");
        user.setPassword("defaultpass");

        assertEquals(1, user.getId());
        assertEquals("default constructor user", user.getFullName());
        assertEquals("default@gmail.com", user.getEmail());
        assertEquals("defaultpass", user.getPassword());
    }

    @Test
    public void testParameterizedConstructorWithId() {
        User user = new User(2, "parameter constructor with id user", "parameterwithid@gmail.com", "parameterwithidpass");

        assertEquals(2, user.getId());
        assertEquals("parameter constructor with id user", user.getFullName());
        assertEquals("parameterwithid@gmail.com", user.getEmail());
        assertEquals("parameterwithidpass", user.getPassword());
    }

    @Test
    public void testParameterizedConstructorWithoutId() {
        User user = new User("parameter constructor without id user", "parameternoid@gmail.com", "parameternoidpass");

        assertEquals(0, user.getId());
        assertEquals("parameter constructor without id user", user.getFullName());
        assertEquals("parameternoid@gmail.com", user.getEmail());
        assertEquals("parameternoidpass", user.getPassword());
    }

    @Test
    public void testToString() {
        User user = new User(3, "to string user", "tostring@gmail.com", "tostringpass");
        String toStr = user.toString();

        assertTrue(toStr.contains("id=3"), "toString should contain id=3");
        assertTrue(toStr.contains("fullName=to string user"), "toString should contain fullName=to string user");
        assertTrue(toStr.contains("email=tostring@gmail.com"), "toString should contain email=dave@example.com");
        assertTrue(toStr.contains("password=tostringpass"), "toString should contain password=tostringpass");
    }
}