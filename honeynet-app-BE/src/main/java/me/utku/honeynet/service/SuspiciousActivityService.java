package me.utku.honeynet.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.utku.honeynet.dto.PaginatedSuspiciousActivities;
import me.utku.honeynet.dto.SuspiciousActivityFilter;
import me.utku.honeynet.dto.SuspiciousActivityGroupByCategoryDTO;
import me.utku.honeynet.dto.SuspiciousActivityGroupByOriginDTO;
import me.utku.honeynet.enums.PotCategory;
import me.utku.honeynet.model.SuspiciousActivity;
import me.utku.honeynet.repository.SuspiciousRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

    public Aggregation groupAggregation(String groupBy, String firmRef, String since){
        try{
            GroupOperation groupOperation = group(groupBy).count().as("count").addToSet(groupBy).as(groupBy);
            MatchOperation matchOperation = match(Criteria.where("date").gte(calculateSince(since)).and("firmRef").is(firmRef));
            SortOperation sortByCount = sort(Sort.by(Sort.Direction.DESC, "count"));
            return Aggregation.newAggregation(matchOperation, groupOperation, sortByCount);
        }catch (Exception error) {
            log.error("SuspiciousActivity service groupAggregation exception: {}", error.getMessage());
            return null;
        }
    }

    public List<SuspiciousActivityGroupByCategoryDTO> groupAndCountSuspiciousActivitiesByCategory(String since,String firmRef){
        try {
            Aggregation aggregation = groupAggregation("category", firmRef,since);
            AggregationResults<SuspiciousActivityGroupByCategoryDTO> results = mongoTemplate.aggregate(aggregation, "suspiciousActivity", SuspiciousActivityGroupByCategoryDTO.class);
            return results.getMappedResults();
        } catch (Exception error) {
            log.error("SuspiciousActivity service groupAndCountSuspiciousActivitiesByCategory exception: {}", error.getMessage());
            return null;
        }
    }

    public List<SuspiciousActivityGroupByOriginDTO> groupAndCountSuspiciousActivitiesByOrigin(String since,String firmRef){
        try {
            Aggregation aggregation = groupAggregation("origin", firmRef, since);
            AggregationResults<SuspiciousActivityGroupByOriginDTO> results = mongoTemplate.aggregate(aggregation, "suspiciousActivity", SuspiciousActivityGroupByOriginDTO.class);
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
                suspiciousActivityFilter.setOriginFilter("");
            }
            if(suspiciousActivityFilter.getDateFilters().length != 2){
                suspiciousActivityFilter.setDateFilters(new LocalDateTime[]{
                    LocalDateTime.of(2023, Month.JULY,01,00,00),
                    LocalDateTime.now()
                });
            }
            if(suspiciousActivityFilter.getCategoryFilters().isEmpty()){
                suspiciousActivityFilter.setCategoryFilters(List.of(PotCategory.values()));
            }
            Pageable pageable = PageRequest.of(page,size);
            Page<SuspiciousActivity> activities = suspiciousRepository.findAllByFirmRefAndOriginContainsAndCategoryInAndDateBetween(
                firmRef,
                suspiciousActivityFilter.getOriginFilter(),
                suspiciousActivityFilter.getCategoryFilters(),
                suspiciousActivityFilter.getDateFilters()[0],
                suspiciousActivityFilter.getDateFilters()[1],
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