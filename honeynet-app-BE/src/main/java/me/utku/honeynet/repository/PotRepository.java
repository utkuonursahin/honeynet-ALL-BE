package me.utku.honeynet.repository;

import me.utku.honeynet.model.Pot;
import me.utku.honeynet.model.ServerInfo;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface PotRepository extends MongoRepository<Pot,String> {
}
