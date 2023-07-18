package me.utku.emailhoneypot.dto;

import lombok.Data;

@Data
public class EmailSetupRequest {
    private String email;
    private String password;
}
