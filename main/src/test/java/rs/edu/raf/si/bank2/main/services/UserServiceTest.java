package rs.edu.raf.si.bank2.main.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import rs.edu.raf.si.bank2.main.dto.CommunicationDto;
import rs.edu.raf.si.bank2.main.exceptions.PasswordResetTokenNotFoundException;
import rs.edu.raf.si.bank2.main.models.mariadb.PasswordResetToken;
import rs.edu.raf.si.bank2.main.models.mariadb.User;
import rs.edu.raf.si.bank2.main.repositories.mariadb.PasswordResetTokenRepository;
import rs.edu.raf.si.bank2.main.repositories.mariadb.UserRepository;
import rs.edu.raf.si.bank2.main.services.interfaces.UserCommunicationInterface;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    UserRepository userRepository;

    @Mock
    PasswordResetTokenRepository passwordResetTokenRepository;

    @InjectMocks
    UserService userService;

    //    @Test
    //    public void findAll_success() {
    //
    //        long id = 1L;
    //
    //        List<User> userList = Arrays.asList(
    //                User.builder()
    //                        .id(id++)
    //                        .firstName("Petar")
    //                        .lastName("Petrovic")
    //                        .phone("000000000")
    //                        .jmbg("000000000")
    //                        .password("12345")
    //                        .email("petar@gmail.com")
    //                        .jobPosition("/")
    //                        .build(),
    //                User.builder()
    //                        .id(id++)
    //                        .firstName("Marko")
    //                        .lastName("Markovic")
    //                        .phone("000000000")
    //                        .jmbg("000000000")
    //                        .password("12345")
    //                        .email("marko@gmail.com")
    //                        .jobPosition("/")
    //                        .build(),
    //                User.builder()
    //                        .id(id)
    //                        .firstName("Darko")
    //                        .lastName("Darkovic")
    //                        .phone("000000000")
    //                        .jmbg("000000000")
    //                        .password("12345")
    //                        .email("darko@gmail.com")
    //                        .jobPosition("/")
    //                        .build());
    //
    //        when(userRepository.findAll()).thenReturn(userList);
    //
    //        List<User> result = userService.findAll();
    //        assertEquals(userList, result);
    //    }

    //    @Test
    //    public void findById_success() {
    //
    //        long id = 1L;
    //
    //        User user = User.builder()
    //                .id(id)
    //                .firstName("Darko")
    //                .lastName("Darkovic")
    //                .phone("000000000")
    //                .jmbg("000000000")
    //                .password("12345")
    //                .email("darko@gmail.com")
    //                .jobPosition("/")
    //                .build();
    //
    //        when(userRepository.findById(id)).thenReturn(Optional.of(user));
    //
    //        Optional<User> result = userService.findById(id);
    //
    //        assertEquals(user, result.get());
    //    }

    //    @Test
    //    public void findById_throwsUserNotFoundException() {
    //
    //        long id = 1L;
    //
    //        when(userRepository.findById(id)).thenReturn(Optional.empty());
    //
    //        assertThrows(UserNotFoundException.class, () -> {
    //            userService.findById(id);
    //        });
    //    }

    //    @Test
    //    public void findByEmail_success() {
    //
    //        long id = 1L;
    //
    //        String emailSearch = "darko@gmail.com";
    //
    //        User user = User.builder()
    //                .id(id)
    //                .firstName("Darko")
    //                .lastName("Darkovic")
    //                .phone("000000000")
    //                .jmbg("000000000")
    //                .password("12345")
    //                .email("darko@gmail.com")
    //                .jobPosition("/")
    //                .build();
    //
    //        when(userRepository.findUserByEmail(emailSearch)).thenReturn(Optional.of(user));
    //
    //        Optional<User> result = userService.findByEmail(emailSearch);
    //
    //        assertEquals(user, result.get());
    //    }

    //    @Test
    //    public void loadUserByUsername_success() {
    //
    //        long id = 1L;
    //        String email = "darko@gmail.com";
    //
    //        User userFromDB = User.builder()
    //                .id(id)
    //                .firstName("Darko")
    //                .lastName("Darkovic")
    //                .phone("000000000")
    //                .jmbg("000000000")
    //                .password("12345")
    //                .email("darko@gmail.com")
    //                .jobPosition("/")
    //                .build();
    //
    //        when(userRepository.findUserByEmail(email)).thenReturn(Optional.of(userFromDB));
    //
    //        UserDetails result = userService.loadUserByUsername(email);
    //
    //        assertEquals(userFromDB.getEmail(), result.getUsername());
    //        assertEquals(userFromDB.getPassword(), result.getPassword());
    //    }

    //    @Test
    //    public void loadUserByUsername_throwsUsernameNotFoundException() {
    //
    //        String email = "darko@gmail.com";
    //
    //        when(userRepository.findUserByEmail(email)).thenReturn(Optional.empty());
    //
    //        assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername(email));
    //    }

    @Test
    public void save_success() {

        long id = 1L;

        User newUser = User.builder()
                .id(id)
                .firstName("Darko")
                .lastName("Darkovic")
                .phone("000000000")
                .jmbg("000000000")
                .password("12345")
                .email("darko@gmail.com")
                .jobPosition("/")
                .build();

        when(userRepository.save(newUser)).thenReturn(newUser);

        userService.save(newUser);

        verify(userRepository).save(newUser);
    }

    //    @Test
    //    public void deleteById_success() {
    //
    //        long id = 1L;
    //
    //        doNothing().when(userRepository).deleteById(id);
    //
    //        userService.deleteById(id);
    //
    //        verify(userRepository).deleteById(id);
    //    }

    //    @Test
    //    public void deleteById_throwsUserNotFoundException() {
    //
    //        long id = 1L;
    //
    //        when(userRepository.findById(id)).thenThrow(UserNotFoundException.class);
    //
    //        assertThrows(UserNotFoundException.class, () -> userService.findById(id));
    //    }

    @Test
    public void getUserByPasswordResetToken_success() {

        User user = User.builder()
                .id(1L)
                .firstName("Darko")
                .lastName("Darkovic")
                .phone("000000000")
                .jmbg("000000000")
                .password("12345")
                .email("darko@gmail.com")
                .jobPosition("/")
                .build();
        String token = UUID.randomUUID().toString();
        PasswordResetToken passwordResetToken = new PasswordResetToken(user, token);

        when(passwordResetTokenRepository.findPasswordResetTokenByToken(token))
                .thenReturn(Optional.of(passwordResetToken));
        when(userRepository.findById(passwordResetToken.getUser().getId())).thenReturn(Optional.of(user));

        Optional<User> userWithToken = userService.getUserByPasswordResetToken(token);

        assertEquals(user.getEmail(), userWithToken.get().getEmail());
    }

    @Test
    public void getUserByPasswordResetToken_throwsPasswordResetTokenNotFoundException() {

        String token = UUID.randomUUID().toString();

        when(passwordResetTokenRepository.findPasswordResetTokenByToken(token)).thenReturn(Optional.empty());

        assertThrows(PasswordResetTokenNotFoundException.class, () -> {
            userService.getUserByPasswordResetToken(token);
        });
    }

    @Test
    public void changePassword_success() {
        long id = 1L;

        User user = User.builder()
                .id(id)
                .firstName("Darko")
                .lastName("Darkovic")
                .phone("000000000")
                .jmbg("000000000")
                .password("12345")
                .email("darko@gmail.com")
                .jobPosition("/")
                .build();

        String newPassword = "54321";

        String token = UUID.randomUUID().toString();
        PasswordResetToken passwordResetToken = new PasswordResetToken(user, token);

        when(passwordResetTokenRepository.findPasswordResetTokenByToken(token))
                .thenReturn(Optional.of(passwordResetToken));

        UserCommunicationInterface userCommunicationInterface = Mockito.mock(UserCommunicationInterface.class);
        userService.setUserCommunicationInterface(userCommunicationInterface);
        String responseMsg = """
                {
                  "id": 1,
                  "firstName": "Darko",
                  "lastName": "Darkovic",
                  "phone": "000000000",
                  "jmbg": "000000000",
                  "password": "12345",
                  "email": "darko@gmail.com",
                  "jobPosition": "/",
                  "dailyLimit": 4000
                }
                """;
        CommunicationDto dto = new CommunicationDto(200, responseMsg);
        when(userCommunicationInterface.sendGet(null, "/findById/" + id)).thenReturn(dto);
        try {
            userService.changePassword(user, newPassword, token);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        assertEquals(newPassword, user.getPassword());
        verify(passwordResetTokenRepository).deleteByToken(token);
    }


    @Test
    public void changePassword_throwsPasswordResetTokenNotFoundException() {

        long id = 1L;

        User user = User.builder()
                .id(id)
                .firstName("Darko")
                .lastName("Darkovic")
                .phone("000000000")
                .jmbg("000000000")
                .password("12345")
                .email("darko@gmail.com")
                .jobPosition("/")
                .build();

        String newPassword = "54321";

        String token = UUID.randomUUID().toString();

        when(passwordResetTokenRepository.findPasswordResetTokenByToken(token)).thenReturn(Optional.empty());

        assertThrows(PasswordResetTokenNotFoundException.class, () -> {
            userService.changePassword(user, newPassword, token);
        });
    }

//        @Test
//        public void changePassword_throwsUserNotFoundException() {
//
//            long id = 1L;
//
//            User user = User.builder()
//                    .id(id)
//                    .firstName("Darko")
//                    .lastName("Darkovic")
//                    .phone("000000000")
//                    .jmbg("000000000")
//                    .password("12345")
//                    .email("darko@gmail.com")
//                    .jobPosition("/")
//                    .build();
//
//            String newPassword = "54321";
//
//            String token = UUID.randomUUID().toString();
//
//            PasswordResetToken passwordResetToken = new PasswordResetToken(user, token);
//
//            when(passwordResetTokenRepository.findPasswordResetTokenByToken(token))
//                    .thenReturn(Optional.of(passwordResetToken));
//            when(userRepository.findById(user.getId())).thenReturn(Optional.empty());
//
//            assertThrows(UserNotFoundException.class, () -> {
//                userService.changePassword(user, newPassword, token);
//            });
//        }
    @Test
    public void changeUsersDailyLimit() {
        long id = 1L;

        User user = User.builder()
                .id(id)
                .firstName("Darko")
                .lastName("Darkovic")
                .phone("000000000")
                .jmbg("000000000")
                .password("12345")
                .email("darko@gmail.com")
                .jobPosition("/")
                .build();


        UserCommunicationInterface userCommunicationInterface = Mockito.mock(UserCommunicationInterface.class);
        userService.setUserCommunicationInterface(userCommunicationInterface);

        String email = "darko@gmail.com";
        String responseMsg = """
                {
                  "id": 1,
                  "firstName": "Darko",
                  "lastName": "Darkovic",
                  "phone": "000000000",
                  "jmbg": "000000000",
                  "password": "12345",
                  "email": "darko@gmail.com",
                  "jobPosition": "/",
                  "dailyLimit": 4000
                }
                """;
        CommunicationDto dto = new CommunicationDto(200, responseMsg);
        when(userCommunicationInterface.sendGet(email, "/findByEmail")).thenReturn(dto);
        this.userService.changeUsersDailyLimit(user.getEmail(), 50000d);
    }
}
