package me.utku.webThreatsHoneypotBE.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class PathResponse {
    private byte[] imageResponse;
    private String textResponse;
}
