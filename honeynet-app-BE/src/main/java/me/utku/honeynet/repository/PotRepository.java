package me.utku.honeynet.repository;

import me.utku.honeynet.model.Firm;
import me.utku.honeynet.model.Pot;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface PotRepository extends MongoRepository<Pot,String> {
    List<Pot> findAllByFirm_Id(String firmId);
}
