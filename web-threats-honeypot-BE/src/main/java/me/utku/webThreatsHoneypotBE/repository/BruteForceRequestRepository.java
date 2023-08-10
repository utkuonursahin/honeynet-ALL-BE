package me.utku.webThreatsHoneypotBE.repository;

import me.utku.webThreatsHoneypotBE.model.BruteForceRequest;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface BruteForceRequestRepository extends MongoRepository<BruteForceRequest,String> {
    @Query()
    BruteForceRequest findByOrigin_SourceAndFirmRef(String source, String firmRef);
}
