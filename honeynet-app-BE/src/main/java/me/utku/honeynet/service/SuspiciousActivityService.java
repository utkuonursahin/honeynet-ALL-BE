package me.utku.honeynet.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.utku.honeynet.dto.*;
import me.utku.honeynet.dto.chart.SuspiciousActivityGroupByCategoryDTO;
import me.utku.honeynet.dto.chart.SuspiciousActivityGroupByOriginCountryDTO;
import me.utku.honeynet.dto.chart.SuspiciousActivityGroupByOriginSourceDTO;
import me.utku.honeynet.enums.PotCategory;
import me.utku.honeynet.model.SuspiciousActivity;
import me.utku.honeynet.repository.SuspiciousRepository;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class SuspiciousActivityService {
    private final SuspiciousRepository suspiciousRepository;
    private final JWTService jwtService;
    private final MongoTemplate mongoTemplate;
    private static final String TOKEN_HEADER = "In-App-Auth-Token";

    public PaginatedSuspiciousActivities createPaginatedSuspiciousActivity(Page<SuspiciousActivity> activities, int page, int size){
        PaginatedSuspiciousActivities paginatedSuspiciousActivities = new PaginatedSuspiciousActivities();
        paginatedSuspiciousActivities.setActivityList(activities.getContent());
        paginatedSuspiciousActivities.setCurrentPage((long) page);
        paginatedSuspiciousActivities.setCurrentSize((long) size);
        paginatedSuspiciousActivities.setTotalPage((long) activities.getTotalPages());
        paginatedSuspiciousActivities.setTotalSize(activities.getTotalElements());
        return paginatedSuspiciousActivities;
    }

    public Date calculateSince(String dateInput){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("uuuu-MM-dd'T'HH:mm:ss.SSSxxx");
        String dateFilter;
        if(dateInput.equals("24h")){
            dateFilter = OffsetDateTime.now(ZoneOffset.UTC).minusHours(24).format(formatter);
        } else if(dateInput.equals("1w")){
            dateFilter = OffsetDateTime.now(ZoneOffset.UTC).minusDays(7).format(formatter);
        } else if(dateInput.equals("2w")){
            dateFilter = OffsetDateTime.now(ZoneOffset.UTC).minusDays(14).format(formatter);
        } else if (dateInput.equals("1m")){
            dateFilter = OffsetDateTime.now(ZoneOffset.UTC).minusMonths(1).format(formatter);
        } else if (dateInput.equals("3m")){
            dateFilter = OffsetDateTime.now(ZoneOffset.UTC).minusMonths(3).format(formatter);
        } else if (dateInput.equals("6m")){
            dateFilter = OffsetDateTime.now(ZoneOffset.UTC).minusMonths(6).format(formatter);
        } else {
            dateFilter = OffsetDateTime.of(2023, 05,01,00,00,00,00,ZoneOffset.UTC).format(formatter);
        }
        return Date.from(Instant.from(formatter.parse(dateFilter)));
    }

    public List<SuspiciousActivityGroupByCategoryDTO> groupAndCountSuspiciousActivitiesByCategory(String since, String firmRef){
        try {
            GroupOperation groupOperation = group("category").count().as("count");
            MatchOperation matchOperation = match(Criteria.where("date").gte(calculateSince(since)).and("firmRef").is(firmRef));
            SortOperation sortByCount = sort(Sort.by(Sort.Direction.DESC, "count"));
            ProjectionOperation projectionOperation = project("count").and("category").previousOperation();
            Aggregation aggregation = Aggregation.newAggregation(matchOperation, groupOperation, sortByCount, projectionOperation);
            AggregationResults<SuspiciousActivityGroupByCategoryDTO> results = mongoTemplate.aggregate(aggregation, "suspiciousActivity", SuspiciousActivityGroupByCategoryDTO.class);
            return results.getMappedResults();
        } catch (Exception error) {
            log.error("SuspiciousActivity service groupAndCountSuspiciousActivitiesByCategory exception: {}", error.getMessage());
            return null;
        }
    }

    public List<SuspiciousActivityGroupByOriginSourceDTO> groupAndCountSuspiciousActivitiesByOriginSource(String since, String firmRef){
        try {
            GroupOperation groupOperation = group("origin.source").count().as("count");
            MatchOperation matchOperation = match(Criteria.where("date").gte(calculateSince(since)).and("firmRef").is(firmRef));
            SortOperation sortByCount = sort(Sort.by(Sort.Direction.DESC, "count"));
            ProjectionOperation projectionOperation = project("count").and("source").previousOperation();
            Aggregation aggregation = Aggregation.newAggregation(matchOperation, groupOperation, sortByCount, projectionOperation);
            AggregationResults<SuspiciousActivityGroupByOriginSourceDTO> results = mongoTemplate.aggregate(aggregation, "suspiciousActivity", SuspiciousActivityGroupByOriginSourceDTO.class);
            return results.getMappedResults();
        } catch (Exception error) {
            log.error("SuspiciousActivity service groupAndCountSuspiciousActivitiesByCategory exception: {}", error.getMessage());
            return null;
        }
    }

    public List<SuspiciousActivityGroupByOriginCountryDTO> groupAndCountSuspiciousActivitiesByOriginCountry(String since, String firmRef){
        try {
            GroupOperation groupOperation = group("origin.country").count().as("count");
            MatchOperation matchOperation = match(Criteria.where("date").gte(calculateSince(since)).and("firmRef").is(firmRef));
            SortOperation sortByCount = sort(Sort.by(Sort.Direction.DESC, "count"));
            ProjectionOperation projectionOperation = project("count").and("country").previousOperation();
            Aggregation aggregation = Aggregation.newAggregation(matchOperation, groupOperation, sortByCount, projectionOperation);
            AggregationResults<SuspiciousActivityGroupByOriginCountryDTO> results = mongoTemplate.aggregate(aggregation, "suspiciousActivity", SuspiciousActivityGroupByOriginCountryDTO.class);
            return results.getMappedResults();
        } catch (Exception error) {
            log.error("SuspiciousActivity service groupAndCountSuspiciousActivitiesByCategory exception: {}", error.getMessage());
            return null;
        }
    }

    public SuspiciousActivity getActivityById(String id) {
        SuspiciousActivity suspiciousActivity = new SuspiciousActivity();
        try {
            suspiciousActivity = suspiciousRepository.findById(id).orElse(null);
        } catch (Exception error) {
            log.error("SuspiciousActivity service getActivityById exception: {}", error.getMessage());
        }
        return suspiciousActivity;
    }

    public PaginatedSuspiciousActivities filterActivities(String firmRef, SuspiciousActivityFilter suspiciousActivityFilter, int page, int size){
        try {
            if(suspiciousActivityFilter.getOriginFilter() == null){
                suspiciousActivityFilter.setOriginFilter(new Origin("",""));
            }
            if(suspiciousActivityFilter.getDateFilters().length != 2){
                suspiciousActivityFilter.setDateFilters(
                    new Instant[]{
                        OffsetDateTime.of(2023, 05,01,00,00,00,00,ZoneOffset.UTC).toInstant(),
                        OffsetDateTime.now(ZoneOffset.UTC).toInstant()
                    }
                );
            }
            if(suspiciousActivityFilter.getCategoryFilters().isEmpty()){
                suspiciousActivityFilter.setCategoryFilters(List.of(PotCategory.values()));
            }
            Pageable pageable = PageRequest.of(page,size);
            Page<SuspiciousActivity> activities = suspiciousRepository.findAllByFirmRefAndOrigin_SourceContainsAndCategoryInAndDateBetween(
                firmRef,
                suspiciousActivityFilter.getOriginFilter().source(),
                suspiciousActivityFilter.getCategoryFilters(),
                Range.<Instant>closed(suspiciousActivityFilter.getDateFilters()[0], suspiciousActivityFilter.getDateFilters()[1]),
                pageable
            );
            return createPaginatedSuspiciousActivity(activities,page,size);
        } catch (Exception error) {
            log.error("SuspiciousActivity service filterActivities exception: {}", error.getMessage());
            return null;
        }
    }

    public SuspiciousActivity createActivity(SuspiciousActivity newSuspiciousActivity, HttpServletRequest httpServletRequest) {
        SuspiciousActivity suspiciousActivity = new SuspiciousActivity();
        try {
            String authToken = httpServletRequest.getHeader(TOKEN_HEADER);
            if(authToken != null && jwtService.validateJWT(authToken)){
                newSuspiciousActivity.setId(UUID.randomUUID().toString());
                suspiciousActivity = suspiciousRepository.save(newSuspiciousActivity);
            }
        } catch (Exception error) {
            log.error("SuspiciousActivity service createActivity exception: {}", error.getMessage());
        }
        return suspiciousActivity;
    }

    //NOT COMPLETED
    public SuspiciousActivity updateActivity(String id, SuspiciousActivity updatedSuspiciousActivity, HttpServletRequest httpServletRequest ) {
        SuspiciousActivity existingSuspiciousActivity = new SuspiciousActivity();
        try {
            String authToken = httpServletRequest.getHeader(TOKEN_HEADER);
            if(authToken != null && jwtService.validateJWT(authToken)) {
                existingSuspiciousActivity = suspiciousRepository.findById(id).orElse(null);
                if (existingSuspiciousActivity == null) {
                    throw new Exception("No activity found with that id");
                }
                if(updatedSuspiciousActivity.getOrigin() != null)
                    existingSuspiciousActivity.setOrigin(updatedSuspiciousActivity.getOrigin());
                suspiciousRepository.save(existingSuspiciousActivity);
            }
        } catch (Exception error) {
            log.error("SuspiciousActivity service updateActivity exception: {}", error.getMessage());
        }
        return existingSuspiciousActivity;
    }

    public boolean deleteActivity(String id, HttpServletRequest httpServletRequest ) {
        boolean isDeleted = false;
        String origin = httpServletRequest.getRemoteAddr();
        try {
            String authToken = httpServletRequest.getHeader(TOKEN_HEADER);
            if(authToken != null && jwtService.validateJWT(authToken)){
                suspiciousRepository.deleteById(id);
                isDeleted = true;
                log.info("SuspiciousActivity with id: {} deleted by {}", id, origin);
            }
        } catch (Exception error) {
            log.error("SuspiciousActivity service deleteActivity exception: {}", error.getMessage());
        }
        return isDeleted;
    }
}