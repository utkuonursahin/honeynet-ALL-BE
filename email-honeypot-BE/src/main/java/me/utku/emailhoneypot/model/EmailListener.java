package me.utku.emailhoneypot.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import me.utku.emailhoneypot.enums.EmailListenerStatus;
import org.springframework.data.mongodb.core.mapping.Document;

@EqualsAndHashCode(callSuper = true)
@Data
@Document
@AllArgsConstructor
@NoArgsConstructor
public class EmailListener extends Base{
    private String email;
    private String password;
    private EmailListenerStatus status;
}
