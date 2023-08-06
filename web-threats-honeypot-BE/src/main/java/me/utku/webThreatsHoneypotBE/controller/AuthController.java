package me.utku.webThreatsHoneypotBE.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import me.utku.webThreatsHoneypotBE.dto.AuthResponse;
import me.utku.webThreatsHoneypotBE.dto.LoginRequest;
import me.utku.webThreatsHoneypotBE.service.BruteForceRequestService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final BruteForceRequestService bruteForceRequestService;

    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest reqBody, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse){
        String username = reqBody.getUsername();
        String password = reqBody.getPassword();
        return bruteForceRequestService.handleBruteForceLoginAttempt(username,password,httpServletRequest,httpServletResponse);
    }
}