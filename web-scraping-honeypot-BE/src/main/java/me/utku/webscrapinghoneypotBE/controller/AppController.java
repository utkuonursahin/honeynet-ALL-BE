package me.utku.webscrapinghoneypotBE.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import me.utku.webscrapinghoneypotBE.service.AppService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/shutdown")
@RequiredArgsConstructor
public class AppController {
    private final AppService appService;

    @PostMapping
    public void shutdown(HttpServletRequest httpServletRequest) {
        appService.shutdown(httpServletRequest);
    }
}
