package me.utku.honeynet.model;

import lombok.Data;
import me.utku.honeynet.enums.PotCategory;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashSet;

@Data
@Document
public class Pot extends Base {
  private String potName;
  private String description;
  private String clientUrl;
  private String serverPath;
  private PotCategory[] category;
  private String previewImagePath;
  @DBRef()
  private HashSet<ServerInfo> serverInfoList;
}
