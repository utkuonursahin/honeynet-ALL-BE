package me.utku.webcrawlerhoneypot.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class ScrawlAttempt{
    private String origin;
    private String targetElementId;
}
