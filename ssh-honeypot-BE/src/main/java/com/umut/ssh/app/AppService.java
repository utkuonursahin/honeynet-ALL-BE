package com.umut.ssh.app;

import com.umut.ssh.command.SshServerMain;
import com.umut.ssh.service.JWTService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class AppService {
    private static final String TOKEN_HEADER = "In-App-Auth-Token";
    private final JWTService jwtService;
    private final SshServerMain sshServerMain;

    public void shutdown(HttpServletRequest httpServletRequest) {
        try{
            String authToken = (httpServletRequest.getHeader(TOKEN_HEADER));
            if(authToken != null && jwtService.validateJWT(authToken)){
                    sshServerMain.sshServerStop();
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