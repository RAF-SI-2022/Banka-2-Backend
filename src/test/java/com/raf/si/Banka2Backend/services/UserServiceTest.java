package com.raf.si.Banka2Backend.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.raf.si.Banka2Backend.models.User;
import com.raf.si.Banka2Backend.repositories.UserRepository;
import java.util.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

  @Mock UserRepository userRepository;

  @InjectMocks UserService userService;

  @Test
  public void findAll_success() {

    long id = 1L;

    List<User> userList =
        Arrays.asList(
            User.builder()
                .id(id++)
                .firstName("Petar")
                .lastName("Petrovic")
                .phone("000000000")
                .jmbg("000000000")
                .password("12345")
                .email("petar@gmail.com")
                .jobPosition("/")
                .build(),
            User.builder()
                .id(id++)
                .firstName("Marko")
                .lastName("Markovic")
                .phone("000000000")
                .jmbg("000000000")
                .password("12345")
                .email("marko@gmail.com")
                .jobPosition("/")
                .build(),
            User.builder()
                .id(id)
                .firstName("Darko")
                .lastName("Darkovic")
                .phone("000000000")
                .jmbg("000000000")
                .password("12345")
                .email("darko@gmail.com")
                .jobPosition("/")
                .build());

    when(userRepository.findAll()).thenReturn(userList);

    List<User> result = userService.findAll();
    assertEquals(userList, result);
  }

  @Test
  public void findById_success() {

    long id = 1L;

    User user =
        User.builder()
            .id(id)
            .firstName("Darko")
            .lastName("Darkovic")
            .phone("000000000")
            .jmbg("000000000")
            .password("12345")
            .email("darko@gmail.com")
            .jobPosition("/")
            .build();

    when(userRepository.findById(id)).thenReturn(Optional.of(user));

    Optional<User> result = userService.findById(id);

    assertEquals(user, result.get());
  }

  @Test
  public void findById_notFound() {

    long id = 1L;

    when(userRepository.findById(id)).thenReturn(null);

    assertNull(userService.findById(id));
  }

  @Test
  public void findByEmail_success() {

    long id = 1L;

    String emailSearch = "darko@gmail.com";

    User user =
        User.builder()
            .id(id)
            .firstName("Darko")
            .lastName("Darkovic")
            .phone("000000000")
            .jmbg("000000000")
            .password("12345")
            .email("darko@gmail.com")
            .jobPosition("/")
            .build();

    when(userRepository.findUserByEmail(emailSearch)).thenReturn(Optional.of(user));

    Optional<User> result = userService.findByEmail(emailSearch);

    assertEquals(user, result.get());
  }

  @Test
  public void findByEmail_notFound() {

    String emailSearch = "darko@gmail.com";

    when(userRepository.findUserByEmail(emailSearch)).thenReturn(null);

    assertNull(userService.findByEmail(emailSearch));
  }

  @Test
  public void loadUserByUsername_success() {

    long id = 1L;
    String email = "darko@gmail.com";

    User userFromDB =
        User.builder()
            .id(id)
            .firstName("Darko")
            .lastName("Darkovic")
            .phone("000000000")
            .jmbg("000000000")
            .password("12345")
            .email("darko@gmail.com")
            .jobPosition("/")
            .build();

    when(userRepository.findUserByEmail(email)).thenReturn(Optional.of(userFromDB));

    UserDetails result = userService.loadUserByUsername(email);

    assertEquals(userFromDB.getEmail(), result.getUsername());
    assertEquals(userFromDB.getPassword(), result.getPassword());
  }

  @Test
  public void loadUserByUsername_throwsUsernameNotFoundException() {

    String email = "darko@gmail.com";

    when(userRepository.findUserByEmail(email)).thenReturn(Optional.empty());

    assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername(email));
  }

  @Test
  public void save_success() {

    long id = 1L;

    User newUser =
        User.builder()
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

  @Test
  public void deleteById_success() {

    long id = 1L;

    doNothing().when(userRepository).deleteById(id);

    userService.deleteById(id);

    verify(userRepository).deleteById(id);
  }

  @Test
  public void deleteById_notFound() {

    long id = 1L;

    when(userRepository.findById(id)).thenReturn(null);

    assertNull(userService.findById(id));
  }
}
