package me.utku.honeynet.controller;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
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
  public GenericResponse<User> getUser(@PathVariable String id){
    User user = userService.get(id);
    return GenericResponse.<User>builder().data(user).statusCode(200).build();
  }

  @PostMapping()
  public GenericResponse<User> createUser(@RequestBody User newUser){
    User user = userService.create(newUser);
    return GenericResponse.<User>builder().data(user).statusCode(200).build();
  }

  @PatchMapping("/{id}")
  public GenericResponse<User> updateUser(@PathVariable String id, @RequestBody User reqBody){
    User user = userService.update(id,reqBody);
    return GenericResponse.<User>builder().data(user).statusCode(200).build();
  }

  @DeleteMapping("/{id}")
  public GenericResponse<Boolean> deleteUser(@PathVariable String id){
    boolean result = userService.delete(id);
    return GenericResponse.<Boolean>builder().data(result).statusCode(200).build();
  }

  @GetMapping("/who-am-i")
  public GenericResponse<User> me(HttpSession session,
                                               HttpServletRequest request,
                                               @AuthenticationPrincipal CustomUserDetails userDetails){
    String sessionId = session.getId();
    Cookie [] cookies = request.getCookies();
    String cookie = cookies[0].getValue();
    User user = userService.get(userDetails.getId());
    if(sessionId.equals(cookie)){
      return GenericResponse.<User>builder().data(user).statusCode(200).build();
    } else {
      return GenericResponse.<User>builder().data(null).statusCode(401).build();
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
  public GenericResponse<User> findUserToSwitch(@RequestParam String firmId){
    User user = userService.getByFirmId(firmId);
    return GenericResponse.<User>builder().data(user).statusCode(200).build();
  }

}