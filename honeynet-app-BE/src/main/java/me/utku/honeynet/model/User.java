package me.utku.honeynet.model;

import me.utku.honeynet.enums.UserRole;
import lombok.Data;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Data
@TypeAlias("User")
public class User extends Base{
  private String username;
  private String password;
  private String email;
  private UserRole role;
  private String firmRef;
}
