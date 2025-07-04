package com.advisor.controller;

import com.advisor.common.Result;
import com.advisor.entity.User;
import com.advisor.service.AuthService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.util.HashMap;
import java.util.Map;

/**
 * 认证控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody @Valid LoginRequest request) {
        try {
            log.info("用户登录请求: username={}", request.getUsername());
            
            String token = authService.login(request.getUsername(), request.getPassword());
            log.info("JWT Token生成成功");
            
            User user = authService.getUserByUsername(request.getUsername());
            log.info("获取用户信息: user={}", user);
            
            if (user == null) {
                log.error("用户信息为空");
                return Result.error("用户信息不存在");
            }
            
            Map<String, Object> data = new HashMap<>();
            data.put("token", token);
            data.put("user", Map.of(
                "id", user.getId() != null ? user.getId() : 0L,
                "username", user.getUsername() != null ? user.getUsername() : "",
                "email", user.getEmail() != null ? user.getEmail() : "",
                "realName", user.getRealName() != null ? user.getRealName() : ""
            ));
            
            log.info("登录成功，返回数据: {}", data);
            return Result.success("登录成功", data);
        } catch (Exception e) {
            log.error("登录失败: ", e);
            return Result.error(e.getMessage() != null ? e.getMessage() : "登录失败");
        }
    }

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public Result<Void> register(@RequestBody @Valid RegisterRequest request) {
        try {
            authService.register(request.getUsername(), request.getPassword(), 
                               request.getEmail(), request.getRealName());
            return Result.success("注册成功");
        } catch (Exception e) {
            log.error("注册失败: {}", e.getMessage());
            return Result.error(e.getMessage());
        }
    }

    /**
     * 用户登出
     */
    @PostMapping("/logout")
    public Result<Void> logout() {
        // 由于使用了JWT，客户端只需要删除本地的token即可
        // 这里可以添加一些服务端的逻辑，比如将token加入黑名单等
        return Result.success("登出成功");
    }

    /**
     * 获取当前用户信息
     */
    @GetMapping("/me")
    public Result<Map<String, Object>> getCurrentUser(@RequestHeader("Authorization") String authorization) {
        try {
            String token = authorization.replace("Bearer ", "");
            String username = authService.getUsernameFromToken(token);
            User user = authService.getUserByUsername(username);
            
            Map<String, Object> userInfo = Map.of(
                "id", user.getId(),
                "username", user.getUsername(),
                "email", user.getEmail(),
                "realName", user.getRealName()
            );
            
            return Result.success(userInfo);
        } catch (Exception e) {
            log.error("获取用户信息失败: {}", e.getMessage());
            return Result.unauthorized();
        }
    }

    /**
     * 登录请求类
     */
    @Data
    public static class LoginRequest {
        @NotBlank(message = "用户名不能为空")
        private String username;
        
        @NotBlank(message = "密码不能为空")
        private String password;
    }

    /**
     * 注册请求类
     */
    @Data
    public static class RegisterRequest {
        @NotBlank(message = "用户名不能为空")
        private String username;
        
        @NotBlank(message = "密码不能为空")
        private String password;
        
        private String email;
        private String realName;
    }
} 