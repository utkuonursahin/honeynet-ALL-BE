package me.utku.honeynet.repository;

import me.utku.honeynet.enums.PotCategory;
import me.utku.honeynet.model.SuspiciousActivity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface SuspiciousRepository extends MongoRepository<SuspiciousActivity,String> {
    List<SuspiciousActivity> findByCategory(PotCategory category);
    List<SuspiciousActivity> findByDateBetween(LocalDateTime start, LocalDateTime end);
}
