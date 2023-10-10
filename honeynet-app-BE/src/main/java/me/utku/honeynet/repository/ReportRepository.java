package me.utku.honeynet.repository;

import me.utku.honeynet.model.Report;
import org.springframework.data.domain.Range;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.time.Instant;
import java.util.Date;

public interface ReportRepository extends MongoRepository<Report,String> {

    Report findFirstByOrderByReportInitDateDesc();
}
