package me.utku.honeynet.dto.user;

public record UserUpdateResponseDTO(Integer statusCode, String message, UserResponseDTO user) { }
