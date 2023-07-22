package me.utku.webThreatsHoneypotBE.repository;


import me.utku.webThreatsHoneypotBE.model.BruteForceRequest;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface BruteForceRequestRepository extends MongoRepository<BruteForceRequest,String> {
    BruteForceRequest findByOrigin(String ip);
}
