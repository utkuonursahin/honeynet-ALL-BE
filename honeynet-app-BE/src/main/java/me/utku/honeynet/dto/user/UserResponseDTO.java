package me.utku.honeynet.dto.user;

import me.utku.honeynet.enums.UserRole;

public record UserResponseDTO(String id, String username, String email, UserRole role, String firmRef, String firmName) { }
