package me.utku.honeynet.controller;

import lombok.RequiredArgsConstructor;
import me.utku.honeynet.dto.GenericResponse;
import me.utku.honeynet.dto.suspiciousActivity.PaginatedSuspiciousActivities;
import me.utku.honeynet.dto.suspiciousActivity.SuspiciousActivityFilter;
import me.utku.honeynet.dto.security.CustomUserDetails;
import me.utku.honeynet.model.SuspiciousActivity;
import me.utku.honeynet.service.SuspiciousActivityService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/suspicious/client")
@RequiredArgsConstructor
public class SuspiciousActivityClientController {
    private final SuspiciousActivityService suspiciousActivityService;

    /*@GetMapping
    public GenericResponse<PaginatedSuspiciousActivities> getActivities(@RequestParam(defaultValue = "0") int page,
                                                                        @RequestParam(defaultValue = "20") int size) {
        PaginatedSuspiciousActivities activities = suspiciousActivityService.getAllActivities(page, size);
        return GenericResponse.<PaginatedSuspiciousActivities>builder().data(activities).statusCode(200).build();
    }*/

    @GetMapping("/{id}")
    public GenericResponse<SuspiciousActivity> getActivity(@PathVariable String id) {
        SuspiciousActivity suspiciousActivity = suspiciousActivityService.getActivityById(id);
        return GenericResponse.<SuspiciousActivity>builder().data(suspiciousActivity).statusCode(200).build();
    }

    @GetMapping("/all")
    public GenericResponse<List<SuspiciousActivity>> getAllActivities(){
        List<SuspiciousActivity> suspiciousActivities = suspiciousActivityService.getAllSuspiciousActivities();
        return GenericResponse.<List<SuspiciousActivity>>builder().data(suspiciousActivities).statusCode(200).build();
    }



    @PostMapping("/filter")
    public GenericResponse<PaginatedSuspiciousActivities> getFilteredActivities(
        @AuthenticationPrincipal CustomUserDetails userDetails,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size,
        @RequestBody SuspiciousActivityFilter suspiciousActivityFilter) {
        PaginatedSuspiciousActivities activities = suspiciousActivityService.filterActivities(userDetails.getFirmRef(), suspiciousActivityFilter, page, size);
        return GenericResponse.<PaginatedSuspiciousActivities>builder().data(activities).statusCode(200).build();
    }
}
