package me.utku.honeynet.service;

import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.utku.honeynet.dto.PaginatedSuspiciousActivities;
import me.utku.honeynet.dto.SuspiciousActivityFilter;
import me.utku.honeynet.dto.SuspiciousActivityGroupByCategoryDTO;
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
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.time.*;
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
    private final JavaMailSender mailSender;
    private final FirmService firmService;


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

    public List<SuspiciousActivityGroupByCategoryDTO> groupAndCountSuspiciousActivitiesByCategory(String firmId){
        try {
            GroupOperation groupByCategory = group("category").count().as("count")
                .addToSet("category").as("category")
                .addToSet("firmRef").as("firmRef");
            MatchOperation matchByFirm = match(Criteria.where("firmRef").is(firmId));
            SortOperation sortByCount = sort(Sort.by(Sort.Direction.DESC, "count"));
            Aggregation aggregation = Aggregation.newAggregation(
                groupByCategory,
                matchByFirm,
                sortByCount
            );
            AggregationResults<SuspiciousActivityGroupByCategoryDTO> results = mongoTemplate.aggregate(aggregation,"suspiciousActivity", SuspiciousActivityGroupByCategoryDTO.class);
            return results.getMappedResults();
        } catch (Exception error) {
            log.error("SuspiciousActivity service getAllActivitiesByFirm exception: {}", error.getMessage());
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
    public void sendEmail(String to, String subject, PotCategory potCategory,String potName,Object payload, Date currentDate) {

        try{
            String contentHtml =
                    "<div style=\"background-color: #F49C14; color: white;font-size: 30px;\"><h1>Suspicious Activity Alert</h1></div>" +
                            "<div style='font:italic;font-size:larger'><p>"+to+", \nWe've <span>detected</span> some suspicious activites as"+potCategory+ "in"+potName+"</p></div>"+
                            "\n<h3>Please take a look !</h3>"+potCategory+
                            "<div style='color:red'>Date : "+currentDate+"</div>"+
                            "Payload : "+payload;

            MimeMessage message = mailSender.createMimeMessage();
            message.setFrom("fakemployeebeam@gmail.com");
            message.setRecipients(MimeMessage.RecipientType.TO,to);
            message.setSubject(subject);
            message.setContent(contentHtml,"text/html;charset=utf-8");
            message.setSentDate(new Date());
            mailSender.send(message);
        }catch (Exception exception){
            log.error("EmailSenderService sendEmail exception {}", exception.getMessage());
        }
    }

    public SuspiciousActivity createActivity(SuspiciousActivity newSuspiciousActivity, HttpServletRequest httpServletRequest) {
        SuspiciousActivity suspiciousActivity = new SuspiciousActivity();
        try {
            String authToken = httpServletRequest.getHeader(TOKEN_HEADER);
            if(authToken != null && jwtService.validateJWT(authToken)){
                newSuspiciousActivity.setId(UUID.randomUUID().toString());
                firmService.get(newSuspiciousActivity.getFirmRef()).getAlertReceivers().forEach(receiver->{
                    sendEmail(receiver,"Alert",newSuspiciousActivity.getCategory(),
                            newSuspiciousActivity.getPotName(), newSuspiciousActivity.getPayload(), new Date()
                    );
                });
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