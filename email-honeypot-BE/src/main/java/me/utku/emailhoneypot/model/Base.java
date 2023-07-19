package me.utku.emailhoneypot.model;

import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
public class Base {
    @Id
    public String id;
}
