package me.utku.honeynet.dto.user;

public record UserUpdateDTO(String username, String email, String oldPassword, String newPassword, String passwordConfirm) { }
