package me.utku.emailhoneypot.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import me.utku.emailhoneypot.enums.EmailListenerStatus;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

@EqualsAndHashCode(callSuper = true)
@Data
@Document
@TypeAlias("EmailListener")
@Accessors(chain = true)
public class EmailListener extends Base{
    private String email;
    private String password;
    private EmailListenerStatus status;
    private String firmRef;
}
