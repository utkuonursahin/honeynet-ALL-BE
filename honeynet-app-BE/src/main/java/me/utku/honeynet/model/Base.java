package me.utku.honeynet.model;

import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
public class Base {
    @Id
    private String id;
}
