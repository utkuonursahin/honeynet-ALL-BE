package me.utku.webThreatsHoneypotBE.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class SuspiciousActivity implements Serializable {
    private String origin;
    private String category;
    private Object payload;
    private String potName;
    private LocalDateTime date;
}