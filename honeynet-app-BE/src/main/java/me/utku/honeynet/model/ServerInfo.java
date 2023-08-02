package me.utku.honeynet.model;

import lombok.Data;
import lombok.experimental.Accessors;
import me.utku.honeynet.enums.ServerInfoStatus;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document
@TypeAlias("ServerInfo")
@Accessors(chain = true)
public class ServerInfo extends Base {
    private String firmRef;
    private String potRef;
    private String hostName;
    private String host;
    private String port;
    private String url;
    private ServerInfoStatus status;
}
