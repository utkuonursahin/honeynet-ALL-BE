package me.utku.honeynet.repository;

import me.utku.honeynet.model.Pot;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PotRepository extends MongoRepository<Pot,String> {
}
