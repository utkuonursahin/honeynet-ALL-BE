package me.utku.honeynet.dto.email;

import lombok.Data;

@Data
public class EmailSetupRequest {
    private String email;
    private String password;
}
