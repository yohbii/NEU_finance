package com.advisor.controller;
import com.advisor.common.Result;
import com.advisor.entity.User;
import com.advisor.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
// 导入 MyBatis 自动配置，以便在需要时排除它
import org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import java.util.HashMap;
import java.util.Map;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// 排除与数据库相关的自动配置，并额外排除MyBatis自动配置和Mapper层
@WebMvcTest(
        controllers = AuthController.class,
        excludeAutoConfiguration = {
                DataSourceAutoConfiguration.class,
                DataSourceTransactionManagerAutoConfiguration.class,
                HibernateJpaAutoConfiguration.class,
                MybatisAutoConfiguration.class // 明确排除MyBatis的自动配置
        },
        // 添加ComponentScan过滤器，排除所有Mapper接口
        // 假设你的所有Mapper都在com.advisor.mapper包下
        // 如果你的Mapper不在这个包下，请修改 pattern
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.REGEX,
                pattern = "com\\.advisor\\.mapper\\..*Mapper" // 排除所有以 Mapper 结尾的类，例如 FactorAnalysisResultMapper
        )
)
class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("POST /api/auth/login - 登录成功")
    void login_Success() throws Exception {
        String username = "testuser";
        String password = "password123";
        String token = "mock_jwt_token";

        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername(username);
        mockUser.setEmail("test@example.com");
        mockUser.setRealName("测试用户");

        when(authService.login(username, password)).thenReturn(token);
        when(authService.getUserByUsername(username)).thenReturn(mockUser);

        // 使用 AuthController 内部定义的 LoginRequest 类
        AuthController.LoginRequest loginRequest = new AuthController.LoginRequest();
        loginRequest.setUsername(username);
        loginRequest.setPassword(password);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("登录成功"))
                .andExpect(jsonPath("$.data.token").value(token))
                .andExpect(jsonPath("$.data.user.id").value(1L))
                .andExpect(jsonPath("$.data.user.username").value(username))
                .andExpect(jsonPath("$.data.user.email").value("test@example.com"))
                .andExpect(jsonPath("$.data.user.realName").value("测试用户"));

        verify(authService, times(1)).login(username, password);
        verify(authService, times(1)).getUserByUsername(username);
    }

    @Test
    @DisplayName("POST /api/auth/login - 登录失败，AuthService抛出异常")
    void login_Failure_AuthServiceThrowsException() throws Exception {
        String username = "wronguser";
        String password = "wrongpassword";
        String errorMessage = "用户名或密码错误";

        when(authService.login(username, password)).thenThrow(new RuntimeException(errorMessage));

        AuthController.LoginRequest loginRequest = new AuthController.LoginRequest();
        loginRequest.setUsername(username);
        loginRequest.setPassword(password);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value(errorMessage));

        verify(authService, times(1)).login(username, password);
        verify(authService, never()).getUserByUsername(anyString());
    }

    @Test
    @DisplayName("POST /api/auth/login - 登录成功但用户信息为空")
    void login_Success_UserIsNull() throws Exception {
        String username = "testuser";
        String password = "password123";
        String token = "mock_jwt_token";

        when(authService.login(username, password)).thenReturn(token);
        when(authService.getUserByUsername(username)).thenReturn(null);

        AuthController.LoginRequest loginRequest = new AuthController.LoginRequest();
        loginRequest.setUsername(username);
        loginRequest.setPassword(password);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value("用户信息不存在"));

        verify(authService, times(1)).login(username, password);
        verify(authService, times(1)).getUserByUsername(username);
    }

    @Test
    @DisplayName("POST /api/auth/login - 请求参数校验失败 (用户名不能为空)")
    void login_ValidationFailure_UsernameBlank() throws Exception {
        AuthController.LoginRequest loginRequest = new AuthController.LoginRequest();
        loginRequest.setUsername("");
        loginRequest.setPassword("password123");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").exists()); // 假设验证失败会返回errors字段

        verifyNoInteractions(authService);
    }

    @Test
    @DisplayName("POST /api/auth/register - 注册成功")
    void register_Success() throws Exception {
        AuthController.RegisterRequest registerRequest = new AuthController.RegisterRequest();
        registerRequest.setUsername("newuser");
        registerRequest.setPassword("newpass");
        registerRequest.setEmail("new@example.com");
        registerRequest.setRealName("新用户");

        doNothing().when(authService).register(anyString(), anyString(), anyString(), anyString());

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("注册成功"));

        verify(authService, times(1)).register(
                eq("newuser"), eq("newpass"), eq("new@example.com"), eq("新用户"));
    }

    @Test
    @DisplayName("POST /api/auth/register - 注册失败，AuthService抛出异常")
    void register_Failure_AuthServiceThrowsException() throws Exception {
        String username = "existuser";
        String password = "pass";
        String errorMessage = "用户名已存在";

        doThrow(new RuntimeException(errorMessage)).when(authService)
                .register(anyString(), anyString(), anyString(), anyString());

        AuthController.RegisterRequest registerRequest = new AuthController.RegisterRequest();
        registerRequest.setUsername(username);
        registerRequest.setPassword(password);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value(errorMessage));

        verify(authService, times(1)).register(anyString(), anyString(), anyString(), anyString());
    }

    @Test
    @DisplayName("POST /api/auth/register - 请求参数校验失败 (密码不能为空)")
    void register_ValidationFailure_PasswordBlank() throws Exception {
        AuthController.RegisterRequest registerRequest = new AuthController.RegisterRequest();
        registerRequest.setUsername("user");
        registerRequest.setPassword("");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").exists()); // 假设验证失败会返回errors字段

        verifyNoInteractions(authService);
    }

    @Test
    @DisplayName("POST /api/auth/logout - 登出成功")
    void logout_Success() throws Exception {
        mockMvc.perform(post("/api/auth/logout"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("登出成功"));

        verifyNoInteractions(authService);
    }

    @Test
    @DisplayName("GET /api/auth/me - 成功获取当前用户信息")
    void getCurrentUser_Success() throws Exception {
        String token = "mock_jwt_token_abc";
        String username = "currentuser";

        User mockUser = new User();
        mockUser.setId(10L);
        mockUser.setUsername(username);
        mockUser.setEmail("current@example.com");
        mockUser.setRealName("当前用户");

        when(authService.getUsernameFromToken(token)).thenReturn(username);
        when(authService.getUserByUsername(username)).thenReturn(mockUser);

        mockMvc.perform(get("/api/auth/me")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("操作成功"))
                .andExpect(jsonPath("$.data.id").value(10L))
                .andExpect(jsonPath("$.data.username").value(username))
                .andExpect(jsonPath("$.data.email").value("current@example.com"))
                .andExpect(jsonPath("$.data.realName").value("当前用户"));

        verify(authService, times(1)).getUsernameFromToken(token);
        verify(authService, times(1)).getUserByUsername(username);
    }

    @Test
    @DisplayName("GET /api/auth/me - Token无效或获取用户名失败")
    void getCurrentUser_InvalidToken() throws Exception {
        String invalidToken = "invalid_token";

        when(authService.getUsernameFromToken(invalidToken)).thenThrow(new RuntimeException("Token解析失败"));

        mockMvc.perform(get("/api/auth/me")
                        .header("Authorization", "Bearer " + invalidToken))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(401))
                .andExpect(jsonPath("$.message").value("未授权访问"));

        verify(authService, times(1)).getUsernameFromToken(invalidToken);
        verify(authService, never()).getUserByUsername(anyString());
    }

    @Test
    @DisplayName("GET /api/auth/me - 用户名有效但用户不存在")
    void getCurrentUser_UserNotFound() throws Exception {
        String token = "valid_token";
        String username = "nonexistentuser";

        when(authService.getUsernameFromToken(token)).thenReturn(username);
        when(authService.getUserByUsername(username)).thenReturn(null);

        mockMvc.perform(get("/api/auth/me")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(401))
                .andExpect(jsonPath("$.message").value("未授权访问"));

        verify(authService, times(1)).getUsernameFromToken(token);
        verify(authService, times(1)).getUserByUsername(username);
    }

    @Test
    @DisplayName("GET /api/auth/me - Authorization header缺失")
    void getCurrentUser_MissingAuthorizationHeader() throws Exception {
        mockMvc.perform(get("/api/auth/me"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(authService);
    }
}