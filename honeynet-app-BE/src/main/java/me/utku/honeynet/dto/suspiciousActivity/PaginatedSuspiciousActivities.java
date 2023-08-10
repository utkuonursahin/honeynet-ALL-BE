package me.utku.honeynet.dto.suspiciousActivity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.utku.honeynet.model.SuspiciousActivity;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaginatedSuspiciousActivities {
    private List<SuspiciousActivity> activityList;
    private Long currentPage;
    private Long currentSize;
    private Long totalSize;
    private Long totalPage;
}
