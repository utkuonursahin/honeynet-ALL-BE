package me.utku.honeynet.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.utku.honeynet.dto.PaginatedSuspiciousActivities;
import me.utku.honeynet.dto.SuspiciousActivityFilter;
import me.utku.honeynet.dto.security.CustomUserDetails;
import me.utku.honeynet.enums.PotCategory;
import me.utku.honeynet.model.SuspiciousActivity;
import me.utku.honeynet.repository.SuspiciousRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class SuspiciousActivityService {
    private final SuspiciousRepository suspiciousRepository;
    private final JWTService jwtService;
    private static final String TOKEN_HEADER = "In-App-Auth-Token";

    public PaginatedSuspiciousActivities createPaginatedSuspiciousActivity(Page<SuspiciousActivity> activities, int page, int size){
        PaginatedSuspiciousActivities paginatedSuspiciousActivities = new PaginatedSuspiciousActivities();
        paginatedSuspiciousActivities.setActivityList(activities.getContent());
        paginatedSuspiciousActivities.setCurrentPage(Long.valueOf(page));
        paginatedSuspiciousActivities.setCurrentSize(Long.valueOf(size));
        paginatedSuspiciousActivities.setTotalPage(Long.valueOf(activities.getTotalPages()));
        paginatedSuspiciousActivities.setTotalSize(Long.valueOf(activities.getTotalElements()));
        return paginatedSuspiciousActivities;
    }

    public PaginatedSuspiciousActivities getAllActivities(int page, int size) {
        try {
            Pageable pageable = PageRequest.of(page,size);
            Page<SuspiciousActivity> activities = suspiciousRepository.findAll(pageable);
            return createPaginatedSuspiciousActivity(activities,page,size);
        } catch (Exception error) {
            log.error("SuspiciousActivity service getAllActivities exception: {}", error.getMessage());
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
            Page<SuspiciousActivity> activities = null;
            activities = suspiciousRepository.findAllByFirmRefAndOriginContainsAndCategoryInAndDateBetween(
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