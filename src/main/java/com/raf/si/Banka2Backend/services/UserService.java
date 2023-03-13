package com.raf.si.Banka2Backend.services;

import com.raf.si.Banka2Backend.models.User;
import com.raf.si.Banka2Backend.repositories.UserRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService, UserServiceInteface {

  @Autowired private final UserRepository userRepository;

  public UserService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    Optional<User> myUser = this.findByEmail(username);
    if (myUser.isEmpty()) {
      throw new UsernameNotFoundException("User with email: " + username + " not found");
    }

    return new org.springframework.security.core.userdetails.User(
        myUser.get().getEmail(), myUser.get().getPassword(), new ArrayList<>());
  }

  public Optional<User> findByEmail(String email) {
    return userRepository.findUserByEmail(email);
  }

  @Override
  public List<User> findAll() {
    return userRepository.findAll();
  }

  @Override
  public User save(User user) {
    return userRepository.save(user);
  }

  @Override
  public Optional<User> findById(Long id) {
    return userRepository.findById(id);
  }

  @Override
  public void deleteById(Long id) {
    userRepository.deleteById(id);
  }
}
