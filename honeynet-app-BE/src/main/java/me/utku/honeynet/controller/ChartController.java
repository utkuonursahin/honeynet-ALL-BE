package me.utku.honeynet.controller;

import lombok.RequiredArgsConstructor;
import me.utku.honeynet.dto.GenericResponse;
import me.utku.honeynet.dto.chart.ServerInfoGroupByStatusDTO;
import me.utku.honeynet.dto.chart.SuspiciousActivityGroupByCategoryDTO;
import me.utku.honeynet.dto.chart.SuspiciousActivityGroupByOriginCountryDTO;
import me.utku.honeynet.dto.chart.SuspiciousActivityGroupByOriginSourceDTO;
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

    @GetMapping("/group-by-suspicious-origin-sources")
    public GenericResponse<List<SuspiciousActivityGroupByOriginSourceDTO>> groupBySuspiciousOriginSources(@RequestParam String since, @AuthenticationPrincipal CustomUserDetails userDetails){
        List<SuspiciousActivityGroupByOriginSourceDTO> result = chartService.getGroupedSuspiciousActivitiesByOriginSource(since, userDetails.getFirmRef());
        return GenericResponse.<List<SuspiciousActivityGroupByOriginSourceDTO>>builder().data(result).statusCode(200).build();
    }

    @GetMapping("/group-by-suspicious-origin-countries")
    public GenericResponse<List<SuspiciousActivityGroupByOriginCountryDTO>> groupBySuspiciousOriginCountries(@RequestParam String since, @AuthenticationPrincipal CustomUserDetails userDetails){
        List<SuspiciousActivityGroupByOriginCountryDTO> result = chartService.getGroupedSuspiciousActivitiesByOriginCountry(since, userDetails.getFirmRef());
        return GenericResponse.<List<SuspiciousActivityGroupByOriginCountryDTO>>builder().data(result).statusCode(200).build();
    }

    @GetMapping("/group-by-server-status")
    public GenericResponse<List<ServerInfoGroupByStatusDTO>> groupByServerStatus(@AuthenticationPrincipal CustomUserDetails userDetails){
        List<ServerInfoGroupByStatusDTO> result = chartService.getGroupedServerInfoByStatus(userDetails.getFirmRef());
        return GenericResponse.<List<ServerInfoGroupByStatusDTO>>builder().data(result).statusCode(200).build();
    }
}
