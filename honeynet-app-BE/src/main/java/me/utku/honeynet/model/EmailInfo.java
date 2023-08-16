package me.utku.honeynet.model;

import lombok.Data;
import me.utku.honeynet.dto.email.EmailFooterStatics;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Document
@Data
@TypeAlias("Email-Info")
public class EmailInfo extends Base implements EmailFooterStatics {
    private String emailSender;
    private String emailReceiver;
    private String emailMessage;
    private String emailSubject;
    private Date emailDate;
    private String suspiciousActivityRef;
}

