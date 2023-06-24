package rs.edu.raf.si.bank2.users.services;

import io.micrometer.core.annotation.Timed;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import rs.edu.raf.si.bank2.users.exceptions.PasswordResetTokenNotFoundException;
import rs.edu.raf.si.bank2.users.exceptions.UserNotFoundException;
import rs.edu.raf.si.bank2.users.models.mariadb.PasswordResetToken;
import rs.edu.raf.si.bank2.users.models.mariadb.Permission;
import rs.edu.raf.si.bank2.users.models.mariadb.User;
import rs.edu.raf.si.bank2.users.repositories.mariadb.PasswordResetTokenRepository;
import rs.edu.raf.si.bank2.users.repositories.mariadb.UserRepository;
import rs.edu.raf.si.bank2.users.services.interfaces.UserServiceInterface;

@Timed
@Service
public class UserService implements UserServiceInterface {
    private final UserRepository userRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;

    @Autowired
    public UserService(UserRepository userRepository, PasswordResetTokenRepository passwordResetTokenRepository) {
        this.userRepository = userRepository;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
    }

    @Timed("services.user.loadUserByUsername")
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> myUser = this.findByEmail(username);
        if (myUser.isEmpty()) {
            throw new UsernameNotFoundException("Korisnik sa email-om: " + username + " nije pronadjen.");
        }

        return new org.springframework.security.core.userdetails.User(
                myUser.get().getEmail(), myUser.get().getPassword(), new ArrayList<>());
    }

    @Timed("services.user.findByEmail")
    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findUserByEmail(email);
    }

    @Timed("services.user.findAll")
    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Timed("services.user.save")
    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

    @Timed("services.user.getUserPermissions")
    @Override
    public List<Permission> getUserPermissions(String email) {
        List<Permission> permissions =
                new ArrayList<>(userRepository.findUserByEmail(email).get().getPermissions());
        return permissions;
    }

    @Timed("services.user.findById")
    @Override
    public Optional<User> findById(Long id) throws UserNotFoundException {

        Optional<User> user = userRepository.findById(id);

        if (user.isPresent()) {
            return user;
        } else {
            throw new UserNotFoundException(id);
        }
    }

    @Timed("services.user.deleteById")
    @Override
    public void deleteById(Long id) throws UserNotFoundException {
        userRepository.deleteById(id);
    }

    @Timed("services.user.getUserByPasswordResetToken")
    @Override
    public Optional<User> getUserByPasswordResetToken(String token) {
        Optional<PasswordResetToken> passwordResetToken =
                passwordResetTokenRepository.findPasswordResetTokenByToken(token);

        if (passwordResetToken.isPresent())
            return userRepository.findById(passwordResetToken.get().getUser().getId());
        else throw new PasswordResetTokenNotFoundException(token);
    }

    @Timed("services.user.changePassword")
    @Override
    public void changePassword(User user, String newPassword, String passwordResetToken) {
        user.setPassword(newPassword);

        Optional<PasswordResetToken> passwordResetTokenFromDB =
                passwordResetTokenRepository.findPasswordResetTokenByToken(passwordResetToken);

        if (passwordResetTokenFromDB.isPresent()) {
            Optional<User> userFromDB = userRepository.findById(user.getId());

            if (userFromDB.isPresent()) {
                User userToChangePasswordTo = userFromDB.get();
                userToChangePasswordTo.setPassword(newPassword);

                userRepository.save(user);
            } else {
                throw new UserNotFoundException(user.getId());
            }

            passwordResetTokenRepository.deleteByToken(passwordResetToken);
        } else {
            throw new PasswordResetTokenNotFoundException(passwordResetToken);
        }
    }

    @Timed("services.user.changeUsersDailyLimit")
    @Override
    public User changeUsersDailyLimit(String userEmail, Double limitChange) {
        User user = findByEmail(userEmail).get();
        user.setDailyLimit(user.getDailyLimit() + limitChange);
        userRepository.save(user);
        return user;
    }

    @Timed("services.user.getUsersDailyLimit")
    @Override
    public Double getUsersDailyLimit(String userEmail) {
        return findByEmail(userEmail).get().getDailyLimit();
    }
}
