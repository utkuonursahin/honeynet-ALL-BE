package me.utku.webThreatsHoneypotBE.dto;

import lombok.Builder;
import lombok.Data;
import me.utku.webThreatsHoneypotBE.model.User;

@Builder
@Data
public class AuthResponse {
    private boolean authenticated;
    private String message;
    private User user;
}
