package me.utku.honeynet.dto;

import lombok.Data;

@Data
public class EmailSetupRequest {
    private String email;
    private String password;
}
