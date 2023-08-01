package me.utku.honeynet.repository;

import me.utku.honeynet.model.ServerInfo;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ServerInfoRepository extends MongoRepository<ServerInfo,String> {
    Boolean existsServerInfosByPotRefAndFirmRef(String potId, String firmId);
    ServerInfo findByPotRefAndFirmRef(String potId, String firmId);
    List<ServerInfo> findAllByFirmRef(String firmId);
}