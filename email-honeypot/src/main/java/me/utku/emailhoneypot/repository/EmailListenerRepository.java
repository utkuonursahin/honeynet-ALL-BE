package me.utku.emailhoneypot.repository;

import me.utku.emailhoneypot.model.EmailListener;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface EmailListenerRepository extends MongoRepository<EmailListener,String> {
}
