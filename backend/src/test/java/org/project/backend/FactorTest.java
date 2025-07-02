package org.project.backend.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class FactorTest {

    private Factor factor;

    @BeforeEach
    void setUp() {
        factor = new Factor();
        factor.setFactorId(1L);
        factor.setFactorCode("ALPHA");
        factor.setFactorName("阿尔法因子");
        factor.setSourceType("PYTHON");
    }

    @Test
    @DisplayName("Test Factor getter/setter")
    void testFactorField() {
        assertEquals(1L, factor.getFactorId());
        assertEquals("ALPHA", factor.getFactorCode());
        assertEquals("阿尔法因子", factor.getFactorName());
        assertEquals("PYTHON", factor.getSourceType());
    }

    @Test
    @DisplayName("Test Factor equals works")
    void testFactorEqual() {
        Factor another = new Factor();
        another.setFactorId(1L);
        another.setFactorCode("ALPHA");
        another.setFactorName("阿尔法因子");
        another.setSourceType("PYTHON");
        assertEquals(factor, another);
    }

    @Test
    @DisplayName("Test Factor toString contains info")
    void testToString() {
        String s = factor.toString();
        assertTrue(s.contains("factorId=1"));
        assertTrue(s.contains("factorCode=ALPHA"));
        assertTrue(s.contains("阿尔法因子"));
        assertTrue(s.contains("PYTHON"));
    }
}