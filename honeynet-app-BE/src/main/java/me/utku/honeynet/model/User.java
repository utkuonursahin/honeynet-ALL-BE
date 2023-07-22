package me.utku.honeynet.model;

import me.utku.honeynet.enums.Role;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document
@Data
public class User extends Base{
  private String username;
  private String password;
  private String email;
  private List<String> notificationReceiverMails;
  private Role role;
}
