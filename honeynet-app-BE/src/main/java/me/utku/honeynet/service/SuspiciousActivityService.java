package me.utku.honeynet.service;

import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.utku.honeynet.dto.*;
import me.utku.honeynet.dto.chart.SuspiciousActivityGroupByCategoryDTO;
import me.utku.honeynet.dto.chart.SuspiciousActivityGroupByOriginCountryDTO;
import me.utku.honeynet.dto.chart.SuspiciousActivityGroupByOriginSourceDTO;
import me.utku.honeynet.dto.email.EmailFooterStatics;
import me.utku.honeynet.dto.suspiciousActivity.PaginatedSuspiciousActivities;
import me.utku.honeynet.dto.suspiciousActivity.SuspiciousActivityFilter;
import me.utku.honeynet.enums.PotCategory;
import me.utku.honeynet.model.EmailInfo;
import me.utku.honeynet.model.SuspiciousActivity;
import me.utku.honeynet.repository.SuspiciousRepository;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

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
    private final EmailInfoService emailInfoService;
    private final JavaMailSender mailSender;
    private final FirmService firmService;


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
            log.error("Exception occurs in groupAndCountSuspiciousActivitiesByCategory operation of SuspiciousActivityService : {}", error.getMessage());
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
            log.error("Exception occurs in groupAndCountSuspiciousActivitiesByOriginSource operation of SuspiciousActivityService : {}", error.getMessage());
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
            log.error("Exception occurs in groupAndCountSuspiciousActivitiesByOriginCountry operation of SuspiciousActivityService: {}", error.getMessage());
            return null;
        }
    }

    public SuspiciousActivity getActivityById(String id) {
        SuspiciousActivity suspiciousActivity = new SuspiciousActivity();
        try {
            suspiciousActivity = suspiciousRepository.findById(id).orElse(null);
        } catch (Exception error) {
            log.error("Exception occurs in get activity by ID operation of SuspicioysActivityService : {}", error.getMessage());
        }
        return suspiciousActivity;
    }


    private static String renderThymeleafTemplate(String templateName, Map<String,Object> model){
        TemplateEngine templateEngine = new TemplateEngine();
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setPrefix("templates/");
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateResolver.setCharacterEncoding("UTF-8");
        templateEngine.setTemplateResolver(templateResolver);
        Context context = new Context();
        context.setVariables(model);
        return templateEngine.process(templateName, context);
    }

    public PaginatedSuspiciousActivities filterActivities(String firmRef, SuspiciousActivityFilter suspiciousActivityFilter, int page, int size){
        try {
            if(suspiciousActivityFilter.getOriginFilter() == null){
                suspiciousActivityFilter.setOriginFilter(new Origin("","","",""));
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
            log.error("Exception occurs in filterActivities operation of SuspiciousActivityService : {}", error.getMessage());
            return null;
        }
    }

    public void sendEmail(SuspiciousActivity newSuspiciousActivity, String to, String sender, String subject, PotCategory potCategory, String potName, Object payload, Date currentDate, EmailInfo email, Origin origin) {
        EmailFooterStatics emailFooterStatics = new EmailFooterStatics() {};
        String companyName = emailFooterStatics.COMPANY_NAME;
        String address = emailFooterStatics.ADDRESS;
        String phoneNumber = emailFooterStatics.PHONE_NUMBER;
        String companyEmail = emailFooterStatics.COMPANY_EMAIL;
        try{
            Map<String,Object> model = new HashMap<>();
            model.put("to",to);
            model.put("companyName",companyName);
            model.put("address",address);
            model.put("phoneNumber",phoneNumber);
            model.put("companyEmail",companyEmail);
            model.put("attackCategory",potCategory);
            model.put("potName",potName);
            model.put("date",currentDate);
            model.put("sourceCountry",origin.country());
            model.put("sourceIP",origin.source());
            String renderedHtml = renderThymeleafTemplate("mail.html",model);
            MimeMessage message = mailSender.createMimeMessage();
            message.setFrom(sender);
            email.setEmailSender(sender);
            message.setRecipients(MimeMessage.RecipientType.TO,to);
            email.setEmailReceiver(to);
            message.setSubject(subject);
            email.setEmailSubject(subject);
            message.setContent(renderedHtml,"text/html;charset=utf-8");
            message.setSentDate(currentDate);
            email.setEmailDate(currentDate);
            email.setEmailMessage(String.valueOf(message));
            email.setSuspiciousActivityRef(newSuspiciousActivity.getId());
            emailInfoService.create(email);
            mailSender.send(message);


        }catch (Exception exception){
            log.error("Exception occurs in send email operation of SuspiciousActivityService : {}", exception.getMessage());
        }
    }

        public SuspiciousActivity createActivity(SuspiciousActivity newSuspiciousActivity, HttpServletRequest httpServletRequest) {
            SuspiciousActivity suspiciousActivity = new SuspiciousActivity();
            EmailInfo email = new EmailInfo();
            try {
                String authToken = httpServletRequest.getHeader(TOKEN_HEADER);
                if(authToken != null && jwtService.validateJWT(authToken)){
                    newSuspiciousActivity.setId(UUID.randomUUID().toString());
                    firmService.get(newSuspiciousActivity.getFirmRef()).getAlertReceivers().forEach(receiver->{
                        sendEmail(newSuspiciousActivity,receiver,"fakemployeebeam@gmail.com","Alert",newSuspiciousActivity.getCategory(),
                                newSuspiciousActivity.getPotName(), newSuspiciousActivity.getPayload(), new Date(),
                                email, newSuspiciousActivity.getOrigin()
                        );
                    });
                    suspiciousActivity = suspiciousRepository.save(newSuspiciousActivity);
                    log.info("New Suspicious Activity successfully noted as {} attack with ID : {}", newSuspiciousActivity.getCategory(),newSuspiciousActivity.getId());
                }
            } catch (Exception error) {
                log.error("Exception occurs in create activity operation of SupiciousActivityService : {}", error.getMessage());
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
                log.info("Selected suspicious activity has been updated successfully with ID : {}",id);
            }
        } catch (Exception error) {
            log.error("Exception occurs in update operation of SuspiciousActivityService : {}", error.getMessage());
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
                log.info("SuspiciousActivity with id: {} has deleted by {}", id, origin);
            }
        } catch (Exception error) {
            log.error("Exception occurs in delete operation of SuspiciousActivityService : {}", error.getMessage());
        }
        return isDeleted;
    }
}