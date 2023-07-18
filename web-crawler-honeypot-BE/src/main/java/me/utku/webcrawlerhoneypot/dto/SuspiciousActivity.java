package me.utku.webcrawlerhoneypot.dto;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Document
public class SuspiciousActivity implements Serializable {
    private String origin;
    private Object payload;
    private String category;
    private String potName;
    private LocalDateTime date;
}