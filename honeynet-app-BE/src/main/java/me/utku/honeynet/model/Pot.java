package me.utku.honeynet.model;

import lombok.Data;
import me.utku.honeynet.enums.PotCategory;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document
public class Pot extends Base {
  private String name;
  private String description;
  private String url; //make it client
  private String setupUrl;
  private PotCategory[] category;
  private String previewImagePath;
}
