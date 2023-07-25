package me.utku.honeynet.model;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Document
public class Firm extends Base {
    private String firmName;
    private String previewImgPath;
    private List<String> alertReceivers;
}
