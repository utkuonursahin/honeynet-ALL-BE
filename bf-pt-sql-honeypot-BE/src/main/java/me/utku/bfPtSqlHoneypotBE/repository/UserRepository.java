package me.utku.bfPtSqlHoneypotBE.repository;

import me.utku.bfPtSqlHoneypotBE.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User,String> {}
