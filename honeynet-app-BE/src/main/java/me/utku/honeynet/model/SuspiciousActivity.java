package me.utku.honeynet.model;

import lombok.Data;
import me.utku.honeynet.enums.PotCategory;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document
public class SuspiciousActivity extends Base{
    private String origin;
    private PotCategory category;
    private String potName;
    private Object payload;
    private LocalDateTime date;
}