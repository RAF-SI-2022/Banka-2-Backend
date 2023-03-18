package com.raf.si.Banka2Backend.services.interfaces;

import com.raf.si.Banka2Backend.models.users.Permission;
import com.raf.si.Banka2Backend.models.users.User;
import java.util.List;
import java.util.Optional;

public interface UserServiceInterface {

  List<User> findAll();

  User save(User user);

  Optional<User> findById(Long id);

  List<Permission> getUserPermissions(String email);

  void deleteUser(Long id);

  //    User updateUser(User user);

  void deleteById(Long id);

  Optional<User> getUserByPasswordResetToken(String token);

  void changePassword(User user, String newPassword, String token);
}
