package me.utku.webscrapinghoneypotBE.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import me.utku.webscrapinghoneypotBE.dto.GenericResponse;
import me.utku.webscrapinghoneypotBE.dto.ScrawlAttempt;
import me.utku.webscrapinghoneypotBE.service.ScrawlAttemptService;
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