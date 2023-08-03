package me.utku.honeynet.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SuspiciousActivityGroupByCategoryDTO {
    private String firmRef;
    private String category;
    private Long count;
}
