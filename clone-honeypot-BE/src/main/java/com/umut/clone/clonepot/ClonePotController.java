package com.umut.clone.clonepot;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/clone")
public class ClonePotController {

    private final ClonePotService clonePotService;

    @GetMapping()
    public String cloneHtml(@RequestParam(value = "url",required = true) String url) {
        return clonePotService.getPage(url);
    }
}
