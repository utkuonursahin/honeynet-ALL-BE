package com.umut.clone.clone;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/clone")
public class CloneController {
    private final CloneService cloneService;

    @PostMapping()
    public void cloneHtml(@RequestBody CloneRequest cloneRequest) {
        cloneService.init();
        cloneService.completeClone(cloneRequest.cloneUrl());
    }
}
