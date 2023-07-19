package me.utku.webThreatsHoneypotBE.service;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.utku.webThreatsHoneypotBE.dto.AuthResponse;
import me.utku.webThreatsHoneypotBE.model.BruteForceRequest;
import me.utku.webThreatsHoneypotBE.model.User;
import me.utku.webThreatsHoneypotBE.repository.BruteForceRequestRepository;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class BruteForceRequestService {
    private final BruteForceRequestRepository bruteForceRequestRepository;
    private final RestService restService;

    public ResponseCookie generateCookie(){
        return ResponseCookie.from("authenticated","true")
            .secure(true).path("/")
            .maxAge(60 * 60 * 24 * 7)
            .sameSite("None").build();
    }

    public AuthResponse fakeAuthenticationSuccessResponse(){
        return AuthResponse.builder()
            .authenticated(true)
            .message("User authenticated!")
            .user(new User("Jackson Moore","1a7vdf0w@1234$cıf039481eur£#$9","ADMIN",""))
            .build();
    }
    public AuthResponse fakeAuthenticationFailResponse(){
        return AuthResponse.builder()
            .authenticated(false)
            .message("No username/password found")
            .user(null)
            .build();
    }

    public AuthResponse handleBruteForceLoginAttempt(String reqIp, String username, String password, HttpServletResponse httpServletResponse){
        int max = 5;
        int min = 1;
        int randomBetweenMinMax = (int)((Math.random() * (max - min)) + min);
        ResponseCookie cookie =  this.generateCookie();
        BruteForceRequest existReq = this.getByOrigin(reqIp);

        if(existReq == null){
            this.create(new BruteForceRequest(reqIp, randomBetweenMinMax,1, null, null, null));
            return fakeAuthenticationFailResponse();
        } else if(username.equals(existReq.getPayloadUsername()) && password.equals(existReq.getPayloadPassword())){
            httpServletResponse.setHeader(HttpHeaders.SET_COOKIE, cookie.toString());
            return fakeAuthenticationSuccessResponse();
        } else {
            existReq.setTimesAccessed(existReq.getTimesAccessed() + 1);
            existReq = this.update(existReq.getId(),existReq);
            if(existReq.getTimesAccessed() == existReq.getRandAuthenticateNumber()){
                existReq.setPayloadUsername(username);
                existReq.setPayloadPassword(password);
                this.update(existReq.getId(),existReq);
                httpServletResponse.setHeader(HttpHeaders.SET_COOKIE, cookie.toString());
                restService.postSuspiciousActivity(existReq);
                return fakeAuthenticationSuccessResponse();
            } else {
                return fakeAuthenticationSuccessResponse();
            }
        }
    }

    public List<BruteForceRequest> getAll(){
        List<BruteForceRequest> bruteForceRequests = new ArrayList<BruteForceRequest>();
        try{
            bruteForceRequests = bruteForceRequestRepository.findAll();
        }catch (Exception error){
            log.error("BruteForceRequestService getAll exception: {}",error.getMessage());
        }
        return bruteForceRequests;
    }

    public BruteForceRequest getById(String id){
        BruteForceRequest bruteForceRequest = new BruteForceRequest();
        try{
            bruteForceRequest = bruteForceRequestRepository.findById(id).orElse(null);
        }catch (Exception error){
            log.error("BruteForceRequestService get exception: {}",error.getMessage());
        }
        return bruteForceRequest;
    }

    public BruteForceRequest getByOrigin(String ip){
        BruteForceRequest bruteForceRequest = new BruteForceRequest();
        try{
            bruteForceRequest = bruteForceRequestRepository.findByOrigin(ip);
        }catch (Exception error){
            log.error("BruteForceRequestService get exception: {}",error.getMessage());
        }
        return bruteForceRequest;
    }

    public BruteForceRequest create(BruteForceRequest newBruteForceRequest){
        BruteForceRequest bruteForceRequest = new BruteForceRequest();
        try{
            bruteForceRequest = bruteForceRequestRepository.save(newBruteForceRequest);
        }catch (Exception error){
            log.error("BruteForceRequestService create exception: {}",error.getMessage());
        }
        return bruteForceRequest;
    }

    public BruteForceRequest update(String id, BruteForceRequest updatedParts){
        BruteForceRequest existingBruteForceRequest = new BruteForceRequest();
        try{
            existingBruteForceRequest = bruteForceRequestRepository.findById(id).orElse(null);
            if(existingBruteForceRequest == null) throw new Exception("No ip address found with given id");
            if(updatedParts.getTimesAccessed() != null){
                existingBruteForceRequest.setTimesAccessed(updatedParts.getTimesAccessed());
            }
            if(updatedParts.getPayloadUsername() != null){
                existingBruteForceRequest.setPayloadUsername(updatedParts.getPayloadUsername());
            }
            if(updatedParts.getPayloadPassword() != null){
                existingBruteForceRequest.setPayloadPassword(updatedParts.getPayloadPassword());
            }
            bruteForceRequestRepository.save(existingBruteForceRequest);
        }catch (Exception error){
            log.error("BruteForceRequestService update exception: {}",error.getMessage());
        }
        return existingBruteForceRequest;
    }

    public boolean delete(String id){
        boolean isDeleted = false;
        try{
            bruteForceRequestRepository.deleteById(id);
            isDeleted = true;

        }catch (Exception error){
            log.error("BruteForceRequestService delete exception: {}",error.getMessage());
        }
        return isDeleted;
    }
}
