package me.utku.honeynet.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import me.utku.honeynet.enums.PotCategory;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class SuspiciousActivityFilter {
    private Origin originFilter;
    private List<PotCategory> categoryFilters;
    private LocalDateTime[] dateFilters;
    private int page;
}
