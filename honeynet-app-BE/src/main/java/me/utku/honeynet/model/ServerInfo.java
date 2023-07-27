package me.utku.honeynet.model;

import lombok.Data;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document
@TypeAlias("ServerInfo")
public class ServerInfo extends Base {
    @DBRef
    private Firm firm;
    @DBRef Pot pot;
    private String port;
}
