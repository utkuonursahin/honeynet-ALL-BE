package me.utku.honeynet.model;

import lombok.Data;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Document
@TypeAlias("Firm")
public class Firm extends Base {
    private String firmName;
    private String previewImgPath;
    private List<String> alertReceivers;
}
