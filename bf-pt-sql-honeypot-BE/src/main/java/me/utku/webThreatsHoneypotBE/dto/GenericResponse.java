package me.utku.webThreatsHoneypotBE.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class GenericResponse<T> {
    protected int statusCode;
    protected T data;
}
