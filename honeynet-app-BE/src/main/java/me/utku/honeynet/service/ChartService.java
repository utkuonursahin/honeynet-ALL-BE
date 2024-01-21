package me.utku.honeynet.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.utku.honeynet.dto.chart.ServerInfoGroupByStatusDTO;
import me.utku.honeynet.dto.chart.SuspiciousActivityGroupByCategoryDTO;
import me.utku.honeynet.dto.chart.SuspiciousActivityGroupByOriginCountryDTO;
import me.utku.honeynet.dto.chart.SuspiciousActivityGroupByOriginSourceDTO;
import org.springframework.stereotype.Service;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChartService {
    private final SuspiciousActivityService suspiciousActivityService;
    private final ServerInstanceService serverInstanceService;

    public List<SuspiciousActivityGroupByCategoryDTO> getGroupedSuspiciousActivitiesByCategory(String since, String firmRef) {
        return suspiciousActivityService.groupAndCountSuspiciousActivitiesByCategory(since, firmRef);
    }

    public List<SuspiciousActivityGroupByOriginSourceDTO> getGroupedSuspiciousActivitiesByOriginSource(String since, String firmRef){
        return suspiciousActivityService.groupAndCountSuspiciousActivitiesByOriginSource(since, firmRef);
    }

    public List<SuspiciousActivityGroupByOriginCountryDTO> getGroupedSuspiciousActivitiesByOriginCountry(String since, String firmRef){
        return suspiciousActivityService.groupAndCountSuspiciousActivitiesByOriginCountry(since, firmRef);
    }

    public List<ServerInfoGroupByStatusDTO> getGroupedServerInfoByStatus(String firmRef){
        return serverInstanceService.groupAndCountServerInfoByStatus(firmRef);
    }
}
