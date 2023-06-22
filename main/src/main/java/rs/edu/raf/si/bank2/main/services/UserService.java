package rs.edu.raf.si.bank2.main.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
    public UserService(
            UserRepository userRepository,
            PasswordResetTokenRepository passwordResetTokenRepository,
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
        return new org.springframework.security.core.userdetails.User(
                myUser.get().getEmail(), myUser.get().getPassword(), new ArrayList<>());
    }

    @Override
    public Optional<User> findByEmail(String email) {
        User user = null;
        CommunicationDto response = userCommunicationInterface.sendGet(email, "/findByEmail");

        if (response.getResponseCode() == 200) {
            try {
                user = mapper.readValue(response.getResponseMsg(), User.class);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            return Optional.of(user);

        } else {
            return Optional.empty();
            //            throw new UserNotFoundException(email);
        }
    }

    @Override
    public List<User> findAll() {
        List<User> user = null;
        CommunicationDto response = userCommunicationInterface.sendGet(null, "/findAll");
        try {
            user = mapper.readValue(response.getResponseMsg(), List.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return user;
    }

    @Override
    public List<Permission> getUserPermissions(String email) {
        List<Permission> permissions =
                new ArrayList<>(this.findByEmail(email).get().getPermissions());
        return permissions;
    }

    @Override
    public Optional<User> findById(Long id) throws UserNotFoundException {
        CommunicationDto response = userCommunicationInterface.sendGet(null, "/findById/" + id);

        User user = null;

        try {
            user = mapper.readValue(response.getResponseMsg(), User.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        if (user != null) {
            return Optional.of(user);
        } else {
            throw new UserNotFoundException(id);
        }
    }

    @Override
    public void deleteById(Long id) throws UserNotFoundException {
        //        userCommunicationInterface.sendDelete("/deleteById/" + id);
        userRepository.deleteById(id);
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
            Optional<User> userFromDB = this.findById(user.getId());

            if (userFromDB.isPresent()) {
                User userToChangePasswordTo = userFromDB.get();
                userToChangePasswordTo.setPassword(newPassword);

                //                userRepository.save(user);

                try { // todo proveri da li ovo radi pravilno
                    String userJsonBody = mapper.writeValueAsString(user);
                    userCommunicationInterface.sendPostLike("/save", userJsonBody, null, "POST");
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }

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
        //        userRepository.save(user);

        try {
            String userJsonBody = mapper.writeValueAsString(user);
            userCommunicationInterface.sendPostLike("/save", userJsonBody, null, "POST");
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        return user;
    }

    @Override
    public Double getUsersDailyLimit(String userEmail) {
        return findByEmail(userEmail).get().getDailyLimit();
    }

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }
}
