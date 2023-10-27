package me.utku.honeynet.dto.report;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReportSettings {
    private Boolean byCategory;
    private Boolean byCountry;
    private Boolean bySource;
}
