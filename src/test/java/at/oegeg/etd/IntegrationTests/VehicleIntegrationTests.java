package at.oegeg.etd.IntegrationTests;

import at.oegeg.etd.Configs.TestConfig;
import at.oegeg.etd.DataTransferObjects.DisplayModels.VehicleDisplay;
import at.oegeg.etd.Entities.Enums.Role;
import at.oegeg.etd.Entities.UserEntity;
import at.oegeg.etd.Entities.VehicleEntity;
import at.oegeg.etd.Repositories.IUserEntityRepository;
import at.oegeg.etd.Repositories.IVehicleRepository;
import at.oegeg.etd.Services.Implementations.VehicleService;
import org.junit.Before;
import org.junit.Test;
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
import java.util.List;

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

    // == before ==
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
                .isUserEnabled(true)
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
    public void VehicleIntegration_UpdateVehicle()
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
        entity2.setIdentifier(vehicleService.FindAllVehicles("").stream().findFirst().orElseThrow().getIdentifier());
        vehicleService.UpdateVehicle(entity2);
        VehicleDisplay updatedVehicle = vehicleService.FindVehicleByIdentifier(entity2.getIdentifier());

        assertEquals(entity2.getNumber(), updatedVehicle.getNumber());
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

    @Test
    public void VehicleIntegration_FindVehicleByIdentifier()
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

        VehicleDisplay entity = vehicleService.FindAllVehicles("").stream().findFirst().orElseThrow();
        VehicleDisplay found = vehicleService.FindVehicleByIdentifier(entity.getIdentifier());

        assertEquals(entity,found);
    }

    @Test
    public void VehicleIntegration_FindAllByCreatedBy()
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
        List<VehicleDisplay> displays = vehicleService.FindAllbyCreatedBy(SecurityContextHolder.getContext().getAuthentication().getName(),"");
        List<VehicleDisplay> filter = vehicleService.FindAllbyCreatedBy(SecurityContextHolder.getContext().getAuthentication().getName(),"test");

        assertEquals(2,displays.size());
        assertEquals(1,filter.size());
        assertEquals(entity1.getNumber(),filter.stream().findFirst().orElseThrow().getNumber());
    }

    @Test
    public void VehicleIntegration_FindByUpdatedBy()
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
        entity2.setIdentifier(vehicleService.FindAllVehicles("").stream().findFirst().orElseThrow().getIdentifier());
        vehicleService.UpdateVehicle(entity2);
        List<VehicleDisplay> updatedVehicle = vehicleService.FindAllbyUpdatedBy(SecurityContextHolder.getContext().getAuthentication().getName(),"");
        List<VehicleDisplay> updatedWithFilter = vehicleService.FindAllbyUpdatedBy(SecurityContextHolder.getContext().getAuthentication().getName(),"test");

        assertEquals(entity2.getNumber(),updatedVehicle.stream().findFirst().orElseThrow().getNumber());
        assertEquals(0,updatedWithFilter.size());
    }

    @Test
    public void VehicleIntegration_DownloadVehiclePdf()
    {
        VehicleDisplay entity1 = VehicleDisplay.builder()
                .number("3")
                .type("test")
                .status("t")
                .stand("365")
                .build();

        vehicleService.SaveVehicle(entity1);
        String identifier = vehicleService.FindAllVehicles("").stream().findFirst().orElseThrow().getIdentifier();
        byte[] pdf = vehicleService.DownloadVehiclePdf(identifier);

        assertNotNull(pdf);
    }

    @Test
    public void VehicleIntegration_VehicleCount()
    {
        VehicleDisplay entity1 = VehicleDisplay.builder()
                .number("3")
                .type("test")
                .status("t")
                .stand("365")
                .build();

        vehicleService.SaveVehicle(entity1);

        assertEquals(1,vehicleService.VehiclesCount());
    }

    @Test
    public void VehicleIntegration_DeleteVehicle()
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
        vehicleService.DeleteVehicle(vehicleService.FindAllVehicles("").stream().findFirst().orElseThrow().getIdentifier());
        List<VehicleDisplay> result = vehicleService.FindAllVehicles("");

        assertEquals(1, result.size());
        assertEquals(entity2.getNumber(),result.stream().findFirst().orElseThrow().getNumber());
    }
}
