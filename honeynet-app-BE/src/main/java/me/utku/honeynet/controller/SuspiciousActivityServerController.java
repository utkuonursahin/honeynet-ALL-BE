package me.utku.honeynet.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import me.utku.honeynet.dto.GenericResponse;
import me.utku.honeynet.model.SuspiciousActivity;
import me.utku.honeynet.service.SuspiciousActivityService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/suspicious/server")
@RequiredArgsConstructor
public class SuspiciousActivityServerController {
    private final SuspiciousActivityService suspiciousActivityService;

    @PostMapping
    public GenericResponse<SuspiciousActivity> createActivity(@RequestBody SuspiciousActivity newSuspiciousActivity, HttpServletRequest httpServletRequest) {
        SuspiciousActivity suspiciousActivity = suspiciousActivityService.createActivity(newSuspiciousActivity, httpServletRequest );
        return GenericResponse.<SuspiciousActivity>builder().data(suspiciousActivity).statusCode(200).build();
    }

    @PatchMapping("/{id}")
    public GenericResponse<SuspiciousActivity> updateActivity(@PathVariable String id, @RequestBody SuspiciousActivity reqBody, HttpServletRequest httpServletRequest) {
        SuspiciousActivity suspiciousActivity = suspiciousActivityService.updateActivity(id, reqBody ,httpServletRequest );
        return GenericResponse.<SuspiciousActivity>builder().data(suspiciousActivity).statusCode(200).build();
    }

    @DeleteMapping("/{id}")
    public GenericResponse<Boolean> deleteActivity(@PathVariable String id, HttpServletRequest httpServletRequest ) {
        boolean result = suspiciousActivityService.deleteActivity(id, httpServletRequest);
        return GenericResponse.<Boolean>builder().data(result).statusCode(200).build();
    }
}
