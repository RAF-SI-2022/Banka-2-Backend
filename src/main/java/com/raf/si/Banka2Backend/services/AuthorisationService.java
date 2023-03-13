package com.raf.si.Banka2Backend.services;

import com.raf.si.Banka2Backend.models.Permission;
import com.raf.si.Banka2Backend.models.PermissionName;
import com.raf.si.Banka2Backend.models.User;
import com.raf.si.Banka2Backend.repositories.PermissionRepository;
import com.raf.si.Banka2Backend.repositories.UserRepository;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthorisationService {

  private final PermissionRepository permissionRepository;
  private final UserRepository userRepository;

  @Autowired
  public AuthorisationService(
      PermissionRepository permissionRepository, UserRepository userRepository) {
    this.permissionRepository = permissionRepository;
    this.userRepository = userRepository;
  }

  public boolean isAuthorised(PermissionName permissionRequired, String userEmail) {
    Optional<User> userOptional = this.userRepository.findUserByEmail(userEmail);
    if (!userOptional.isPresent()) {
      return false;
    }
    User user = userOptional.get();
    for (Permission p : user.getPermissions()) {
      if (p.getPermissionName().equals(permissionRequired)
          || p.getPermissionName().equals(PermissionName.ADMIN_USER)) {
        return true;
      }
    }
    return false;
  }
}
