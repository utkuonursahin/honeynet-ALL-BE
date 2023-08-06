package me.utku.webThreatsHoneypotBE.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class PathTraversalRequest {
    private Origin origin;
    private String payloadPath;
}
