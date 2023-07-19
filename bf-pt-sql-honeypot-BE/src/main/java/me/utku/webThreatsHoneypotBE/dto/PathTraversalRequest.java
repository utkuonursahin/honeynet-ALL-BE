package me.utku.webThreatsHoneypotBE.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class PathTraversalRequest {
    private String origin;
    private String payloadPath;
}
