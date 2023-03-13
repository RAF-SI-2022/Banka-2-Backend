package com.raf.si.Banka2Backend.services;

import com.raf.si.Banka2Backend.models.User;
import java.util.List;
import java.util.Optional;

public interface UserServiceInteface {

  List<User> findAll();

  User save(User user);

  Optional<User> findById(Long id);

  //    User updateUser(User user);

  void deleteById(Long id);
}
