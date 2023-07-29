package me.utku.honeynet.service;

import lombok.extern.slf4j.Slf4j;
import me.utku.honeynet.dto.EmailListener;
import me.utku.honeynet.dto.EmailSetupRequest;
import me.utku.honeynet.model.ServerInfo;
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
    private final ServerInfoService serverInfoService;
    private final RestTemplate restTemplate;
    private final JWTService jwtService;

    public RestService(RestTemplateBuilder restTemplateBuilder, JWTService jwtService, ServerInfoService serverInfoService) {
        this.restTemplate = restTemplateBuilder.build();
        this.jwtService = jwtService;
        this.serverInfoService = serverInfoService;
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

    public String findEmailListenerServerUrl(String potId,String firmId){
        ServerInfo serverInfo = serverInfoService.getByPotIdAndFirmId(potId,firmId);
        return "http://localhost:"+serverInfo.getPort()+"/email-listener";
    }

    public List<EmailListener> getEmailListeners(String url, HttpHeaders headers){
        HttpEntity<String> request = new HttpEntity<>(headers);
        return this.restTemplate.exchange(url, HttpMethod.GET, request, List.class).getBody();
    }

    public EmailListener postEmailListener(String url, Map<String,Object> body, HttpHeaders headers){
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
        return this.restTemplate.postForEntity(url,request, EmailListener.class).getBody();
    }

    public EmailListener putEmailListener(String id, String url, Map<String,Object> body, HttpHeaders headers){
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
        return this.restTemplate.exchange(url+"/"+id, HttpMethod.PUT,request, EmailListener.class).getBody();
    }

    public Boolean deleteEmailListener(String id,String url,HttpHeaders headers){
        HttpEntity<String> request = new HttpEntity<>(headers);
        this.restTemplate.exchange(url+"/"+id, HttpMethod.DELETE,request,Void.class);
        return true;
    }

    public List<EmailListener> forwardGetAllEmailListeners(String potId,String firmId){
        List<EmailListener> emailListeners = new ArrayList<>();
        try{
            HttpHeaders headers = generateHeaders();
            emailListeners = getEmailListeners(findEmailListenerServerUrl(potId,firmId),headers);
        } catch (Exception error){
            log.error("Error while forwarding get all email listeners request: {}", error.getMessage());
        }
        return emailListeners;
    }

    public EmailListener forwardCreateEmailListener(String potId, String firmId,EmailSetupRequest emailSetupRequest){
        try{
            HttpHeaders headers = generateHeaders();
            Map<String, Object> body = generateBody(emailSetupRequest);
            return postEmailListener(findEmailListenerServerUrl(potId,firmId),body,headers);
        } catch (Exception error){
            log.error("Error while forwarding email listener setup request: {}", error.getMessage());
            return null;
        }
    }

    public EmailListener forwardUpdateEmailListener(String id, String potId, String firmId, EmailListener updatePart){
        try{
            HttpHeaders headers = generateHeaders();
            Map<String,Object> body = new HashMap<>();
            body.put("status", updatePart.getStatus());
            return putEmailListener(id,findEmailListenerServerUrl(potId,firmId),body,headers);
        } catch (Exception error){
            log.error("Error while forwarding email listener update request: {}", error.getMessage());
            return null;
        }
    }

    public Boolean forwardDeleteEmailListener(String id,String potId, String firmId){
        try{
            HttpHeaders headers = generateHeaders();
            return deleteEmailListener(id,findEmailListenerServerUrl(potId,firmId),headers);
        } catch (Exception error){
            log.error("Error while forwarding delete all email listeners request: {}", error.getMessage());
            return false;
        }
    }
}
