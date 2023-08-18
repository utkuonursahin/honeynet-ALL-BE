package me.utku.honeynet.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.utku.honeynet.dto.user.UserResponseDTO;
import me.utku.honeynet.dto.user.UserUpdateDTO;
import me.utku.honeynet.dto.user.UserUpdateResponseDTO;
import me.utku.honeynet.dto.security.CustomUserDetails;
import me.utku.honeynet.model.User;
import me.utku.honeynet.repository.UserRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = {@Lazy})
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    @Lazy
    private final PasswordEncoder passwordEncoder;
    private final FirmService firmService;

    public List<User> getAll() {
        List<User> users = new ArrayList<>();
        try {
            users = userRepository.findAll();
        } catch (Exception exception) {
            log.error("Exception occurs in get all operation of UserService : {}", exception.getMessage());
        }
        return users;
    }

    private UserResponseDTO generateUserResponseDTO(User user){
        UserResponseDTO userResponseDTO = null;
        try{
            if (user.getFirmRef() != null)
                userResponseDTO = new UserResponseDTO(user.getId(), user.getUsername(), user.getEmail(), user.getRole(), user.getFirmRef(), firmService.get(user.getFirmRef()).getFirmName());
            else
                userResponseDTO = new UserResponseDTO(user.getId(), user.getUsername(), user.getEmail(), user.getRole(), null, null);
        }catch (Exception exception){
            log.error("Exception occurs in generateUserResponseDTO operation of UserService : {}", exception.getMessage());
        }
        return userResponseDTO;
    }

    public UserResponseDTO get(String id) {
        UserResponseDTO userResponseDTO = null;
        try {
            User user = userRepository.findById(id).orElse(null);
            if (user == null) throw new Exception("No user found with that id!");
            userResponseDTO = generateUserResponseDTO(user);
        } catch (Exception exception) {
            log.error("Error occurs in get operation of UserService : {}", exception.getMessage());
            }
        return userResponseDTO;
    }

    public UserResponseDTO getByFirm(String firmId) {
        UserResponseDTO userResponseDTO = null;
        try {
            User user = userRepository.findFirstByFirmRef(firmId);
            if (user == null) throw new Exception("No user found with that firm id!");
            userResponseDTO = generateUserResponseDTO(user);
        } catch (Exception exception) {
            log.error("Exception occurs in get by firm operation of UserService : {}", exception.getMessage());
        }
        return userResponseDTO;
    }

    public UserResponseDTO create(User newUser) {
        UserResponseDTO userResponseDTO = null;
        try {
            newUser.setId(UUID.randomUUID().toString());
            newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
            User user = userRepository.save(newUser);
            userResponseDTO = generateUserResponseDTO(user);
            log.info("UserResponseDTO has been created successfully");
        } catch (Exception exception) {
            log.error("Exception occurs in create operation of UserService : {}", exception.getMessage());
        }
        return userResponseDTO;
    }

    public UserUpdateResponseDTO update(String id, UserUpdateDTO userUpdateDTO) {
        UserResponseDTO userResponseDTO = new UserResponseDTO(null, null, null, null, null, null);
        try {
            User existingUser = userRepository.findById(id).orElse(null);
            if (existingUser == null) throw new Exception("No user found with that id!");
            if (userUpdateDTO.newPassword() != null && !passwordEncoder.matches(userUpdateDTO.oldPassword(), existingUser.getPassword())) {
                throw new Exception("Current password is wrong!");
            }
            if (userUpdateDTO.newPassword() != null && !userUpdateDTO.newPassword().equals(userUpdateDTO.passwordConfirm())) {
                throw new Exception("New password and confirm password does not match!");
            }
            if (userUpdateDTO.newPassword() != null)
                existingUser.setPassword(passwordEncoder.encode(userUpdateDTO.newPassword()));
            if (userUpdateDTO.username() != null) existingUser.setUsername(userUpdateDTO.username());
            if (userUpdateDTO.email() != null) existingUser.setEmail(userUpdateDTO.email());
            existingUser = userRepository.save(existingUser);
            userResponseDTO = generateUserResponseDTO(existingUser);
            log.info("User has been updated successfully with ID : {}", id);
        } catch (Exception exception) {
            log.error("Exception occurs in update operation of UserService : {}", exception.getMessage());
            return new UserUpdateResponseDTO(401, exception.getMessage(), userResponseDTO);
        }
        return new UserUpdateResponseDTO(200, "User updated successfully!", userResponseDTO);
    }

    public boolean delete(String id) {
        boolean result = false;
        try {
            userRepository.deleteById(id);
            result = true;
            log.info("Selected User has been deleted successfully with ID : {}", id);
        } catch (Exception exception) {
            log.error("Exception occurs in delete operation of UserService : {}", exception.getMessage());
        }
        return result;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("Could not find user");
        }
        return new CustomUserDetails(
            user.getId(),
            user.getUsername(),
            user.getPassword(),
            user.getEmail(),
            user.getFirmRef(),
            AuthorityUtils.createAuthorityList(user.getRole().name())
        );
    }
}