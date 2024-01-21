package me.utku.honeynet.model;

import lombok.Data;
import lombok.experimental.Accessors;
import me.utku.honeynet.dto.suspiciousActivity.Origin;
import me.utku.honeynet.enums.PotCategory;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document
@TypeAlias("Suspicious-Activity")
@Accessors(chain = true)
public class SuspiciousActivity extends Base{
    private String firmRef;
    private Origin origin;
    private PotCategory category;
    private String potName;
    private Object payload;
    private LocalDateTime date;
}