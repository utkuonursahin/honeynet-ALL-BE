package me.utku.honeynet.dto.email;

import lombok.AllArgsConstructor;
import lombok.Data;
import me.utku.honeynet.enums.EmailListenerStatus;

@Data
@AllArgsConstructor
public class EmailListener {
    private String email;
    private String password;
    private EmailListenerStatus status;
    private String firmRef;
}
