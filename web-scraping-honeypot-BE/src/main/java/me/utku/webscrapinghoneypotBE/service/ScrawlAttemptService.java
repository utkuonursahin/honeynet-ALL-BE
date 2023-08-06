package me.utku.webscrapinghoneypotBE.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.utku.webscrapinghoneypotBE.dto.Origin;
import me.utku.webscrapinghoneypotBE.dto.ScrawlAttempt;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScrawlAttemptService {
    private final RestService restService;

    public ScrawlAttempt create(ScrawlAttempt newScrawlAttempt, HttpServletRequest httpServletRequest){
        try{
            newScrawlAttempt.setOrigin(new Origin(httpServletRequest.getRemoteAddr(),httpServletRequest.getLocale().getISO3Country()));
            restService.postSuspiciousActivity(newScrawlAttempt);
        }catch (Exception error){
            log.error("ScrawlAttempt service save exception: {}", error.getMessage());
        }
        return newScrawlAttempt;
    }
}
