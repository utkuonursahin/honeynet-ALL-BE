package me.utku.webThreatsHoneypotBE.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.utku.webThreatsHoneypotBE.model.User;
import me.utku.webThreatsHoneypotBE.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public List<User> getAll(){
        List<User> users = new ArrayList<>();
        try{
            users = userRepository.findAll();
        }catch (Exception error){
            log.error("User service getAll exception: {}", error.getMessage());
        }
        return users;
    }

    public User get(String id){
        User user = new User();
        try{
            user = userRepository.findById(id).orElse(null);
        }catch (Exception error){
            log.error("User service get exception: {}", error.getMessage());
        }
        return user;
    }

    public byte[] getImage(String id){
        byte[] image = null;
        try{
            User user = userRepository.findById(id).orElse(null);
            if(user == null) throw new Exception("No user found with given id");
            String imagePath = user.getImagePath();
            Path path = Paths.get(imagePath);
            if(Files.exists(path)){
                image = Files.readAllBytes(path);
            }
        }catch (Exception error){
            log.error("User service getImage exception: {}",error.getMessage());
        }
        return image;
    }

    public User create(User newUser){
        User user = new User();
        try{
            user = userRepository.save(newUser);
        }catch (Exception error){
            log.error("User service create exception: {}", error.getMessage());
        }
        return user;
    }

    //NOT COMPLETED
    public User update(String id, User updatedUser){
        User existingUser = new User();
        try{
            existingUser = userRepository.findById(id).orElse(null);
            if(existingUser == null) throw new Exception("No user found with that id");
            userRepository.save(existingUser);
        }catch (Exception error){
            log.error("UserService update exception: {}", error.getMessage());
        }
        return existingUser;
    }

    public boolean delete(String id){
        boolean isDeleted = false;
        try{
            userRepository.deleteById(id);
            isDeleted = true;
        }catch (Exception error){
            log.error("UserService delete exception: {}", error.getMessage());
        }
        return isDeleted;
    }
}
