package me.utku.honeynet.service;

import lombok.extern.slf4j.Slf4j;
import me.utku.honeynet.dto.EmailListener;
import me.utku.honeynet.dto.EmailSetupRequest;
import me.utku.honeynet.dto.GenericResponse;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

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
            return new HttpHeaders();
        }
    }

    public Map<String,Object> generateBody(EmailSetupRequest emailSetupRequest){
        Map<String,Object> map = new HashMap<>();
        map.put("email", emailSetupRequest.getEmail());
        map.put("password", emailSetupRequest.getPassword());
        return map;
    }

    public List<EmailListener> get(HttpHeaders headers){
        String url = "http://localhost:8083/email-listener";
        HttpEntity<String> request = new HttpEntity<>(headers);
        return this.restTemplate.getForEntity(url, List.class).getBody();
    }

    public EmailListener post(Map<String,Object> body, HttpHeaders headers){
        String url = "http://localhost:8083/email-listener";
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
        return this.restTemplate.postForEntity(url,request, EmailListener.class).getBody();
    }

    public Boolean delete(String id,HttpHeaders headers){
        String url = "http://localhost:8083/email-listener"+"/"+id;
        HttpEntity<String> request = new HttpEntity<>(headers);
        this.restTemplate.exchange(url, HttpMethod.DELETE,request,Void.class);
        return true;
    }

    public List<EmailListener> forwardGetALlEmailListeners(){
        List<EmailListener> emailListeners = new ArrayList<>();
        try{
            HttpHeaders headers = generateHeaders();
            emailListeners = get(headers);
        } catch (Exception error){
            log.error("Error while forwarding get all email listeners request: {}", error.getMessage());
        }
        return emailListeners;
    }

    public EmailListener forwardSetupEmailListener(EmailSetupRequest emailSetupRequest){
        try{
            HttpHeaders headers = generateHeaders();
            Map<String, Object> body = generateBody(emailSetupRequest);
            return post(body,headers);
        } catch (Exception error){
            log.error("Error while forwarding email listener setup request: {}", error.getMessage());
            return null;
        }
    }

    public Boolean forwardDeleteEmailListener(String id){
        try{
            HttpHeaders headers = generateHeaders();
            return delete(id,headers);
        } catch (Exception error){
            log.error("Error while forwarding delete all email listeners request: {}", error.getMessage());
            return false;
        }
    }
}
