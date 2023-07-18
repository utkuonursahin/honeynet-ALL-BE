package me.utku.bfPtSqlHoneypotBE.dto;

import lombok.Builder;
import lombok.Data;
import me.utku.bfPtSqlHoneypotBE.model.User;

@Builder
@Data
public class AuthResponse {
    private boolean authenticated;
    private String message;
    private User user;
}
