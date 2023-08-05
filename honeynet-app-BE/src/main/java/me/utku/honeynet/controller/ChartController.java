package me.utku.honeynet.controller;

import lombok.RequiredArgsConstructor;
import me.utku.honeynet.dto.GenericResponse;
import me.utku.honeynet.dto.SuspiciousActivityGroupByCategoryDTO;
import me.utku.honeynet.dto.SuspiciousActivityGroupByOriginDTO;
import me.utku.honeynet.service.ChartService;
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
    public GenericResponse<List<SuspiciousActivityGroupByCategoryDTO>> groupBySuspiciousCategories(@RequestParam String dateAfter) {
        List<SuspiciousActivityGroupByCategoryDTO> result = chartService.getGroupedSuspiciousActivities(dateAfter);
        return GenericResponse.<List<SuspiciousActivityGroupByCategoryDTO>>builder().data(result).statusCode(200).build();
    }

    @GetMapping("/group-by-suspicious-origins")
    public GenericResponse<List<SuspiciousActivityGroupByOriginDTO>> groupBySuspiciousOrigins(@RequestParam String since){
        List<SuspiciousActivityGroupByOriginDTO> result = chartService.getGroupedSuspiciousActivitiesByOrigin(since);
        return GenericResponse.<List<SuspiciousActivityGroupByOriginDTO>>builder().data(result).statusCode(200).build();
    }
}
