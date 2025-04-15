package com.hms.entity;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class SpecialistTest {

    @Test
    public void testDefaultConstructor() {
        Specialist specialist = new Specialist();

        assertEquals(0, specialist.getId());
        assertNull(specialist.getSpecialistName());

        specialist.setId(5);
        specialist.setSpecialistName("default specailist");

        assertEquals(5, specialist.getId());
        assertEquals("default specailist", specialist.getSpecialistName());
    }

    @Test
    public void testConstructorWithParameters() {
        Specialist specialist = new Specialist(10, "parameter specialist");

        assertEquals(10, specialist.getId());
        assertEquals("parameter specialist", specialist.getSpecialistName());
    }
}