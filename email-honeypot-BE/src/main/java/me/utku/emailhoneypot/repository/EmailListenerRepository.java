package me.utku.emailhoneypot.repository;

import me.utku.emailhoneypot.model.EmailListener;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmailListenerRepository extends MongoRepository<EmailListener,String> {
    boolean existsByEmail(String email);
    EmailListener findByEmail(String email);

}