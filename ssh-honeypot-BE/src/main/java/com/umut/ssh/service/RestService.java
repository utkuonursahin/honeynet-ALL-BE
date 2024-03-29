package com.umut.ssh.service;

import com.umut.ssh.suspiciousactivity.Origin;
import com.umut.ssh.suspiciousactivity.SuspiciousActivity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

@Service
@Slf4j
public class RestService {
    private final RestTemplate restTemplate;
    private final JWTService jwtService;
    private String firmId;

    public RestService(RestTemplateBuilder restTemplateBuilder, JWTService jwtService){
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
            BufferedReader reader = new BufferedReader(new FileReader("C:\\Users\\Utku\\Personal\\Projects\\Java Projects\\honeynet-ALL-BE\\ssh-honeypot-BE\\src\\main\\resources\\firmId.txt"));
            firmId = reader.readLine();
            reader.close();
            HttpHeaders headers = generateHeaders();
            Map<String, Object> body = generateBody(origin,time,msg);
            post(body,headers);
        } catch (Exception error){
            log.error("Error while posting suspicious activity: {}", error.getMessage());
        }
    }
}