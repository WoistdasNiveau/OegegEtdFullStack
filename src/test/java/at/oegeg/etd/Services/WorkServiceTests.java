package at.oegeg.etd.Services;

import at.oegeg.etd.DataTransferObjects.DisplayModels.WorkDisplay;
import at.oegeg.etd.Entities.UserEntity;
import at.oegeg.etd.Entities.VehicleEntity;
import at.oegeg.etd.Entities.WorkEntity;
import at.oegeg.etd.Repositories.IUserEntityRepository;
import at.oegeg.etd.Repositories.IVehicleRepository;
import at.oegeg.etd.Repositories.IWorkRepository;
import at.oegeg.etd.Services.Implementations.WorkService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static org.hamcrest.Matchers.any;
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
    @Mock
    private WorkService workService;

    // == tests ==
    @Test
    public void WorkService_SaveAll()
    {
        VehicleEntity vehicleEntity = Mockito.mock(VehicleEntity.class);
        UserEntity user = Mockito.mock(UserEntity.class);
        WorkEntity entity = Mockito.mock(WorkEntity.class);
        WorkDisplay display = Mockito.mock(WorkDisplay.class);

        when(vehicleRepository.findByIdentifier(anyString())).thenReturn(Optional.of(vehicleEntity));
        when(userEntityRepository.findByIdentifier(anyString())).thenReturn(Optional.of(user));

        workService.SaveWork(display,"3");

        Mockito.verify(userEntityRepository).s
    }
}
