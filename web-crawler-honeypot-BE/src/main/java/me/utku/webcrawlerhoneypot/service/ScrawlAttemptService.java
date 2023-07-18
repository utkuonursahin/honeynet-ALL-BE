package me.utku.webcrawlerhoneypot.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.utku.webcrawlerhoneypot.dto.ScrawlAttempt;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScrawlAttemptService {
    private final RestService restService;

    public ScrawlAttempt create(ScrawlAttempt newScrawlAttempt, HttpServletRequest httpServletRequest){
        try{
            String ipAddress = httpServletRequest.getRemoteAddr();
            newScrawlAttempt.setOrigin(ipAddress);
            restService.postSuspiciousActivity(newScrawlAttempt);
        }catch (Exception error){
            log.error("ScrawlAttempt service save exception: {}", error.getMessage());
        }
        return newScrawlAttempt;
    }
}
