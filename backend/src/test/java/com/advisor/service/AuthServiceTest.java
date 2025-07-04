package com.advisor.service;

import com.advisor.entity.User;
import com.advisor.mapper.UserMapper;
import com.advisor.utils.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

// 启用 Mockito 注解
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    // 模拟依赖项
    @Mock
    private UserMapper userMapper;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtUtil jwtUtil;

    // 注入被测试对象，Mockito 会自动注入上面 Mock 的依赖
    @InjectMocks
    private AuthService authService;

    // 在每个测试方法运行前执行，用于初始化 Mockito 模拟对象
    // @BeforeEach 已经由 @ExtendWith(MockitoExtension.class) 和 @Mock, @InjectMocks 自动处理了
    @BeforeEach
    void setUp() {
        // 可以在这里重置 Mockito 的行为，或者设置一些通用的模拟行为
        // 例如：Mockito.reset(userMapper, passwordEncoder, jwtUtil);
    }

    @Test
    @DisplayName("login_成功登录")
    void login_Success() {
        // 1. 准备数据
        String username = "testuser";
        String password = "rawpassword";
        String encodedPassword = "encodedpassword";
        String expectedToken = "mocked_jwt_token";

        User user = new User();
        user.setUsername(username);
        user.setPassword(encodedPassword);
        user.setStatus(1); // 启用状态

        // 2. 模拟依赖行为
        when(userMapper.findByUsername(username)).thenReturn(user);
        when(passwordEncoder.matches(password, encodedPassword)).thenReturn(true);
        when(jwtUtil.generateToken(username)).thenReturn(expectedToken);

        // 3. 调用被测试方法
        String actualToken = authService.login(username, password);

        // 4. 验证结果
        assertEquals(expectedToken, actualToken);

        // 5. 验证依赖方法是否被调用
        verify(userMapper, times(1)).findByUsername(username);
        verify(passwordEncoder, times(1)).matches(password, encodedPassword);
        verify(jwtUtil, times(1)).generateToken(username);
        verifyNoMoreInteractions(userMapper, passwordEncoder, jwtUtil); // 确保没有其他不期望的交互
    }

    @Test
    @DisplayName("login_用户名不存在")
    void login_UserNotFound() {
        // 1. 准备数据
        String username = "nonexistent";
        String password = "anypassword";

        // 2. 模拟依赖行为：用户不存在
        when(userMapper.findByUsername(username)).thenReturn(null);

        // 3. 调用被测试方法并验证异常
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                authService.login(username, password)
        );

        // 4. 验证异常信息
        assertEquals("用户名或密码错误", exception.getMessage());

        // 5. 验证依赖方法是否被调用
        verify(userMapper, times(1)).findByUsername(username);
        // 确保没有调用密码验证和token生成
        verifyNoInteractions(passwordEncoder, jwtUtil);
    }

    @Test
    @DisplayName("login_密码不匹配")
    void login_PasswordMismatch() {
        // 1. 准备数据
        String username = "testuser";
        String wrongPassword = "wrongpassword";
        String encodedPassword = "encodedpassword";

        User user = new User();
        user.setUsername(username);
        user.setPassword(encodedPassword);
        user.setStatus(1);

        // 2. 模拟依赖行为：密码不匹配
        when(userMapper.findByUsername(username)).thenReturn(user);
        when(passwordEncoder.matches(wrongPassword, encodedPassword)).thenReturn(false);

        // 3. 调用被测试方法并验证异常
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                authService.login(username, wrongPassword)
        );

        // 4. 验证异常信息
        assertEquals("用户名或密码错误", exception.getMessage());

        // 5. 验证依赖方法是否被调用
        verify(userMapper, times(1)).findByUsername(username);
        verify(passwordEncoder, times(1)).matches(wrongPassword, encodedPassword);
        verifyNoInteractions(jwtUtil); // 确保没有调用token生成
    }

    @Test
    @DisplayName("login_用户被禁用")
    void login_UserDisabled() {
        // 1. 准备数据
        String username = "disableduser";
        String password = "rawpassword";
        String encodedPassword = "encodedpassword";

        User user = new User();
        user.setUsername(username);
        user.setPassword(encodedPassword);
        user.setStatus(0); // 禁用状态

        // 2. 模拟依赖行为
        when(userMapper.findByUsername(username)).thenReturn(user);
        when(passwordEncoder.matches(password, encodedPassword)).thenReturn(true);

        // 3. 调用被测试方法并验证异常
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                authService.login(username, password)
        );

        // 4. 验证异常信息
        assertEquals("用户已被禁用", exception.getMessage());

        // 5. 验证依赖方法是否被调用
        verify(userMapper, times(1)).findByUsername(username);
        verify(passwordEncoder, times(1)).matches(password, encodedPassword);
        verifyNoInteractions(jwtUtil); // 确保没有调用token生成
    }

    @Test
    @DisplayName("register_成功注册")
    void register_Success() {
        // 1. 准备数据
        String username = "newuser";
        String password = "newpassword";
        String email = "newuser@example.com";
        String realName = "New User";
        String encodedPassword = "encoded_newpassword";

        // 2. 模拟依赖行为
        when(userMapper.findByUsername(username)).thenReturn(null); // 用户不存在
        when(passwordEncoder.encode(password)).thenReturn(encodedPassword);
        when(userMapper.insert(any(User.class))).thenReturn(1); // 插入成功

        // 3. 调用被测试方法
        assertDoesNotThrow(() -> authService.register(username, password, email, realName));

        // 4. 验证依赖方法是否被调用
        verify(userMapper, times(1)).findByUsername(username);
        verify(passwordEncoder, times(1)).encode(password);
        verify(userMapper, times(1)).insert(any(User.class)); // 验证插入方法被调用
        verifyNoMoreInteractions(userMapper, passwordEncoder, jwtUtil); // 确保没有其他不期望的交互

        // 进一步验证插入的用户对象属性
        // 使用 ArgumentCaptor 捕获传递给 insert 方法的 User 对象
        // ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        // verify(userMapper).insert(userCaptor.capture());
        // User capturedUser = userCaptor.getValue();
        // assertEquals(username, capturedUser.getUsername());
        // assertEquals(encodedPassword, capturedUser.getPassword());
        // assertEquals(email, capturedUser.getEmail());
        // assertEquals(realName, capturedUser.getRealName());
        // assertEquals(1, capturedUser.getStatus());
    }

    @Test
    @DisplayName("register_用户名已存在")
    void register_UsernameExists() {
        // 1. 准备数据
        String username = "existinguser";
        String password = "password";
        String email = "email@example.com";
        String realName = "Existing User";

        // 2. 模拟依赖行为：用户已存在
        when(userMapper.findByUsername(username)).thenReturn(new User());

        // 3. 调用被测试方法并验证异常
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                authService.register(username, password, email, realName)
        );

        // 4. 验证异常信息
        assertEquals("用户名已存在", exception.getMessage());

        // 5. 验证依赖方法是否被调用
        verify(userMapper, times(1)).findByUsername(username);
        // 确保没有调用密码编码和插入
        verifyNoInteractions(passwordEncoder, jwtUtil);
        verify(userMapper, never()).insert(any(User.class));
    }

    @Test
    @DisplayName("register_注册失败_数据库插入异常")
    void register_DatabaseInsertFailure() {
        // 1. 准备数据
        String username = "failuser";
        String password = "password";
        String email = "fail@example.com";
        String realName = "Fail User";
        String encodedPassword = "encoded_password";

        // 2. 模拟依赖行为：插入失败
        when(userMapper.findByUsername(username)).thenReturn(null);
        when(passwordEncoder.encode(password)).thenReturn(encodedPassword);
        when(userMapper.insert(any(User.class))).thenReturn(0); // 模拟插入0行，表示失败

        // 3. 调用被测试方法并验证异常
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                authService.register(username, password, email, realName)
        );

        // 4. 验证异常信息
        assertEquals("注册失败", exception.getMessage());

        // 5. 验证依赖方法是否被调用
        verify(userMapper, times(1)).findByUsername(username);
        verify(passwordEncoder, times(1)).encode(password);
        verify(userMapper, times(1)).insert(any(User.class));
        verifyNoInteractions(jwtUtil);
    }


    @Test
    @DisplayName("getUserByUsername_用户存在")
    void getUserByUsername_UserExists() {
        // 1. 准备数据
        String username = "existinguser";
        User expectedUser = new User();
        expectedUser.setUsername(username);
        expectedUser.setEmail("test@example.com");

        // 2. 模拟依赖行为
        when(userMapper.findByUsername(username)).thenReturn(expectedUser);

        // 3. 调用被测试方法
        User actualUser = authService.getUserByUsername(username);

        // 4. 验证结果
        assertNotNull(actualUser);
        assertEquals(expectedUser.getUsername(), actualUser.getUsername());
        assertEquals(expectedUser.getEmail(), actualUser.getEmail());

        // 5. 验证依赖方法是否被调用
        verify(userMapper, times(1)).findByUsername(username);
        verifyNoMoreInteractions(userMapper, passwordEncoder, jwtUtil);
    }

    @Test
    @DisplayName("getUserByUsername_用户不存在")
    void getUserByUsername_UserNotFound() {
        // 1. 准备数据
        String username = "nonexistent";

        // 2. 模拟依赖行为
        when(userMapper.findByUsername(username)).thenReturn(null);

        // 3. 调用被测试方法
        User actualUser = authService.getUserByUsername(username);

        // 4. 验证结果
        assertNull(actualUser);

        // 5. 验证依赖方法是否被调用
        verify(userMapper, times(1)).findByUsername(username);
        verifyNoMoreInteractions(userMapper, passwordEncoder, jwtUtil);
    }

    @Test
    @DisplayName("validateToken_有效Token")
    void validateToken_ValidToken() {
        // 1. 准备数据
        String validToken = "valid.jwt.token";

        // 2. 模拟依赖行为
        when(jwtUtil.validateToken(validToken)).thenReturn(true);

        // 3. 调用被测试方法
        boolean isValid = authService.validateToken(validToken);

        // 4. 验证结果
        assertTrue(isValid);

        // 5. 验证依赖方法是否被调用
        verify(jwtUtil, times(1)).validateToken(validToken);
        verifyNoMoreInteractions(userMapper, passwordEncoder, jwtUtil);
    }

    @Test
    @DisplayName("validateToken_无效Token")
    void validateToken_InvalidToken() {
        // 1. 准备数据
        String invalidToken = "invalid.jwt.token";

        // 2. 模拟依赖行为
        when(jwtUtil.validateToken(invalidToken)).thenReturn(false);

        // 3. 调用被测试方法
        boolean isValid = authService.validateToken(invalidToken);

        // 4. 验证结果
        assertFalse(isValid);

        // 5. 验证依赖方法是否被调用
        verify(jwtUtil, times(1)).validateToken(invalidToken);
        verifyNoMoreInteractions(userMapper, passwordEncoder, jwtUtil);
    }

    @Test
    @DisplayName("getUsernameFromToken_成功获取用户名")
    void getUsernameFromToken_Success() {
        // 1. 准备数据
        String token = "some.jwt.token";
        String expectedUsername = "extracteduser";

        // 2. 模拟依赖行为
        when(jwtUtil.getUsernameFromToken(token)).thenReturn(expectedUsername);

        // 3. 调用被测试方法
        String actualUsername = authService.getUsernameFromToken(token);

        // 4. 验证结果
        assertEquals(expectedUsername, actualUsername);

        // 5. 验证依赖方法是否被调用
        verify(jwtUtil, times(1)).getUsernameFromToken(token);
        verifyNoMoreInteractions(userMapper, passwordEncoder, jwtUtil);
    }

    @Test
    @DisplayName("getUsernameFromToken_Token无效或无法解析")
    void getUsernameFromToken_InvalidOrUnparseableToken() {
        // 1. 准备数据
        String invalidToken = "malformed.token";

        // 2. 模拟依赖行为：JwtUtil 可能返回 null 或抛出异常
        when(jwtUtil.getUsernameFromToken(invalidToken)).thenReturn(null); // 或者 doThrow(new SomeJwtException()).when(jwtUtil).getUsernameFromToken(invalidToken);

        // 3. 调用被测试方法
        String actualUsername = authService.getUsernameFromToken(invalidToken);

        // 4. 验证结果
        assertNull(actualUsername); // 或者根据实际业务逻辑期望抛出异常

        // 5. 验证依赖方法是否被调用
        verify(jwtUtil, times(1)).getUsernameFromToken(invalidToken);
        verifyNoMoreInteractions(userMapper, passwordEncoder, jwtUtil);
    }
}