package at.oegeg.etd.IntegrationTests;

import at.oegeg.etd.Configs.TestConfig;
import at.oegeg.etd.DataTransferObjects.DisplayModels.VehicleDisplay;
import at.oegeg.etd.DataTransferObjects.DisplayModels.WorkDisplay;
import at.oegeg.etd.Entities.Enums.Role;
import at.oegeg.etd.Entities.UserEntity;
import at.oegeg.etd.Entities.VehicleEntity;
import at.oegeg.etd.Repositories.IUserEntityRepository;
import at.oegeg.etd.Repositories.IVehicleRepository;
import at.oegeg.etd.Repositories.IWorkRepository;
import at.oegeg.etd.Services.Implementations.VehicleService;
import at.oegeg.etd.Services.Implementations.WorkService;
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

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@RunWith(SpringRunner.class)
@ComponentScan("at.oegeg.etd.Services.Implementations")
@ContextConfiguration(classes = {TestConfig.class})
public class WorkIntegrationTests
{
    @Autowired
    private WorkService workService;
    @Autowired
    private IWorkRepository workRepository;
    @Autowired
    private VehicleService vehicleService;
    @Autowired
    IVehicleRepository vehicleRepository;
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
                .isUserEnabled(true)
                .role(Role.ADMIN)
                .createdWorks(new ArrayList<>())
                .createdVehicles(new ArrayList<>())
                .updatedWorks(new ArrayList<>())
                .updatedVehicles(new ArrayList<>())
                .build();

        VehicleEntity vehicle = VehicleEntity.builder()
                .identifier("3")
                .number("52")
                .type("fadhsdfh")
                .status("t")
                .stand("fdhfdh")
                .createdBy(user)
                .works(new ArrayList<>())
                .build();

        user.getCreatedVehicles().add(vehicle);

        userRepository.save(user);
        vehicleRepository.save(vehicle);

        // Create an authentication token for the user
        Authentication authentication = new UsernamePasswordAuthenticationToken(user, user.getPassword(), user.getAuthorities());

        // Set up the SecurityContext with the authentication token
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    // == tests ==
    @Test
    public void WorkIntegration_SaveWork()
    {
        WorkDisplay display = WorkDisplay.builder()
                .description("fhfdh")
                .build();

        workService.SaveWork(display,"3");
        VehicleDisplay vehicle = vehicleService.FindVehicleByIdentifier("3");

        assertEquals(1, vehicle.getWorks().size());

        WorkDisplay work = vehicle.getWorks().stream().findFirst().orElseThrow();

        assertEquals("3", vehicle.getWorks().stream().findFirst().orElseThrow().getVehicleIdentifier());
        assertNotNull(work.getIdentifier());
        assertFalse(work.getIdentifier().isBlank() || work.getIdentifier().isEmpty());
        assertNotNull(work.getVehicle());
        assertFalse(work.getVehicle().isBlank() || work.getVehicle().isEmpty());
        assertNotNull(work.getDescription());
        assertFalse(work.getDescription().isBlank() || work.getDescription().isEmpty());
        assertNotNull(work.getPriority());
        assertNotNull(work.getCreatedBy());
        assertFalse(work.getCreatedBy().isBlank() || work.getCreatedBy().isEmpty());
    }

    @Test
    public void WorkIntegration_FindAllByVehicle()
    {
        WorkDisplay display1 = WorkDisplay.builder()
                .description("test")
                .build();
        WorkDisplay display2 = WorkDisplay.builder()
                .description("noDescr")
                .build();

        workService.SaveWork(display1,"3");
        workService.SaveWork(display2,"3");
        List<WorkDisplay> all = workService.FindAllByVehicle("3","");
        List<WorkDisplay> filter = workService.FindAllByVehicle("3","test");

        assertEquals(2, all.size());
        assertEquals(1, filter.size());
        assertEquals(display1.getDescription(), filter.stream().findFirst().orElseThrow().getDescription());
    }

    @Test
    public void WorkIntegration_FindAllByResponsiblePerson()
    {
        WorkDisplay display1 = WorkDisplay.builder()
                .description("test")
                .responsiblePerson("3")
                .build();
        WorkDisplay display2 = WorkDisplay.builder()
                .description("noDescr")
                .build();

        workService.SaveWork(display1,"3");
        workService.SaveWork(display2,"3");

        List<WorkDisplay> all = workService.FindAllByResponsiblePerson("3","");
        List<WorkDisplay> filter = workService.FindAllByResponsiblePerson("3","noDescr");

        assertEquals(1, all.size());
        assertEquals(0,filter.size());
    }

    @Test
    public void WorkIntegration_FindAllByCreatedBy()
    {
        WorkDisplay display1 = WorkDisplay.builder()
                .description("test")
                .build();
        WorkDisplay display2 = WorkDisplay.builder()
                .description("noDescr")
                .build();

        workService.SaveWork(display1,"3");
        workService.SaveWork(display2,"3");

        List<WorkDisplay> all = workService.FindAllByCreatedBy("3","");
        List<WorkDisplay> filter = workService.FindAllByCreatedBy("3","test");

        assertEquals(2, all.size());
        assertEquals(1,filter.size());
        assertEquals(display1.getDescription(), filter.stream().findFirst().orElseThrow().getDescription());
    }

    @Test
    public void WorkIntegration_FindAllByUpdatedBy()
    {
        WorkDisplay display1 = WorkDisplay.builder()
                .description("test")
                .build();
        WorkDisplay display2 = WorkDisplay.builder()
                .description("noDescr")
                .build();

        workService.SaveWork(display1,"3");

        WorkDisplay toUpdate = workService.FindAllByVehicle("3","test").stream().findFirst().orElseThrow();
        display2.setIdentifier(toUpdate.getIdentifier());
        workService.SaveWork(display2,"3");
        List<WorkDisplay> updated = workService.FindAllByUpdatedBy("3","");
        List<WorkDisplay> updatedFilter = workService.FindAllByUpdatedBy("3","test");

        assertEquals(1, updated.size());
        assertEquals(0, updatedFilter.size());
        assertEquals(display2.getDescription(), updated.stream().findFirst().orElseThrow().getDescription());
        assertEquals("test", updated.stream().findFirst().orElseThrow().getUpdatedBy());
    }

    @Test
    public void WorkIntegration_DeleteWork()
    {
        WorkDisplay display1 = WorkDisplay.builder()
                .description("test")
                .build();
        WorkDisplay display2 = WorkDisplay.builder()
                .description("noDescr")
                .build();

        workService.SaveWork(display1,"3");
        workService.SaveWork(display2,"3");

        List<WorkDisplay> worksBeforeDelete = workService.FindAllByVehicle("3","");
        workService.DeleteWork(worksBeforeDelete.stream().findFirst().orElseThrow().getIdentifier());
        List<WorkDisplay> works = workService.FindAllByVehicle("3","");
        VehicleDisplay vehicle = vehicleService.FindVehicleByIdentifier("3");

        assertEquals(1,works.size());
        assertEquals(1,vehicle.getWorks().size());
    }
}






























