package me.utku.honeynet.dto.suspiciousActivity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.utku.honeynet.dto.Origin;
import me.utku.honeynet.enums.PotCategory;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SuspiciousActivityFilter {
    private Origin originFilter;
    private List<PotCategory> categoryFilters;
    private Instant[] dateFilters;
    private int page;

    public static SuspiciousActivityFilter createWithDefaults() {
        SuspiciousActivityFilter filter = new SuspiciousActivityFilter();
        filter.setOriginFilter(new Origin("", "", "", ""));
        filter.setCategoryFilters(Collections.emptyList());
        filter.setDateFilters(new Instant[]{Instant.MIN, Instant.MAX});
        filter.setPage(0);
        return filter;
    }
}
