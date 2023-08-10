package me.utku.webThreatsHoneypotBE.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import me.utku.webThreatsHoneypotBE.dto.Origin;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@AllArgsConstructor
@Data
@Document
@TypeAlias("Brute-Force")
public class BruteForceRequest extends Base{
    private Origin origin;
    private int randAuthenticateNumber;
    private Integer timesAccessed;
    private String payloadUsername;
    private String payloadPassword;
    private LocalDateTime date;
    private String firmRef;
}
