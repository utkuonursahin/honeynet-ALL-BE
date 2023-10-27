package me.utku.honeynet.model;

import lombok.*;
import me.utku.honeynet.dto.report.ReportCategory;
import me.utku.honeynet.dto.report.ReportCountry;
import me.utku.honeynet.dto.report.ReportSource;
import org.springframework.data.annotation.TypeAlias;

import java.time.Instant;
import java.util.List;

@Data
@TypeAlias("Report")
public class Report extends Base{
    private List<ReportCategory> reportCategory;
    private List<ReportCountry> reportCountry;
    private List<ReportSource> reportSource;
    private Instant reportInitDate;
    private String reportPath;
    private String firmRef;
}
