package me.utku.honeynet.model;

import lombok.Data;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document
@TypeAlias("ServerInfo")
public class ServerInfo extends Base {
    private String firmRef;
    private String potRef;
    private String port;
}
