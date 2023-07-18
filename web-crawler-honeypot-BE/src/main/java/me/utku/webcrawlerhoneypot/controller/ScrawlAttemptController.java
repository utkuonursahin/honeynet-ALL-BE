package me.utku.webcrawlerhoneypot.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import me.utku.webcrawlerhoneypot.dto.GenericResponse;
import me.utku.webcrawlerhoneypot.dto.ScrawlAttempt;
import me.utku.webcrawlerhoneypot.service.ScrawlAttemptService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/scrawl-attempt")
@RequiredArgsConstructor
public class ScrawlAttemptController {
    private final ScrawlAttemptService scrawlAttemptService;

    @PostMapping
    public GenericResponse<ScrawlAttempt> createScrawlAttempt(@RequestBody ScrawlAttempt scrawlAttempt, HttpServletRequest httpServletRequest){
        ScrawlAttempt createdScrawlAttempt = scrawlAttemptService.create(scrawlAttempt,httpServletRequest);
        return GenericResponse.<ScrawlAttempt>builder().data(createdScrawlAttempt).statusCode(201).build();
    }
}