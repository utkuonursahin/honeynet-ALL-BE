package me.utku.honeynet.dto.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private final ObjectMapper objectMapper;
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException{
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");

        // Customize the JSON response
        Map<String, Object> jsonResponse = new HashMap<>();
        jsonResponse.put("statusCode", HttpServletResponse.SC_UNAUTHORIZED);
        jsonResponse.put("data", null);

        // Write the JSON response to the response body
        objectMapper.writeValue(response.getWriter(), jsonResponse);
    }
}
