package me.utku.honeynet.repository;

import me.utku.honeynet.enums.PotCategory;
import me.utku.honeynet.model.SuspiciousActivity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Range;
import org.springframework.data.mongodb.repository.MongoRepository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Query;

import java.time.Instant;
import java.util.List;

public interface SuspiciousRepository extends MongoRepository<SuspiciousActivity, String> {
    @Query
    Page<SuspiciousActivity> findAllByFirmRefAndOrigin_SourceContainsAndCategoryInAndDateBetween(String firmId,
                                                                                                 String originSource,
                                                                                                 List<PotCategory> categoryFilters,
                                                                                                 Range<Instant> range, Pageable pageable);
}
