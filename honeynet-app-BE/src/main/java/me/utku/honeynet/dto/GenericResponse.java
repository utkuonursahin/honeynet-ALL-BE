package me.utku.honeynet.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Builder
@Data
public class GenericResponse<T> {
  protected int statusCode;
  protected T data;
}