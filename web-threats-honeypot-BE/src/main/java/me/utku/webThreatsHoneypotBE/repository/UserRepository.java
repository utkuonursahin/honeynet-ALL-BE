package me.utku.webThreatsHoneypotBE.repository;

import me.utku.webThreatsHoneypotBE.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User,String> {}
