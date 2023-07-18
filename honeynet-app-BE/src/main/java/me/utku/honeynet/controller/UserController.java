package me.utku.honeynet.controller;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import me.utku.honeynet.dto.GenericResponse;
import me.utku.honeynet.model.User;
import me.utku.honeynet.service.UserService;
import lombok.RequiredArgsConstructor;
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
  public GenericResponse<User> updateUser(@PathVariable String id, @RequestBody User reqBody) throws Exception {
    User user = userService.update(id,reqBody);
    return GenericResponse.<User>builder().data(user).statusCode(200).build();
  }

  @DeleteMapping("/{id}")
  public GenericResponse<Boolean> deleteUser(@PathVariable String id){
    boolean result = userService.delete(id);
    return GenericResponse.<Boolean>builder().data(result).statusCode(200).build();
  }

  @GetMapping("/isLoggedIn")
  public boolean whoAmI(HttpSession session, HttpServletRequest request){
    String sessionId = session.getId();
    Cookie [] cookies = request.getCookies();
    String cookie = cookies[0].getValue();
    return sessionId.equals(cookie);
  }
}