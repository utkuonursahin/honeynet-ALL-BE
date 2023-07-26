package me.utku.honeynet.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import me.utku.honeynet.enums.PotCategory;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document
public class SuspiciousActivity extends Base{
    private String origin;
    private PotCategory category;
    @DBRef
    private Pot honeypot;
    private Object payload;
    private LocalDateTime date;

    @JsonProperty("honeypot")
    public void deserializeFirm(String potId){
        Pot potObj = new Pot();
        potObj.setId(potId);
        this.honeypot = potObj;
    }
}