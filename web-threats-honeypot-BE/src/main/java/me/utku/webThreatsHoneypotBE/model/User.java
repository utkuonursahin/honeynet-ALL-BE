package me.utku.webThreatsHoneypotBE.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
@Document
@TypeAlias("User")
public class User extends Base{
    private String username;
    private String password;
    private String role;
    private String imagePath;
}
