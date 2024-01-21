package me.utku.honeynet.dto.email;

import lombok.AllArgsConstructor;
import lombok.Data;
import me.utku.honeynet.model.EmailInfo;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class EmailInfoFilter {
    private String receiverFilter;
    private LocalDateTime[] dateFilters;
}
