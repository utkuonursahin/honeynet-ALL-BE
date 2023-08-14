package me.utku.honeynet.model;

import lombok.Data;
import me.utku.honeynet.enums.PotCategory;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Document
@Data
@TypeAlias("Email-Info")
public class EmailInfo extends Base{
    private String emailSender;
    private String emailReceiver;
    private String emailMessage;
    //    private String potCategory;
    private String emailSubject;
    private Date emailDate;
    private String suspiciousActivityRef;

    private String backgroundImage;
}

