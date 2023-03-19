package com.raf.si.Banka2Backend.services;

import com.raf.si.Banka2Backend.models.users.PasswordResetToken;
import com.raf.si.Banka2Backend.models.users.Permission;
import com.raf.si.Banka2Backend.models.users.PermissionName;
import com.raf.si.Banka2Backend.models.users.User;
import com.raf.si.Banka2Backend.repositories.users.PasswordResetTokenRepository;
import com.raf.si.Banka2Backend.repositories.users.PermissionRepository;
import com.raf.si.Banka2Backend.repositories.users.UserRepository;
import java.util.Calendar;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthorisationService {

  private final PermissionRepository permissionRepository;
  private final UserRepository userRepository;
  private final PasswordResetTokenRepository passwordResetTokenRepository;

  @Autowired
  public AuthorisationService(
      PermissionRepository permissionRepository,
      UserRepository userRepository,
      PasswordResetTokenRepository passwordResetTokenRepository) {
    this.permissionRepository = permissionRepository;
    this.userRepository = userRepository;
    this.passwordResetTokenRepository = passwordResetTokenRepository;
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

  public String validatePasswordResetToken(String token) {
    Optional<PasswordResetToken> passwordResetToken =
        passwordResetTokenRepository.findPasswordResetTokenByToken(token);
    if (passwordResetToken.isEmpty()) return "Token not found";

    Calendar cal = Calendar.getInstance();
    if (passwordResetToken.get().getExpirationDate().before(cal.getTime())) {
      this.passwordResetTokenRepository.delete(passwordResetToken.get());
      return "Token expired";
    }

    return "";
  }
}
