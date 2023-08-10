package me.utku.webscrapinghoneypotBE.service;

import lombok.extern.slf4j.Slf4j;
import me.utku.webscrapinghoneypotBE.dto.IPResponse;
import me.utku.webscrapinghoneypotBE.dto.Origin;
import me.utku.webscrapinghoneypotBE.dto.SuspiciousActivity;
import me.utku.webscrapinghoneypotBE.dto.ScrawlAttempt;
import org.springframework.beans.factory.annotation.Value;
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

    public Origin getOriginDetails(String ip){
        //IP is the local ip of BEAM for developing purposes. Change it to parametrized ip later.
        String url = "http://ip-api.com/json/" + "37.202.55.242" + "?fields=16578";
        ResponseEntity<IPResponse> response = this.restTemplate.getForEntity(url, IPResponse.class);
        if(response.getStatusCode() == HttpStatus.OK){
            IPResponse body = response.getBody();
            return new Origin(ip,body.countryCode(), body.lat(), body.lon());
        }
        return null;
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

    public Map<String,Object> generateBody(ScrawlAttempt scrawlAttempt){
        Map<String,Object> payload = new HashMap<>();
        payload.put("targetElementId", scrawlAttempt.getTargetElementId());

        Map<String,Object> map = new HashMap<>();
        map.put("firmRef",firmId);
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
