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
    private Firm firm;
    private Object payload;
    private LocalDateTime date;

    @JsonProperty("firm")
    public void deserializeFirm(String firmId){
        Firm firmObj = new Firm();
        firmObj.setId(firmId);
        this.firm = firmObj;
    }
}