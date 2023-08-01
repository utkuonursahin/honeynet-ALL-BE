package me.utku.honeynet.model;

import lombok.Data;
import me.utku.honeynet.enums.ServerInfoStatus;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document
@TypeAlias("ServerInfo")
public class ServerInfo extends Base {
    private String firmRef;
    private String potRef;
    private String hostName;
    private String host;
    private String port;
    private ServerInfoStatus status;
}
