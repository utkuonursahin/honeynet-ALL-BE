package me.utku.honeynet.model;

import lombok.Data;
import lombok.experimental.Accessors;
import me.utku.honeynet.enums.ServerInstanceStatus;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document
@TypeAlias("ServerInstance")
@Accessors(chain = true)
public class ServerInstance extends Base {
    private String firmRef;
    private String potRef;
    private String hostName;
    private String host;
    private String port;
    private String url;
    private ServerInstanceStatus status;
}
