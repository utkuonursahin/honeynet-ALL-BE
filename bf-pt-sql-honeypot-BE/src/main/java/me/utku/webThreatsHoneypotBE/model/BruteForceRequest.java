package me.utku.webThreatsHoneypotBE.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@AllArgsConstructor
@Data
@Document
public class BruteForceRequest extends Base{
    private String origin;
    private int randAuthenticateNumber;
    private Integer timesAccessed;
    private String payloadUsername;
    private String payloadPassword;
    private LocalDateTime date;
}
