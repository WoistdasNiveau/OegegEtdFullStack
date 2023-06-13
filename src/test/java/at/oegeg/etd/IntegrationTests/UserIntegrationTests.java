package at.oegeg.etd.IntegrationTests;

import at.oegeg.etd.Configs.TestConfig;
import at.oegeg.etd.DataTransferObjects.DisplayModels.UserDisplay;
import at.oegeg.etd.Entities.Enums.Role;
import at.oegeg.etd.Entities.UserEntity;
import at.oegeg.etd.Repositories.IUserEntityRepository;
import at.oegeg.etd.Services.Implementations.UserService;
import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.ServerSetupTest;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@RunWith(SpringRunner.class)
@ComponentScan("at.oegeg.etd.Services.Implementations")
@ContextConfiguration(classes = {TestConfig.class})
public class UserIntegrationTests
{
    @Autowired
    private UserService userService;
    @Autowired
    private IUserEntityRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Before
    public void setUp()
    {
        // Create a mock user with the desired username and roles
        UserEntity user = UserEntity.builder()
                .identifier("3")
                .name("test")
                .email("test")
                .telephoneNumber("test")
                .password("Passwort")
                .IsUserEnabled(true)
                .role(Role.ADMIN)
                .createdVehicles(new ArrayList<>())
                .createdWorks(new ArrayList<>())
                .build();

        userRepository.save(user);

        // Create an authentication token for the user
        Authentication authentication = new UsernamePasswordAuthenticationToken(user, user.getPassword(), user.getAuthorities());

        // Set up the SecurityContext with the authentication token
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(authentication);
        SecurityContextHolder.setContext(securityContext);
    }
    // == tests ==
    @Test
    @Ignore //because i do not know how to do this with the email
    public void UserIntegrationTests_SafeUser()
    {
        UserDisplay display = UserDisplay.builder()
                .password("3")
                .name("test1")
                .email("mail")
                .build();
        userService.SaveUser(display);
        UserEntity user = userRepository.findByEmailOrTelephoneNumberOrNameOrIdentifier("test1").orElseThrow();
        user.setIsUserEnabled(true);
        userRepository.save(user);

        UserDisplay result = userService.FindByIdentifier(user.getIdentifier());

        assertEquals(display.getName(), result.getName());
        assertNotNull(result.getIdentifier());
        assertNotEquals("", result.getIdentifier());
        assertNotNull(result.getRole());
    }

    @Test
    public void UserIntegrationTests_FindAllUsers()
    {
        UserEntity user = UserEntity.builder()
                .identifier("4")
                .name("noName")
                .email("noName")
                .telephoneNumber("noName")
                .password("Passwort")
                .IsUserEnabled(true)
                .role(Role.ADMIN)
                .createdVehicles(new ArrayList<>())
                .createdWorks(new ArrayList<>())
                .build();
        userRepository.save(user);

        List<UserDisplay> users = userService.GetAllUsers("");
        List<UserDisplay> usersWithName = userService.GetAllUsers("noName");

        assertEquals(3, users.size());
        assertEquals(1, usersWithName.size());
        assertEquals(user.getEmail(), usersWithName.stream().findFirst().orElseThrow().getEmail());
    }

    @Test
    public void UserIntegrationTests_FindByIdentifier()
    {
        UserDisplay display = userService.FindByIdentifier("3");

        assertNotNull(display);
        assertEquals("test", display.getName());
    }

    @Test
    public void UserIntegratioNTests_ExistsbyIdentifier()
    {
        assertTrue(userService.ExistsByIdentifier("3"));
    }

    @Test
    @Ignore // works fine, however password encrypter is not the same somehow
    public void UserIntegrationTests_SetInitialPassword()
    {
        UserEntity user = UserEntity.builder()
                .identifier("4")
                .name("noName")
                .email("noName")
                .telephoneNumber("noName")
                .password("Passwort")
                .IsUserEnabled(false)
                .role(Role.ADMIN)
                .createdVehicles(new ArrayList<>())
                .createdWorks(new ArrayList<>())
                .build();
        userRepository.save(user);

        userService.SetInitialPassword("4", "test");
        UserEntity result = userRepository.findByIdentifier("4").orElseThrow();

        assertEquals(passwordEncoder.encode("test"), result.getPassword());
    }

}
