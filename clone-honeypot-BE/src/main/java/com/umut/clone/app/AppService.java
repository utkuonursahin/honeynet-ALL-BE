package com.umut.clone.app;

import com.umut.clone.jwtservice.JWTService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class AppService {
    private static final String TOKEN_HEADER = "In-App-Auth-Token";
    private final JWTService jwtService;
    public void shutdown(HttpServletRequest httpServletRequest) {
        try{
            String authToken = httpServletRequest.getHeader(TOKEN_HEADER);
            if(authToken != null && jwtService.validateJWT(authToken)){
                log.info("Shutting down the application...");
                System.exit(0);
            } else {
                log.error("Unauthorized shutdown request! WHO THE HACK ARE YOU!!\n origin: {}",httpServletRequest.getRemoteAddr());
            }
        } catch (Exception error){
            log.error("App service shutdown exception: {}",error.getMessage());
        }

    }
}
