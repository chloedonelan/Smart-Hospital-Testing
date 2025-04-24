package com.hms.entity;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

public class SpecialistTest {

    private Specialist specialist;

    @BeforeEach
    public void setUp() {
        specialist = new Specialist();
    }

    @Test
    public void testDefaultConstructorIdIsZero() {
        assertEquals(0, specialist.getId(), "default id should be 0");
    }

    @Test
    public void testDefaultConstructorNameIsNull() {
        assertNull(specialist.getSpecialistName(), "default name should be null");
    }

    @Test
    public void testParaConstructorSetsIdAndName() {
        Specialist sp = new Specialist(10, "spec");
        assertEquals(10, sp.getId());
        assertEquals("spec", sp.getSpecialistName());
    }

    @Test
    public void testParaConstructorWithNullName() {
        Specialist sp = new Specialist(5, null);
        assertEquals(5, sp.getId());
        assertNull(sp.getSpecialistName(), "name not null when passed null");
    }

    @Test
    public void testParaConstructorWithEmptyName() {
        Specialist sp = new Specialist(7, "");
        assertEquals(7, sp.getId());
        assertEquals("", sp.getSpecialistName(), "name not empty string when passed empty str");
    }

    @Test
    public void testSetAndGetPositiveId() {
        specialist.setId(42);
        assertEquals(42, specialist.getId());
    }

    // potential fault - SQL id's should not be negative if AUTO_INCREMENT
    @Test
    public void testSetAndGetNegativeId() {
        specialist.setId(-1);
        assertEquals(-1, specialist.getId(), "negative ids allowed?");
    }

    @Test
    public void testSetIdUpperBound() {
        specialist.setId(Integer.MAX_VALUE);
        assertEquals(Integer.MAX_VALUE, specialist.getId());
    }

    // potential fault - SQL id's should not be negative
    @Test
    public void testSetIdLowerBound() {
        specialist.setId(Integer.MIN_VALUE);
        assertEquals(Integer.MIN_VALUE, specialist.getId());
    }

    @Test
    public void testRegularName() {
        specialist.setSpecialistName("regularname");
        assertEquals("regularname", specialist.getSpecialistName());
    }

    @Test
    public void testNullName() {
        specialist.setSpecialistName("nullname");
        specialist.setSpecialistName(null);
        assertNull(specialist.getSpecialistName());
    }

    @Test
    public void testEmptyStringName() {
        specialist.setSpecialistName("");
        assertEquals("", specialist.getSpecialistName());
    }

    @Test
    public void testSpecialCharacterName() {
        String special = "!specname";
        specialist.setSpecialistName(special);
        assertEquals(special, specialist.getSpecialistName());
    }

    @Test
    public void testLongStringName() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 1000; i++) sb.append('x');
        String longName = sb.toString();
        specialist.setSpecialistName(longName);
        assertEquals(1000, specialist.getSpecialistName().length());
    }
}