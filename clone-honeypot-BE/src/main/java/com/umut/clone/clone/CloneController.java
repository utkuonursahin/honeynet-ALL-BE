package com.umut.clone.clone;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/clone")
public class CloneController {
    private final CloneService cloneService;

    @GetMapping()
    public String cloneHtml(@RequestParam(value = "url",required = true) String url, HttpServletRequest httpServletRequest) {
        cloneService.init();
        cloneService.completeClone(url);
        return "OK";
    }
}
