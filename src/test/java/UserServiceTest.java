import com.netology.diplom.CloudStoreApplication;
import com.netology.diplom.entity.User;
import com.netology.diplom.repository.UserRepository;
import com.netology.diplom.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = CloudStoreApplication.class)
@ActiveProfiles("test")
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;


    @Test
    void loadUserByUsername_shouldReturnUserDetails_whenUserExists() {
        User user = new User();
        user.setLogin("testUser");
        user.setPassword("testPassword");
        when(userRepository.findByLogin("testUser")).thenReturn(Optional.of(user));
        UserService userService = new UserService(userRepository);

        UserDetails userDetails = userService.loadUserByUsername("testUser");

        assertNotNull(userDetails);
        assertEquals("testUser", userDetails.getUsername());
        assertEquals("testPassword", userDetails.getPassword());

        assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername("testUser1"));
    }
}



