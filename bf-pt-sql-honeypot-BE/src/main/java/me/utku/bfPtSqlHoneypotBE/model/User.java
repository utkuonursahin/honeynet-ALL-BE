package me.utku.bfPtSqlHoneypotBE.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
@Document
public class User extends Base{
    private String username;
    private String password;
    private String role;
    private String imagePath;
}
