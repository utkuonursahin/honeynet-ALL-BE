package me.utku.emailhoneypot.model;

import jakarta.mail.Session;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.utku.emailhoneypot.enums.ListenerStatus;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document
@AllArgsConstructor
@NoArgsConstructor
public class EmailListener extends Base{
    private Session session;
    private String username;
    private String password;
    private ListenerStatus status;
}
