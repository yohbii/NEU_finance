package org.project.backend;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class RegistTest {

    private RegistTest registTest;

    @BeforeEach
    void setUp() {
        registTest = new RegistTest();
    }

    /**
     * 假设将来注册要判断用户名是否为空，这里演示下行为
     */
    public boolean isValidUsername(String username) {
        return username != null && !username.trim().isEmpty();
    }

    /**
     * 假设注册时要校验密码长度
     */
    public boolean isValidPassword(String password) {
        return password != null && password.length() >= 6;
    }

    /**
     * 假定注册会抛异常
     */
    public void doRegist(String username, String password) {
        if (!isValidUsername(username)) {
            throw new IllegalArgumentException("用户名无效");
        }
        if (!isValidPassword(password)) {
            throw new IllegalArgumentException("密码太短");
        }
        // 省略注册过程
    }

    @Test
    @DisplayName("Test valid username")
    void testValidUsername() {
        assertTrue(registTest.isValidUsername("zhangsan"));
        assertFalse(registTest.isValidUsername(""));
        assertFalse(registTest.isValidUsername(null));
        assertFalse(registTest.isValidUsername("   "));
    }

    @Test
    @DisplayName("Test valid password")
    void testValidPassword() {
        assertTrue(registTest.isValidPassword("abcdef"));
        assertFalse(registTest.isValidPassword("123"));
        assertFalse(registTest.isValidPassword(null));
    }

    @Test
    @DisplayName("Test doRegist success")
    void testDoRegistSuccess() {
        assertDoesNotThrow(() -> registTest.doRegist("lisi", "654321"));
    }

    @Test
    @DisplayName("Test doRegist fail for invalid username")
    void testDoRegistFailUsername() {
        Exception e = assertThrows(IllegalArgumentException.class, () -> registTest.doRegist("", "abcdef"));
        assertEquals("用户名无效", e.getMessage());
    }

    @Test
    @DisplayName("Test doRegist fail for short password")
    void testDoRegistFailPassword() {
        Exception e = assertThrows(IllegalArgumentException.class, () -> registTest.doRegist("wangwu", "123"));
        assertEquals("密码太短", e.getMessage());
    }
}