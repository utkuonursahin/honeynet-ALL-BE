package me.utku.honeynet.repository;

import me.utku.honeynet.model.Report;
import org.springframework.data.domain.Range;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.time.Instant;
import java.util.Date;
import java.util.List;

public interface ReportRepository extends MongoRepository<Report,String> {
    boolean existsById(String id);

    @Query(fields = "{firmRef:0, createdAt:0, reportPath:0}", sort = "{createdAt:-1}")
    List<Report> findAllByFirmRef(String firmRef);
}
