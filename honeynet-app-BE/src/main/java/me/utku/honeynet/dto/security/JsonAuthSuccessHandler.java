package me.utku.honeynet.dto.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class JsonAuthSuccessHandler implements AuthenticationSuccessHandler {

  private final ObjectMapper objectMapper;

  @Override
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
    response.setStatus(HttpServletResponse.SC_OK);
    response.setContentType("application/json");
    CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
    userDetails.setPassword(null);

    // Customize the JSON response
    Map<String, Object> jsonResponse = new HashMap<>();
    jsonResponse.put("statusCode", HttpServletResponse.SC_OK);
    jsonResponse.put("data", userDetails);

    // Write the JSON response to the response body
    objectMapper.writeValue(response.getWriter(), jsonResponse);
  }
}