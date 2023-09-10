package me.utku.honeynet.dto.report;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReportFilter {
    private Boolean categoryFilter;
    private Boolean countryFilter;
    private Boolean sourceFilter;
    private Boolean dateFilter;
}
