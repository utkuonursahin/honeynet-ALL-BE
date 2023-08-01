package me.utku.honeynet.repository;

import lombok.NonNull;
import me.utku.honeynet.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Optional;

public interface UserRepository extends MongoRepository<User,String> {
  @Query(fields = "{password:0}")
  @NonNull
  Optional<User> findById(@NonNull String id);

  @Query
  User findByEmail(String email);

  @Query(fields = "{password:0}")
  User findFirstByFirmRef(String firmId);
}