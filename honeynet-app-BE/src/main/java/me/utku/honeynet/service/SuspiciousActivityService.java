package me.utku.honeynet.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.utku.honeynet.enums.PotCategory;
import me.utku.honeynet.model.SuspiciousActivity;
import me.utku.honeynet.repository.SuspiciousRepository;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
@Slf4j
@RequiredArgsConstructor
public class SuspiciousActivityService {
    private final SuspiciousRepository suspiciousRepository;
    private final JWTService jwtService;

    public List<SuspiciousActivity> getAllActivities(HttpServletRequest httpServletRequest) {
        List<SuspiciousActivity> activities = new ArrayList<>();
        try {
            activities = suspiciousRepository.findAll();
        } catch (Exception error) {
            log.error("SuspiciousActivity service getAllActivities exception: {}", error.getMessage());
        }
        return activities;
    }

    public SuspiciousActivity getActivityById(String id, HttpServletRequest httpServletRequest) {
        SuspiciousActivity suspiciousActivity = new SuspiciousActivity();
        try {
            suspiciousActivity = suspiciousRepository.findById(id).orElse(null);
        } catch (Exception error) {
            log.error("SuspiciousActivity service getActivityById exception: {}", error.getMessage());
        }
        return suspiciousActivity;
    }

    public List<SuspiciousActivity> getActivitiesByCategory(PotCategory category, HttpServletRequest httpServletRequest) {
        List<SuspiciousActivity> suspiciousActivity = new ArrayList<SuspiciousActivity>();
        try {
            suspiciousActivity = suspiciousRepository.findByCategory(category);
        } catch (Exception error) {
            log.error("SuspiciousActivity service getActivityById exception: {}", error.getMessage());
        }
        return suspiciousActivity;
    }

    public List<SuspiciousActivity> getActivitiesByDateBetween(String start, String end, HttpServletRequest httpServletRequest){
        List<SuspiciousActivity> suspiciousActivity = new ArrayList<SuspiciousActivity>();
        try {
            DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
            suspiciousActivity = suspiciousRepository.findByDateBetween(
                LocalDateTime.parse(start,inputFormatter),
                LocalDateTime.parse(end,inputFormatter)
            );
        } catch (Exception error) {
            log.error("SuspiciousActivity service getActivityById exception: {}", error.getMessage());
        }
        return suspiciousActivity;
    }

    public SuspiciousActivity createActivity(SuspiciousActivity newSuspiciousActivity, HttpServletRequest httpServletRequest) {
        SuspiciousActivity suspiciousActivity = new SuspiciousActivity();
        try {
            String authToken = httpServletRequest.getHeader("In-App-Auth-Token");
            if(authToken != null && jwtService.validateJWT(authToken)){
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
            String authToken = httpServletRequest.getHeader("In-App-Auth-Token");
            if(authToken != null && jwtService.validateJWT(authToken)) {
                existingSuspiciousActivity = suspiciousRepository.findById(id).orElse(null);
                if (existingSuspiciousActivity == null) {
                    throw new Exception("No activity found with that id");
                }
                suspiciousRepository.save(existingSuspiciousActivity);
            }
        } catch (Exception error) {
            log.error("SuspiciousActivity service updateActivity exception: {}", error.getMessage());
        }
        return existingSuspiciousActivity;
    }

    public boolean deleteActivity(String id, HttpServletRequest httpServletRequest ) {
        boolean isDeleted = false;
        try {
            String authToken = httpServletRequest.getHeader("In-App-Auth-Token");
            if(authToken != null && jwtService.validateJWT(authToken)){
                suspiciousRepository.deleteById(id);
                isDeleted = true;
            }
        } catch (Exception error) {
            log.error("SuspiciousActivity service deleteActivity exception: {}", error.getMessage());
        }
        return isDeleted;
    }
}