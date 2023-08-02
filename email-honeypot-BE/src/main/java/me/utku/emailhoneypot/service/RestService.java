package me.utku.emailhoneypot.service;

import lombok.extern.slf4j.Slf4j;
import me.utku.emailhoneypot.dto.EmailContent;
import me.utku.emailhoneypot.dto.SuspiciousActivity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class RestService {
    private final RestTemplate restTemplate;
    private final JWTService jwtService;

    public RestService(RestTemplateBuilder restTemplateBuilder, JWTService jwtService) {
        this.restTemplate = restTemplateBuilder.build();
        this.jwtService = jwtService;
    }

    public HttpHeaders generateHeaders(){
        try{
            String jwt = jwtService.generateJWT();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.set("In-App-Auth-Token", jwt);
            return headers;
        } catch (Exception error){
            log.error("Error while generating headers: {}", error.getMessage());
            return null;
        }
    }

    @Value("${be.firmId}")
    private String firmId;
    public Map<String,Object> generateBody(EmailContent emailContent){
        Map<String,Object> payload = new HashMap<>();
        payload.put("subject", emailContent.getSubject());

        Map<String,Object> map = new HashMap<>();
        map.put("firmRef", firmId);
        map.put("origin", emailContent.getOrigin());
        map.put("payload", payload);
        map.put("category", "EMAIL");
        map.put("potName","Phishing E-mail Pot");
        map.put("date", LocalDateTime.now());
        return map;
    }

    public void post(Map<String,Object> body, HttpHeaders headers){
        String url = "http://localhost:8080/suspicious/server";
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
        this.restTemplate.postForEntity(url, entity, SuspiciousActivity.class);
    }

    public void postSuspiciousActivity(EmailContent emailContent){
        try{
            HttpHeaders headers = generateHeaders();
            Map<String, Object> body = generateBody(emailContent);
            post(body,headers);
        } catch (Exception error){
            log.error("Error while posting suspicious activity: {}", error.getMessage());
        }
    }
}
