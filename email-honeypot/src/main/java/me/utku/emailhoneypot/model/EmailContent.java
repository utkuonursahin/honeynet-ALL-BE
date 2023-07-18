package me.utku.emailhoneypot.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmailContent {
    private String origin;
    private Date date;
    private String subject;
}
