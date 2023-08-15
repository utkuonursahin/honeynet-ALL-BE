package com.umut.clone.clone;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/clone")
@RestController
public class CloneController {
    private final CloneService cloneService;

    @PostMapping()
    public CloneResponse cloneHtml(@RequestBody CloneRequest cloneRequest) {
        cloneService.init();
        return cloneService.completeClone(cloneRequest.cloneUrl());
    }
}