package me.utku.honeynet.service;
import lombok.extern.slf4j.Slf4j;
import me.utku.honeynet.dto.security.CustomUserDetails;
import me.utku.honeynet.model.User;
import me.utku.honeynet.repository.UserRepository;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
@Service
public class UserService implements UserDetailsService {
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  public List<User> getAll(){
    List<User> users = new ArrayList<>();
    try{
      users = userRepository.findAll();
    }catch (Exception exception) {
      log.error("User service getAll exception: {}",exception.getMessage());
    }
    return users;
  }

  public User get(String id){
    User user = new User();
    try{
      user = userRepository.findById(id).orElse(null);
    }catch(Exception exception){
      log.error("User service get exception: {}",exception.getMessage());
    }
    return user;
  }

  public User create(User newUser){
    User user = new User();
    try{
      newUser.setId(UUID.randomUUID().toString());
      newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
      user = userRepository.save(newUser);
    }catch (Exception exception){
      log.error("User service create exception: {}",exception.getMessage());
    }
    return user;
  }

  public User update(String id, User updatedParts){
    User existingUser = new User();
    try{
      existingUser = get(id);
      if(existingUser == null) throw new Exception("No user found with that id!");
      userRepository.save(existingUser);
    }catch (Exception exception){
      log.error("User service update exception: {}",exception.getMessage());
    }
    return existingUser;
  }
  public boolean delete(String id){
    boolean result = false;
    try{
      userRepository.deleteById(id);
      result = true;
    } catch (Exception exception){
      log.error("User service delete exception: {}",exception.getMessage());
    }
    return result;
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    User user = userRepository.findByUsername(username);
    if (user == null) {
      throw new UsernameNotFoundException("Could not find user");
    }
    return new CustomUserDetails(
        user.getId(),
        user.getUsername(),
        user.getPassword(),
        AuthorityUtils.createAuthorityList(user.getRole().name())
    );
  }
}