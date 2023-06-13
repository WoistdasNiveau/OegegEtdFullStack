package at.oegeg.etd.Services;

import at.oegeg.etd.DataTransferObjects.DisplayModels.VehicleDisplay;
import at.oegeg.etd.DataTransferObjects.DisplayModels.WorkDisplay;
import at.oegeg.etd.Entities.UserEntity;
import at.oegeg.etd.Entities.VehicleEntity;
import at.oegeg.etd.Entities.WorkEntity;
import at.oegeg.etd.Repositories.IUserEntityRepository;
import at.oegeg.etd.Repositories.IVehicleRepository;
import at.oegeg.etd.Repositories.IWorkRepository;
import at.oegeg.etd.Services.Implementations.WorkService;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@RunWith(SpringRunner.class)
public class WorkServiceTests
{
    // == private fields ==
    @Mock
    private IWorkRepository workRepository;
    @Mock
    private IVehicleRepository vehicleRepository;
    @Mock
    private IUserEntityRepository userEntityRepository;
    @InjectMocks
    private WorkService workService;

    // == tests ==
    @Test
    public void WorkService_SaveAll()
    {
        WorkDisplay display = Mockito.mock(WorkDisplay.class);
        WorkEntity workEntity = Mockito.mock(WorkEntity.class);
        VehicleEntity vehicle = Mockito.mock(VehicleEntity.class);
        UserEntity user = Mockito.mock(UserEntity.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Authentication authentication = Mockito.mock(Authentication.class);
        String id= "3";
        WorkDisplay identifierDisplay = WorkDisplay.builder()
                .identifier("3")
                .vehicle("24")
                .description("fadhsfgdh")
                .build();

        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(vehicleRepository.findByIdentifier(anyString())).thenReturn(Optional.ofNullable(vehicle));
        when(userEntityRepository.findByEmailOrTelephoneNumberOrNameOrIdentifier(SecurityContextHolder.getContext().getAuthentication().getName()))
                .thenReturn(Optional.ofNullable(user));
        when(userEntityRepository.findByEmailOrTelephoneNumberOrNameOrIdentifier(anyString())).thenReturn(Optional.ofNullable(user));
        when(workRepository.findByIdentifier(anyString())).thenReturn(Optional.ofNullable(workEntity));

        workService.SaveWork(display,"");
        workService.SaveWork(identifierDisplay,"");

        Mockito.verify(vehicleRepository).findByIdentifier(anyString());
        Mockito.verify(workRepository).findByIdentifier(id);
    }

    @Test
    public void WorkService_FindAllByVehicle_TwoCalls()
    {
        VehicleEntity vehicle = Mockito.mock(VehicleEntity.class);
        vehicle.setNumber("56");
        UserEntity user = Mockito.mock(UserEntity.class);
        user.setName("name");
        String identifier = "546";
        WorkEntity work = Mockito.mock(WorkEntity.class);
        work.setVehicle(vehicle);

        when(workRepository.findByVehicle(any(VehicleEntity.class))).thenReturn(Optional.of(List.of(work)));
        when(work.getVehicle()).thenReturn(vehicle);
        when(vehicle.getNumber()).thenReturn("325");
        when(vehicle.getIdentifier()).thenReturn("5757");
        when(vehicleRepository.findByIdentifier(anyString())).thenReturn(Optional.ofNullable(vehicle));
        when(userEntityRepository.)

        workService.FindAllByVehicle(identifier,"");
        workService.FindAllByVehicle(identifier,"3");

        Mockito.verify(workRepository).findByVehicle(any(VehicleEntity.class));
        Mockito.verify(workRepository).findByVehicleAndSearchString(anyString(), anyString());
    }

    @Test
    public void WorkService_FindAllByResponsiblePerson_TwoCalls()
    {
        VehicleEntity vehicle = Mockito.mock(VehicleEntity.class);
        UserEntity user = Mockito.mock(UserEntity.class);
        user.setName("name");
        String identifier = "546";
        WorkEntity work = Mockito.mock(WorkEntity.class);

        when(userEntityRepository.findByIdentifier(anyString())).thenReturn(Optional.of(user));
        when(work.getVehicle()).thenReturn(vehicle);
        when(vehicle.getNumber()).thenReturn("325");
        when(vehicle.getIdentifier()).thenReturn("5757");
        when(vehicleRepository.findByIdentifier(anyString())).thenReturn(Optional.ofNullable(vehicle));

        workService.FindAllByResponsiblePerson(identifier,"");
        workService.FindAllByResponsiblePerson(identifier,"3");

        Mockito.verify(workRepository).findByResponsiblePerson(any(UserEntity.class));
        Mockito.verify(workRepository).findByResponsiblePersonAndSearchString(any(UserEntity.class), anyString());
    }

    @Test
    public void WorkService_FindAllByCreatedBy_TwoCalls()
    {
        VehicleEntity vehicle = Mockito.mock(VehicleEntity.class);
        UserEntity user = Mockito.mock(UserEntity.class);
        user.setName("name");
        String identifier = "546";
        WorkEntity work = Mockito.mock(WorkEntity.class);

        when(userEntityRepository.findByIdentifier(anyString())).thenReturn(Optional.of(user));
        when(work.getVehicle()).thenReturn(vehicle);
        when(vehicle.getNumber()).thenReturn("325");
        when(vehicle.getIdentifier()).thenReturn("5757");
        when(vehicleRepository.findByIdentifier(anyString())).thenReturn(Optional.ofNullable(vehicle));

        workService.FindAllByCreatedBy(identifier,"");
        workService.FindAllByCreatedBy(identifier,"3");

        Mockito.verify(workRepository).findByCreatedBy(any(UserEntity.class));
        Mockito.verify(workRepository).findByCreatedByAndSearchString(any(UserEntity.class), anyString());
    }

    @Test
    public void WorkService_FindAllByUpdatedBy_TwoCalls()
    {
        VehicleEntity vehicle = Mockito.mock(VehicleEntity.class);
        UserEntity user = Mockito.mock(UserEntity.class);
        user.setName("name");
        String identifier = "546";
        WorkEntity work = Mockito.mock(WorkEntity.class);

        when(userEntityRepository.findByIdentifier(anyString())).thenReturn(Optional.of(user));
        when(work.getVehicle()).thenReturn(vehicle);
        when(vehicle.getNumber()).thenReturn("325");
        when(vehicle.getIdentifier()).thenReturn("5757");
        when(vehicleRepository.findByIdentifier(anyString())).thenReturn(Optional.ofNullable(vehicle));

        workService.FindAllByUpdatedBy(identifier,"");
        workService.FindAllByUpdatedBy(identifier,"3");

        Mockito.verify(workRepository).findByUpdatedBy(any(UserEntity.class));
        Mockito.verify(workRepository).findByUpdatedByAndSearchString(any(UserEntity.class), anyString());
    }

    @Test
    public void WorkService_DelteWork()
    {
        WorkEntity work = WorkEntity.builder()
                .identifier("3")
                .description("fgdhsfgdh")
                .build();
        VehicleEntity vehicle = VehicleEntity.builder()
                .identifier("67")
                .works(new ArrayList<>())
                .build();
        vehicle.getWorks().add(work);
        work.setVehicle(vehicle);

        when(workRepository.findByIdentifier(anyString())).thenReturn(Optional.of(work));

        workService.DeleteWork("3");

        Mockito.verify(vehicleRepository).save(vehicle);
        Assertions.assertEquals(0,vehicle.getWorks().size());
    }
}
