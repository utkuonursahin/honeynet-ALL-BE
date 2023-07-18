package me.utku.bfPtSqlHoneypotBE.controller;

import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import me.utku.bfPtSqlHoneypotBE.dto.AuthResponse;
import me.utku.bfPtSqlHoneypotBE.dto.LoginRequest;
import me.utku.bfPtSqlHoneypotBE.service.BruteForceRequestService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final BruteForceRequestService bruteForceRequestService;

    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest reqBody, ServletRequest servletRequest, HttpServletResponse httpServletResponse){
        String username = reqBody.getUsername();
        String password = reqBody.getPassword();
        String reqIp = servletRequest.getRemoteAddr();
        return bruteForceRequestService.handleBruteForceLoginAttempt(reqIp,username,password,httpServletResponse);
    }
}