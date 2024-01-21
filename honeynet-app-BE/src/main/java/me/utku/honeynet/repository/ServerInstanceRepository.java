package me.utku.honeynet.repository;

import me.utku.honeynet.model.ServerInstance;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ServerInstanceRepository extends MongoRepository<ServerInstance,String> {
    ServerInstance findByPotRefAndFirmRef(String potId, String firmId);
    List<ServerInstance> findAllByFirmRef(String firmId);
    boolean existsByPort(String port);
}
