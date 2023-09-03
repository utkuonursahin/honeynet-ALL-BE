package me.utku.honeynet.repository;

import me.utku.honeynet.model.Report;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ReportRepository extends MongoRepository<Report,String> {
}
