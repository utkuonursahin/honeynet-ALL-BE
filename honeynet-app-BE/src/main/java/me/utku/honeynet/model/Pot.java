package me.utku.honeynet.model;

import lombok.Data;
import me.utku.honeynet.enums.PotCategory;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document
@TypeAlias("Pot")
public class Pot extends Base {
  private String potName;
  private String description;
  private String serverPath;
  private String serverFileName;
  private PotCategory[] category;
  private String previewImagePath;
}
