package me.utku.emailhoneypot.repository;

import me.utku.emailhoneypot.model.EmailListener;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmailListenerRepository extends MongoRepository<EmailListener,String> {
    boolean existsByEmail(String email);
    EmailListener findByEmail(String email);
    List<EmailListener> findAllByFirmRef(String firmId);
}