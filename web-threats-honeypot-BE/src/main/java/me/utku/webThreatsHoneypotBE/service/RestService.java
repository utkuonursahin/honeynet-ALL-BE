package me.utku.webThreatsHoneypotBE.service;

import lombok.extern.slf4j.Slf4j;
import me.utku.webThreatsHoneypotBE.dto.IPResponse;
import me.utku.webThreatsHoneypotBE.dto.Origin;
import me.utku.webThreatsHoneypotBE.dto.SuspiciousActivity;
import me.utku.webThreatsHoneypotBE.model.BruteForceRequest;
import me.utku.webThreatsHoneypotBE.dto.PathTraversalRequest;
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

    public Map<String,Object> generateBody(BruteForceRequest bruteForceRequest){
        Map<String,Object> payload = new HashMap<>();
        payload.put("username", bruteForceRequest.getPayloadUsername());
        payload.put("password", bruteForceRequest.getPayloadPassword());

        Map<String,Object> map = new HashMap<>();
        map.put("firmRef",firmId);
        map.put("origin", bruteForceRequest.getOrigin());
        map.put("category", "BRUTE_FORCE");
        map.put("potName","Web Threats Pot");
        map.put("payload", payload);
        map.put("date", LocalDateTime.now());
        return map;
    }

    public Map<String,Object> generateBody(PathTraversalRequest pathTraversalRequest){
        Map<String,Object> payload = new HashMap<>();
        payload.put("path", pathTraversalRequest.getPayloadPath());

        Map<String,Object> map = new HashMap<>();
        map.put("firmRef",firmId);
        map.put("origin", pathTraversalRequest.getOrigin());
        map.put("category", "PATH_TRAVERSAL");
        map.put("potName","Web Threats Pot");
        map.put("payload", payload);
        map.put("date", LocalDateTime.now());
        return map;
    }

    public void post(Map<String,Object> map, HttpHeaders headers){
        String url = "http://localhost:8080/suspicious/server";
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(map, headers);
        this.restTemplate.postForEntity(url, entity, SuspiciousActivity.class);
    }

    public void postSuspiciousActivity(BruteForceRequest bruteForceRequest){
        try{
            HttpHeaders headers = generateHeaders();
            Map<String, Object> body = generateBody(bruteForceRequest);
            post(body,headers);
        } catch (Exception error){
            log.error("Error while posting suspicious activity: {}", error.getMessage());
        }
    }

    public void postSuspiciousActivity(PathTraversalRequest pathTraversalRequest){
        try{
            HttpHeaders headers = generateHeaders();
            Map<String, Object> body = generateBody(pathTraversalRequest);
            post(body,headers);
        }catch (Exception error){
            log.error("Error while posting suspicious activity: {}", error.getMessage());
        }
    }
}
