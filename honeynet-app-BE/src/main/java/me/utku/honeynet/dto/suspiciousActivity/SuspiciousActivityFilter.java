package me.utku.honeynet.dto.suspiciousActivity;

import lombok.AllArgsConstructor;
import lombok.Data;
import me.utku.honeynet.dto.Origin;
import me.utku.honeynet.enums.PotCategory;

import java.time.Instant;
import java.util.List;

@Data
@AllArgsConstructor
public class SuspiciousActivityFilter {
    private Origin originFilter;
    private List<PotCategory> categoryFilters;
    private Instant[] dateFilters;
    private int page;
}
