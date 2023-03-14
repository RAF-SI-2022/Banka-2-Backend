package com.raf.si.Banka2Backend;

import com.raf.si.Banka2Backend.models.User;
import com.raf.si.Banka2Backend.repositories.UserRepository;
import com.raf.si.Banka2Backend.services.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCrypt;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.BDDMockito.given;

@SpringBootTest
class Banka2BackendUserServiceUnitTests {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void testSave() {

        String password = BCrypt.hashpw("Admin1234", BCrypt.gensalt());

        User test = new User().builder()
                .email("admin@gmail.com")
                .firstName("Admin")
                .lastName("Adminic")
                .password( password)
                .jmbg("2902968000000")
                .phone("0657817522")
                .jobPosition("administrator")
                .active(true)
                .build();

        User user = new User().builder()
                .email("admin@gmail.com")
                .firstName("Admin")
                .lastName("Adminic")
                .password( password)
                .jmbg("2902968000000")
                .phone("0657817522")
                .jobPosition("administrator")
                .active(true)
                .build();

        given(userRepository.save(user)).willReturn(user);

        try{
            User expectedUser = userService.save(test);

            assertNotNull(expectedUser);
            assertEquals(expectedUser.getPassword(), user.getPassword());
        }
        catch (Exception e){
            System.err.println(e.getMessage());
        }
    }

}
