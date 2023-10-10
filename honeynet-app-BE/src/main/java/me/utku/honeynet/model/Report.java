package me.utku.honeynet.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.utku.honeynet.dto.report.ReportCategory;
import me.utku.honeynet.dto.report.ReportCountry;
import me.utku.honeynet.dto.report.ReportSource;
import me.utku.honeynet.enums.ReportContains;
import org.springframework.data.annotation.TypeAlias;
import java.time.Instant;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@TypeAlias("Report")
public class Report extends Base{

    private ReportContains[] reportContains;
    private List<ReportCategory> reportCategory;
    private List<ReportCountry> reportCountry;
    private List<ReportSource> reportSource;
    private Date reportInitDate;
    private String reportPath;

}
