package me.utku.honeynet.repository;

import me.utku.honeynet.model.ServerInfo;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ServerInfoRepository extends MongoRepository<ServerInfo,String> {
    Boolean existsServerInfosByPotIdAndFirmId(String potId, String firmId);
    ServerInfo findByPotIdAndFirmId(String potId, String firmId);
}
