package me.utku.honeynet.repository;

import me.utku.honeynet.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User,String> {
  User findByUsername(String target);
}