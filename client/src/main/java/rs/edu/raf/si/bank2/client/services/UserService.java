package rs.edu.raf.si.bank2.client.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import rs.edu.raf.si.bank2.client.models.mariadb.PasswordResetToken;
import rs.edu.raf.si.bank2.client.models.mariadb.Permission;
import rs.edu.raf.si.bank2.client.models.mariadb.User;
import rs.edu.raf.si.bank2.client.services.interfaces.UserServiceInterface;
import rs.edu.raf.si.bank2.client.exceptions.UserNotFoundException;
import rs.edu.raf.si.bank2.client.repositories.mariadb.PasswordResetTokenRepository;
import rs.edu.raf.si.bank2.client.repositories.mariadb.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService implements UserServiceInterface {
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
            throw new UsernameNotFoundException("Korisnik sa email-om: " + username + " nije pronadjen.");
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
        List<Permission> permissions =
                new ArrayList<>(userRepository.findUserByEmail(email).get().getPermissions());
        return permissions;
    }

    @Override
    public Optional<User> findById(Long id) throws UserNotFoundException {

        Optional<User> user = userRepository.findById(id);

        if (user.isPresent()) {
            return user;
        } else {
            throw new UserNotFoundException(id);
        }
    }

    @Override
    public void deleteById(Long id) throws UserNotFoundException {

        //    try {

        userRepository.deleteById(id);
        //    }
        //    catch (NoSuchElementException e) {
        //      throw new UserNotFoundException(id);
        //    }
    }


    @Override
    public User changeUsersDailyLimit(String userEmail, Double limitChange) {
        User user = findByEmail(userEmail).get();
        user.setDailyLimit(user.getDailyLimit() + limitChange);
        userRepository.save(user);
        return user;
    }

    @Override
    public Double getUsersDailyLimit(String userEmail) {
        return findByEmail(userEmail).get().getDailyLimit();
    }
}
