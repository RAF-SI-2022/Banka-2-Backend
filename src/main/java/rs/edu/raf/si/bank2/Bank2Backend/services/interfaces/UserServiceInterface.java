package rs.edu.raf.si.bank2.Bank2Backend.services.interfaces;

import java.util.List;
import java.util.Optional;
import rs.edu.raf.si.bank2.Bank2Backend.models.mariadb.Permission;
import rs.edu.raf.si.bank2.Bank2Backend.models.mariadb.User;

public interface UserServiceInterface {

    List<User> findAll();

    User save(User user);

    Optional<User> findById(Long id);

    Optional<User> findByEmail(String email);

    List<Permission> getUserPermissions(String email);

    //    User updateUser(User user);

    void deleteById(Long id);

    Optional<User> getUserByPasswordResetToken(String token);

    void changePassword(User user, String newPassword, String token);

    User changeUsersDailyLimit(String userEmail, Double limitChange);

    Double getUsersDailyLimit(String userEmail);
}
