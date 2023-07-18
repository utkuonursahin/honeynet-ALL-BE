package me.utku.honeynet.service;

import lombok.extern.slf4j.Slf4j;
import me.utku.honeynet.dto.EmailSetupRequest;
import me.utku.honeynet.dto.GenericResponse;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

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

    public Map<String,Object> generateBody(EmailSetupRequest emailSetupRequest){
        Map<String,Object> map = new HashMap<>();
        map.put("email", emailSetupRequest.getEmail());
        map.put("password", emailSetupRequest.getPassword());
        return map;
    }

    public void post(Map<String,Object> body, HttpHeaders headers){
        String url = "http://localhost:8083/email";
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
        this.restTemplate.postForEntity(url, entity, GenericResponse.class);
    }

    public void forwardEmailListenerSetupReq(EmailSetupRequest emailSetupRequest){
        try{
            HttpHeaders headers = generateHeaders();
            Map<String, Object> body = generateBody(emailSetupRequest);
            post(body,headers);
        } catch (Exception error){
            log.error("Error while forwarding email listener setup request: {}", error.getMessage());
        }
    }
}
