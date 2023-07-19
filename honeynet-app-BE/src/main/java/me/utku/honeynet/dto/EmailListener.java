package me.utku.honeynet.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import me.utku.honeynet.enums.EmailListenerStatus;

@Data
@AllArgsConstructor
public class EmailListener {
    private String email;
    private String password;
    private EmailListenerStatus status;
}
