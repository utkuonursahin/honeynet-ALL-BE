package me.utku.honeynet.controller;
import me.utku.honeynet.dto.user.UserResponseDTO;
import me.utku.honeynet.dto.user.UserUpdateDTO;
import me.utku.honeynet.dto.user.UserUpdateResponseDTO;
import me.utku.honeynet.dto.security.CustomUserDetails;
import me.utku.honeynet.dto.GenericResponse;
import me.utku.honeynet.model.User;
import me.utku.honeynet.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/user")

public class UserController {
  private final UserService userService;

  @GetMapping
  public GenericResponse<List<User>> getUsers(){
    List<User> users = userService.getAll();
    return GenericResponse.<List<User>>builder().data(users).statusCode(200).build();
  }
  @GetMapping("/{id}")
  public GenericResponse<UserResponseDTO> getUser(@PathVariable String id){
    UserResponseDTO userResponseDTO = userService.get(id);
    return GenericResponse.<UserResponseDTO>builder().data(userResponseDTO).statusCode(200).build();
  }

  @PostMapping()
  public GenericResponse<UserResponseDTO> createUser(@RequestBody User newUser){
    UserResponseDTO userResponseDTO = userService.create(newUser);
    return GenericResponse.<UserResponseDTO>builder().data(userResponseDTO).statusCode(200).build();
  }

  @PatchMapping("/{id}")
  public GenericResponse<UserUpdateResponseDTO> updateUser(@PathVariable String id, @RequestBody UserUpdateDTO reqBody){
    UserUpdateResponseDTO userUpdateResponseDTO = userService.update(id,reqBody);
    return GenericResponse.<UserUpdateResponseDTO>builder().data(userUpdateResponseDTO).statusCode(userUpdateResponseDTO.statusCode()).build();
  }

  @DeleteMapping("/{id}")
  public GenericResponse<Boolean> deleteUser(@PathVariable String id){
    boolean result = userService.delete(id);
    return GenericResponse.<Boolean>builder().data(result).statusCode(200).build();
  }

  @GetMapping("/who-am-i")
  public GenericResponse<UserResponseDTO> me(@AuthenticationPrincipal CustomUserDetails userDetails){
    if(userDetails != null){
      UserResponseDTO userResponseDTO = userService.get(userDetails.getId());
      return GenericResponse.<UserResponseDTO>builder().data(userResponseDTO).statusCode(200).build();
    } else {
      return GenericResponse.<UserResponseDTO>builder().data(null).statusCode(401).build();
    }
  }

  @GetMapping("/is-authenticated")
  public GenericResponse<Boolean> isAuthenticated(Authentication authentication){
    if(authentication != null){
      return GenericResponse.<Boolean>builder().data(true).statusCode(200).build();
    } else {
      return GenericResponse.<Boolean>builder().data(false).statusCode(401).build();
    }
  }

  @GetMapping("/find-user-to-switch")
  public GenericResponse<UserResponseDTO> findUserToSwitch(@RequestParam String firmId){
    UserResponseDTO userResponseDTO = userService.getByFirm(firmId);
    return GenericResponse.<UserResponseDTO>builder().data(userResponseDTO).statusCode(200).build();
  }
}