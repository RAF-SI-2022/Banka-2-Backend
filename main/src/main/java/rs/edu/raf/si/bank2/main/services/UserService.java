package rs.edu.raf.si.bank2.main.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import rs.edu.raf.si.bank2.main.dto.CommunicationDto;
import rs.edu.raf.si.bank2.main.exceptions.PasswordResetTokenNotFoundException;
import rs.edu.raf.si.bank2.main.exceptions.UserNotFoundException;
import rs.edu.raf.si.bank2.main.models.mariadb.PasswordResetToken;
import rs.edu.raf.si.bank2.main.models.mariadb.Permission;
import rs.edu.raf.si.bank2.main.models.mariadb.User;
import rs.edu.raf.si.bank2.main.repositories.mariadb.PasswordResetTokenRepository;
import rs.edu.raf.si.bank2.main.repositories.mariadb.UserRepository;
import rs.edu.raf.si.bank2.main.services.interfaces.UserCommunicationInterface;
import rs.edu.raf.si.bank2.main.services.interfaces.UserServiceInterface;

@Service
public class UserService implements UserDetailsService, UserServiceInterface {
    private final UserRepository userRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final UserCommunicationInterface userCommunicationInterface;
    ObjectMapper mapper = new ObjectMapper();

    @Autowired
    public UserService(UserRepository userRepository, PasswordResetTokenRepository passwordResetTokenRepository,
                       UserCommunicationService communicationService) {
        this.userRepository = userRepository;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.userCommunicationInterface = communicationService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> myUser = this.findByEmail(username);
        if (myUser.isEmpty()) {
            throw new UsernameNotFoundException("Korisnik sa email-om: " + username + " nije pronadjen.");
        }
        return new org.springframework.security.core.userdetails.User(myUser.get().getEmail(), "", new ArrayList<>());
    }

    @Override
    public Optional<User> findByEmail(String email) {
//        return userRepository.findUserByEmail(email);

        User user = null;
        CommunicationDto response = userCommunicationInterface.sendGet( email, "/findByEmail");

        System.out.println("ovo je response " + response);


        try {
            user = mapper.readValue(response.getResponseMsg(), User.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        if (user == null) System.err.println("USER IS NULL");

        return Optional.of(user);
    }

    @Override
    public List<User> findAll() {
        List<User> user = null;
        CommunicationDto response = userCommunicationInterface.sendGet( null,"/findAll");
        try {
            user = mapper.readValue(response.getResponseMsg(), List.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return user;
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
    public Optional<User> getUserByPasswordResetToken(String token) {
        Optional<PasswordResetToken> passwordResetToken =
                passwordResetTokenRepository.findPasswordResetTokenByToken(token);

        if (passwordResetToken.isPresent())
            return userRepository.findById(passwordResetToken.get().getUser().getId());
        else throw new PasswordResetTokenNotFoundException(token);
    }

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
