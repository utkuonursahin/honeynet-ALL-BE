package me.utku.honeynet.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.utku.honeynet.dto.SuspiciousActivityGroupByCategoryDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChartService {
    private final SuspiciousActivityService suspiciousActivityService;

    public List<SuspiciousActivityGroupByCategoryDTO> getGroupedSuspiciousActivities(String firmId) {
        return suspiciousActivityService.groupAndCountSuspiciousActivitiesByCategory(firmId);
    }

}
