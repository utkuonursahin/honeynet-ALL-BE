package com.umut.sshpot.services;

import com.umut.sshpot.suspiciousactivity.Origin;
import com.umut.sshpot.suspiciousactivity.SuspiciousActivity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.sshd.server.session.ServerSession;
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
@RequiredArgsConstructor
public class RestService {

    private final RestTemplate restTemplate;
    private final JWTService jwtService;



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

    public Map<String,Object> generateBody(Origin origin,String entryTime, String msg){
        Map<String,Object> payload = new HashMap<>();
        payload.put("entryTime",entryTime);
        payload.put("msg",msg);

        Map<String,Object> map = new HashMap<>();
        map.put("firmRef",firmId);
        map.put("origin",origin);
        map.put("payload", payload);
        map.put("category", "SSH");
        map.put("potName","SSH");
        map.put("date", LocalDateTime.now());
        return map;
    }

    public void post(Map<String,Object> body, HttpHeaders headers){
        String url = "http://localhost:8080/suspicious/server";
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
        this.restTemplate.postForEntity(url, entity, SuspiciousActivity.class);
    }

    public void postSuspiciousActivity(Origin origin,String time,String msg){
        try{
            HttpHeaders headers = generateHeaders();
            Map<String, Object> body = generateBody(origin,time,msg);
            post(body,headers);
        } catch (Exception error){
            log.error("Error while posting suspicious activity: {}", error.getMessage());
        }
    }
}