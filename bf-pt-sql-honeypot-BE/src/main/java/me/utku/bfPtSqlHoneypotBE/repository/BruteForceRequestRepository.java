package me.utku.bfPtSqlHoneypotBE.repository;


import me.utku.bfPtSqlHoneypotBE.model.BruteForceRequest;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface BruteForceRequestRepository extends MongoRepository<BruteForceRequest,String> {
    BruteForceRequest findByOrigin(String ip);
}
