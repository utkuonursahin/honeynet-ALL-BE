package me.utku.honeynet.model;

import lombok.Data;
import me.utku.honeynet.enums.PotCategory;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document
@TypeAlias("Suspicious-Activity")
public class SuspiciousActivity extends Base{
    private String firmRef;
    private String origin;
    private PotCategory category;
    private String potName;
    private Object payload;
    private LocalDateTime date;
}