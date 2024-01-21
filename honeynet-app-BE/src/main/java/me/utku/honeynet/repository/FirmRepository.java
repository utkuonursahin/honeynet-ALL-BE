package me.utku.honeynet.repository;

import me.utku.honeynet.model.Firm;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface FirmRepository extends MongoRepository<Firm,String> {}