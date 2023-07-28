package me.utku.honeynet.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import me.utku.honeynet.enums.UserRole;
import lombok.Data;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Data
@TypeAlias("User")
public class User extends Base{
  private String username;
  private String password;
  private String email;
  private UserRole role;
  @DBRef
  private Firm firm;

  @JsonProperty("firm")
  public void deserializeFirm(String firmId){
    Firm firmObj = new Firm();
    firmObj.setId(firmId);
    this.firm = firmObj;
  }
}
