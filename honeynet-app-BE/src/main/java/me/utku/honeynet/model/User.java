package me.utku.honeynet.model;

import me.utku.honeynet.enums.Role;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Data
public class User extends Base{
  private String username;
  private String email;
  private String password;

  private Role role;
}
