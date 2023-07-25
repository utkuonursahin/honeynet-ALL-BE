package me.utku.honeynet.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import me.utku.honeynet.enums.PotCategory;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document
public class Pot extends Base {
  private String potName;
  private String description;
  private String clientUrl;
  private PotCategory[] category;
  private String previewImagePath;
  @DBRef()
  private Firm firm;

  @JsonProperty("firm")
  public void deserializeFirm(String firmId){
    Firm firmObj = new Firm();
    firmObj.setId(firmId);
    this.firm = firmObj;
  }
}
