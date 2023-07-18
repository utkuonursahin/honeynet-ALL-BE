package me.utku.honeynet.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import me.utku.honeynet.dto.GenericResponse;
import me.utku.honeynet.enums.PotCategory;
import me.utku.honeynet.model.SuspiciousActivity;
import me.utku.honeynet.service.SuspiciousActivityService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/suspicious/client")
@RequiredArgsConstructor
public class SuspiciousActivityClientController {
    private final SuspiciousActivityService suspiciousActivityService;

    @GetMapping
    public GenericResponse<List<SuspiciousActivity>> getActivities(HttpServletRequest httpServletRequest) {
        List<SuspiciousActivity> activities = suspiciousActivityService.getAllActivities(httpServletRequest);
        return GenericResponse.<List<SuspiciousActivity>>builder().data(activities).statusCode(200).build();
    }

    @GetMapping("/{id}")
    public GenericResponse<SuspiciousActivity> getActivity(@PathVariable String id, HttpServletRequest httpServletRequest) {
        SuspiciousActivity suspiciousActivity = suspiciousActivityService.getActivityById(id,httpServletRequest );
        return GenericResponse.<SuspiciousActivity>builder().data(suspiciousActivity).statusCode(200).build();
    }

    @GetMapping("/category/{category}")
    public GenericResponse<List<SuspiciousActivity>> getActivityByCategory(@PathVariable PotCategory category, HttpServletRequest httpServletRequest) {
        List<SuspiciousActivity> suspiciousActivitiesByCategory = suspiciousActivityService.getActivitiesByCategory(category,httpServletRequest );
        return GenericResponse.<List<SuspiciousActivity>>builder().data(suspiciousActivitiesByCategory).statusCode(200).build();
    }

    @GetMapping("/date/{start}/{end}")
    public GenericResponse<List<SuspiciousActivity>> getActivityByDate(@PathVariable String start, @PathVariable String end, HttpServletRequest httpServletRequest) {
        List<SuspiciousActivity> suspiciousActivitiesByDate = suspiciousActivityService.getActivitiesByDateBetween(start, end, httpServletRequest);
        return GenericResponse.<List<SuspiciousActivity>>builder().data(suspiciousActivitiesByDate).statusCode(200).build();
    }
}
