package me.utku.emailhoneypot.dto;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Document
public class SuspiciousActivity implements Serializable {
    private String origin;
    private String category;
    private Object payload;
    private LocalDateTime date;
}