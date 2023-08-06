package me.utku.honeynet.controller;

import lombok.RequiredArgsConstructor;
import me.utku.honeynet.dto.GenericResponse;
import me.utku.honeynet.dto.SuspiciousActivityGroupByCategoryDTO;
import me.utku.honeynet.dto.SuspiciousActivityGroupByOriginSourceDTO;
import me.utku.honeynet.dto.security.CustomUserDetails;
import me.utku.honeynet.service.ChartService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/chart")
@RequiredArgsConstructor
public class ChartController {
    private final ChartService chartService;
    @GetMapping("/group-by-suspicious-categories")
    public GenericResponse<List<SuspiciousActivityGroupByCategoryDTO>> groupBySuspiciousCategories(@RequestParam String since, @AuthenticationPrincipal CustomUserDetails userDetails) {
        List<SuspiciousActivityGroupByCategoryDTO> result = chartService.getGroupedSuspiciousActivitiesByCategory(since, userDetails.getFirmRef());
        return GenericResponse.<List<SuspiciousActivityGroupByCategoryDTO>>builder().data(result).statusCode(200).build();
    }

    @GetMapping("/group-by-suspicious-origins")
    public GenericResponse<List<SuspiciousActivityGroupByOriginSourceDTO>> groupBySuspiciousOrigins(@RequestParam String since, @AuthenticationPrincipal CustomUserDetails userDetails){
        List<SuspiciousActivityGroupByOriginSourceDTO> result = chartService.getGroupedSuspiciousActivitiesByOriginSource(since, userDetails.getFirmRef());
        return GenericResponse.<List<SuspiciousActivityGroupByOriginSourceDTO>>builder().data(result).statusCode(200).build();
    }
}
