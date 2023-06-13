package at.oegeg.etd.IntegrationTests;

import at.oegeg.etd.Configs.TestConfig;
import at.oegeg.etd.DataTransferObjects.DisplayModels.UserDisplay;
import at.oegeg.etd.Entities.Enums.Role;
import at.oegeg.etd.Entities.UserEntity;
import at.oegeg.etd.Repositories.IUserEntityRepository;
import at.oegeg.etd.Services.Implementations.UserService;
import org.junit.Before;
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
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;

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
    public void UserIntegrationTests_FindAllUsers()
    {

    }

}
