package me.utku.webThreatsHoneypotBE.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import me.utku.webThreatsHoneypotBE.dto.Folder;
import me.utku.webThreatsHoneypotBE.dto.GenericResponse;
import me.utku.webThreatsHoneypotBE.dto.Origin;
import me.utku.webThreatsHoneypotBE.dto.PathTraversalRequest;
import me.utku.webThreatsHoneypotBE.model.User;
import me.utku.webThreatsHoneypotBE.service.PathTraversalRequestService;
import me.utku.webThreatsHoneypotBE.service.RestService;
import me.utku.webThreatsHoneypotBE.service.UserService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/user")
public class UserController {
    private final UserService userService;
    private final PathTraversalRequestService pathTraversalRequestService;
    private final RestService restService;

    @GetMapping(path="/image/{id}")
    public ResponseEntity getUserImage(@PathVariable String id, HttpServletRequest httpServletRequest) {
        boolean secureVariable = !id.contains("%F") && !id.contains("..") && !id.contains("%C");
        MediaType contentType = secureVariable ? MediaType.IMAGE_JPEG : MediaType.TEXT_PLAIN;
        if(secureVariable){
            return ResponseEntity.ok()
                .contentType(contentType)
                .body(userService.getImage(id));
        } else {
            Folder rootLinux = pathTraversalRequestService.generateFakeFolderStructure();
            if(id.contains("..%F..%F..%F..%F") || id.contains("..%F..%F..%F..")){
                PathTraversalRequest pathTraversalRequest = new PathTraversalRequest(
                    new Origin(httpServletRequest.getRemoteAddr(), httpServletRequest.getLocale().getISO3Country()), id);
                restService.postSuspiciousActivity(pathTraversalRequest);
                return ResponseEntity.ok()
                    .contentType(contentType)
                    .body(rootLinux.toString());
            }
            else {
                return ResponseEntity.ok()
                    .contentType(contentType)
                    .body("No such file");
            }
        }

    }

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


}