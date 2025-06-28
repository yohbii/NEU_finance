package org.project.backend.service;

import org.project.backend.DTO.HttpResponse;
import org.project.backend.entity.User;
import org.project.backend.mapper.UserMapper;
import org.project.backend.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class LoginService {
    private final JwtUtil jwtUtil;
    private final UserMapper userMapper;

    @Autowired
    public LoginService(JwtUtil jwtUtil, UserMapper userMapper) {
        this.jwtUtil = jwtUtil;
        this.userMapper = userMapper;
    }

    public HttpResponse<Map<String, String>> login(User user) {
        User user_ = userMapper.findByUsernameAndPassword(user.getUsername(), user.getPassword());

        if (null == user_) {
            return new HttpResponse<>(-1, null, "用户名或密码错误", "用户名或密码错误");
        }

        String accessToken = jwtUtil.generateToken(user.getUsername());
        Map<String, String> map = new HashMap<>();
        map.put("accessToken", accessToken);
        return new HttpResponse<>(0, map, "ok", null);
    }

    public HttpResponse<User> getUserInfo(String authHeader) {
        String token = authHeader.startsWith("Bearer ")
                ? authHeader.substring(7).trim()
                : authHeader.trim();
        String username;
        try {
            username = jwtUtil.getUsernameFromToken(token);
        } catch (Exception e) {
            return new HttpResponse<>(-1, null, "accessToken不存在", "accessToken不存在");
        }

        User user = userMapper.findByUsername(username);

        return new HttpResponse<>(0, user, "ok", "ok");
    }


}
