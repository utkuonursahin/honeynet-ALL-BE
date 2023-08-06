package com.beam.uploadfile.suspiciousactivity;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document
public class SuspiciousActivity{
    private Origin origin;
    private String category;
    private Object payload;
    private LocalDateTime date;
}