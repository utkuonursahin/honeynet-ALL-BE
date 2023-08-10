package me.utku.honeynet.repository;

import me.utku.honeynet.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface UserRepository extends MongoRepository<User,String> {
  @Query
  User findByEmail(String email);

  //ONLY USED FOR FINDING IMPERSONATION USER, ONLY EMAIL FIELD NEEDED
  @Query(fields = "{id:0,username: 0,password:0,role:0,firmRef: 0}")
  User findFirstByFirmRef(String firmId);
}