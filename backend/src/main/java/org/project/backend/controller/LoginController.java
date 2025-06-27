package org.project.backend.controller;


import org.project.backend.DTO.HttpResponse;
import org.project.backend.entity.User;
import org.project.backend.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@Controller
public class LoginController {
    private final LoginService loginService;

    @Autowired
    public LoginController(LoginService loginService) {
        this.loginService = loginService;
    }

    @RequestMapping("/auth/login")
    public HttpResponse<Map<String, String>> login(@RequestBody User user) {
        return loginService.login(user);
    }

    @RequestMapping("/user/info")
    public HttpResponse<User> getUserInfo(@RequestHeader("authorization") String authHeader) {
        return loginService.getUserInfo(authHeader);
    }
}
