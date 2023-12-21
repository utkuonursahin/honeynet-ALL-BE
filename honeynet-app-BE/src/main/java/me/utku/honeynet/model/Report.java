package me.utku.honeynet.model;

import lombok.*;
import me.utku.honeynet.dto.chart.SuspiciousActivityGroupByCategoryDTO;
import me.utku.honeynet.dto.chart.SuspiciousActivityGroupByOriginCountryDTO;
import me.utku.honeynet.dto.chart.SuspiciousActivityGroupByOriginSourceDTO;
import org.springframework.data.annotation.TypeAlias;

import java.time.Instant;
import java.util.List;

@Data
@TypeAlias("Report")
@AllArgsConstructor
public class Report extends Base{
    private List<SuspiciousActivityGroupByCategoryDTO> reportCategory;
    private List<SuspiciousActivityGroupByOriginCountryDTO> reportCountry;
    private List<SuspiciousActivityGroupByOriginSourceDTO> reportSource;
    private Instant createdAt;
    private String reportPath;
    private String reportCoverPath;
    private String firmRef;
}
