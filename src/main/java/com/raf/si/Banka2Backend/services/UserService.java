package com.raf.si.Banka2Backend.services;

import com.raf.si.Banka2Backend.exceptions.UserNotFoundException;
import com.raf.si.Banka2Backend.models.PasswordResetToken;
import com.raf.si.Banka2Backend.models.Permission;
import com.raf.si.Banka2Backend.models.User;
import com.raf.si.Banka2Backend.repositories.PasswordResetTokenRepository;
import com.raf.si.Banka2Backend.repositories.UserRepository;
import com.raf.si.Banka2Backend.services.interfaces.UserServiceInterface;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService, UserServiceInterface {
  private final UserRepository userRepository;
  private final PasswordResetTokenRepository passwordResetTokenRepository;

  @Autowired
  public UserService(UserRepository userRepository, PasswordResetTokenRepository passwordResetTokenRepository) {
    this.userRepository = userRepository;
    this.passwordResetTokenRepository = passwordResetTokenRepository;
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

  @Override
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
  public List<Permission> getUserPermissions(String email) {
    List<Permission> permissions = new ArrayList<>(userRepository.findUserByEmail(email).get().getPermissions());
    return permissions;
  }

  @Override
  public Optional<User> findById(Long id) throws NoSuchElementException {
    return userRepository.findById(id);
  }

  @Override
  public void deleteById(Long id) throws UserNotFoundException {
    try{
      userRepository.deleteById(id);
    } catch(Exception e) {
      throw new UserNotFoundException("User with id " + id + " not found.");
    }
  }

  @Override
  public Optional<User> getUserByPasswordResetToken(String token) {
    Optional<PasswordResetToken> passwordResetToken =
        this.passwordResetTokenRepository.findPasswordResetTokenByToken(token);
    if (passwordResetToken.isEmpty()) return null;
    return this.userRepository.findById(passwordResetToken.get().getUser().getId());
  }

  @Override
  public void changePassword(User user, String newPassword, String passwordResetToken) {
    user.setPassword(newPassword);
    userRepository.save(user);
    this.passwordResetTokenRepository.deleteByToken(passwordResetToken);
  }
}
