package me.utku.webcrawlerhoneypot.service;

import lombok.extern.slf4j.Slf4j;
import me.utku.webcrawlerhoneypot.dto.SuspiciousActivity;
import me.utku.webcrawlerhoneypot.dto.ScrawlAttempt;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
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

    public Map<String,Object> generateBody(ScrawlAttempt scrawlAttempt){
        Map<String,Object> payload = new HashMap<>();
        payload.put("targetElementId", scrawlAttempt.getTargetElementId());

        Map<String,Object> map = new HashMap<>();
        map.put("origin", scrawlAttempt.getOrigin());
        map.put("payload", payload);
        map.put("category", "WEB_SCRAPING");
        map.put("potName","Web Crawl Pot");
        map.put("date", LocalDateTime.now());
        return map;
    }

    public void post(Map<String,Object> body, HttpHeaders headers){
        String url = "http://localhost:8080/suspicious/server";
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
        this.restTemplate.postForEntity(url, entity, SuspiciousActivity.class);
    }

    public void postSuspiciousActivity(ScrawlAttempt scrawlAttempt){
        try{
            HttpHeaders headers = generateHeaders();
            Map<String, Object> body = generateBody(scrawlAttempt);
            post(body,headers);
        } catch (Exception error){
            log.error("Error while posting suspicious activity: {}", error.getMessage());
        }
    }
}
