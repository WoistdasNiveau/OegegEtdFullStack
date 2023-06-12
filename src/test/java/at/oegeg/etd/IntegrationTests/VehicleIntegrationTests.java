package at.oegeg.etd.IntegrationTests;

import at.oegeg.etd.Config.ApplicationConfig;
import at.oegeg.etd.Config.SecurityConfig;
import at.oegeg.etd.Configs.TestConfig;
import at.oegeg.etd.DataTransferObjects.DisplayModels.VehicleDisplay;
import at.oegeg.etd.Entities.Enums.Priorities;
import at.oegeg.etd.Entities.Enums.Role;
import at.oegeg.etd.Entities.UserEntity;
import at.oegeg.etd.Entities.VehicleEntity;
import at.oegeg.etd.Repositories.IUserEntityRepository;
import at.oegeg.etd.Repositories.IVehicleRepository;
import at.oegeg.etd.Services.Implementations.VehicleService;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
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

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@RunWith(SpringRunner.class)
@ComponentScan("at.oegeg.etd.Services.Implementations")
@ContextConfiguration(classes = {TestConfig.class})
public class VehicleIntegrationTests
{
    @Autowired
    private VehicleService vehicleService;
    @Autowired
    private IVehicleRepository vehicleRepository;
    @Autowired
    private IUserEntityRepository userRepository;

    // == beofre ==
    @Before
    public void setUp()
    {
        // Create a mock user with the desired username and roles
        UserEntity user = UserEntity.builder()
                .identifier(UUID.randomUUID().toString())
                .name("test")
                .email("test")
                .telephoneNumber("test")
                .password("Passwort")
                .IsUserEnabled(true)
                .role(Role.ADMIN)
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
    public void VehicleIntegration_SaveVehicle_SavesDisplayCorrectly()
    {
        VehicleDisplay display = VehicleDisplay.builder()
                .number("3")
                .type("test")
                .status("t")
                .stand("365")
                .build();

        vehicleService.SaveVehicle(display);

        List<VehicleEntity> entities = vehicleRepository.findAll();
        assertEquals(1,entities.size());
        VehicleEntity entity = vehicleRepository.findAll().stream().findFirst().orElseThrow();
        assertEquals("3",entity.getNumber());
        assertEquals("test", entity.getType());
        assertEquals("t", entity.getStatus());
        assertEquals("365", entity.getStand());
        assertNotNull(entity.getIdentifier());
        assertNotNull(entity.getId());
        assertNotNull(entity.getPriority());
    }

    @Test
    public void VehicleIntegration_FindAllVehicles_WithAndWithoutFilterText()
    {
        VehicleDisplay entity1 = VehicleDisplay.builder()
                .number("3")
                .type("test")
                .status("t")
                .stand("365")
                .build();
        VehicleDisplay entity2 = VehicleDisplay.builder()
                .number("31")
                .type("noType")
                .status("t")
                .stand("3651")
                .build();

        vehicleService.SaveVehicle(entity1);
        vehicleService.SaveVehicle(entity2);

        List<VehicleDisplay> findAll = vehicleService.FindAllVehicles("");
        List<VehicleDisplay> findTest = vehicleService.FindAllVehicles("test");

        assertEquals(2, findAll.size());
        assertEquals(1, findTest.size());

        VehicleDisplay display1 = findTest.stream().findFirst().orElseThrow();
        assertEquals(entity1.getNumber(), display1.getNumber());
        assertEquals(entity1.getType(), display1.getType());
        assertEquals(entity1.getStatus(), display1.getStatus());
        assertEquals(entity1.getStand(), display1.getStand());
        assertEquals(entity1.getPriority(), display1.getPriority());
    }
}
