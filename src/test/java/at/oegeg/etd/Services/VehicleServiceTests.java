package at.oegeg.etd.Services;

import at.oegeg.etd.DataTransferObjects.DisplayModels.VehicleDisplay;
import at.oegeg.etd.Entities.UserEntity;
import at.oegeg.etd.Entities.VehicleEntity;
import at.oegeg.etd.Repositories.IUserEntityRepository;
import at.oegeg.etd.Repositories.IVehicleRepository;
import at.oegeg.etd.Services.Implementations.PdfService;
import at.oegeg.etd.Services.Implementations.VehicleService;
import com.itextpdf.text.DocumentException;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.AdditionalMatchers.not;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@RunWith(SpringRunner.class)
public class VehicleServiceTests
{
    @Mock
    private IVehicleRepository vehicleRepository;
    @Mock
    private IUserEntityRepository userEntityRepository;
    @Mock
    private PdfService pdfService;
    @Mock
    private SecurityContextHolder securityContextHolder;

    @InjectMocks
    private VehicleService vehicleService;

    @Test
    public void VehicleService_FindAllVehicles_ReturnsSavedVehicle()
    {
        String filterText = "test";
        List<VehicleEntity> vehicleEntities = new ArrayList<>();
        vehicleEntities.add(new VehicleEntity());

        // Mock repository behavior
        when(vehicleRepository.findAll()).thenReturn(vehicleEntities);
        when(vehicleRepository.findBySearchString(anyString())).thenReturn(vehicleEntities);

        // Call the method under test
        List<VehicleDisplay> result = vehicleService.FindAllVehicles(filterText);

        // Assert the result
        Assertions.assertEquals(vehicleEntities.size(), result.size());
    }

    @Test
    public void VehicleService_FindVehicleByIdentifier_ReturnsVehicleByIdentifer()
    {
        String identifier = UUID.randomUUID().toString();
        VehicleEntity entity = VehicleEntity.builder()
                .identifier(identifier)
                .number("123")
                .type("ujh")
                .status("o")
                .stand("123")
                .build();
        VehicleDisplay display = VehicleDisplay.builder()
                .identifier(identifier)
                .number("123")
                .type("ujh")
                .status("o")
                .stand("123")
                .workCount(0L)
                .build();


        when(vehicleRepository.findByIdentifier(eq(identifier))).thenReturn(Optional.of(entity));
        when(vehicleRepository.findByIdentifier(not(eq(identifier)))).thenThrow(new NoSuchElementException());

        Assertions.assertEquals(display, vehicleService.FindVehicleByIdentifier(identifier));
        Assertions.assertThrowsExactly(NoSuchElementException.class,() -> vehicleService.FindVehicleByIdentifier("3"));
    }

    @Test
    public void VehicleService_FindAllbyCreatedBy_ReturnsDisplayAndError()
    {
        UserEntity user = Mockito.mock(UserEntity.class);
        VehicleEntity entity = Mockito.mock(VehicleEntity.class);
        String identifier = UUID.randomUUID().toString();

        when(user.getIdentifier()).thenReturn(identifier);
        when(userEntityRepository.findByIdentifier(anyString())).thenReturn(Optional.of(user));
        when(userEntityRepository.findByIdentifier(not(eq(identifier)))).thenThrow(new NoSuchElementException());
        when(vehicleRepository.findBySearchStringAndCreatedBy(anyString(),any(UserEntity.class))).thenReturn(List.of(entity));

        vehicleService.FindAllbyCreatedBy(user.getIdentifier(),"");
        vehicleService.FindAllbyCreatedBy(user.getIdentifier(),"test");

        Assertions.assertThrowsExactly(NoSuchElementException.class, () -> vehicleService.FindAllbyCreatedBy("3",""));
        Mockito.verify(vehicleRepository).findAllByCreatedBy(user);
        Mockito.verify(vehicleRepository).findBySearchStringAndCreatedBy("test",user);
    }

    @Test
    public void VehicleService_FindAllbyUpdatedBy_ReturnsDisplayAndError()
    {
        UserEntity user = Mockito.mock(UserEntity.class);
        VehicleEntity entity = Mockito.mock(VehicleEntity.class);
        String identifier = UUID.randomUUID().toString();

        when(user.getIdentifier()).thenReturn(identifier);
        when(userEntityRepository.findByIdentifier(anyString())).thenReturn(Optional.of(user));
        when(userEntityRepository.findByIdentifier(not(eq(identifier)))).thenThrow(new NoSuchElementException());
        when(vehicleRepository.findBySearchStringAndUpdatedBy(anyString(),any(UserEntity.class))).thenReturn(List.of(entity));

        vehicleService.FindAllbyUpdatedBy(user.getIdentifier(),"");
        vehicleService.FindAllbyUpdatedBy(user.getIdentifier(),"test");

        Assertions.assertThrowsExactly(NoSuchElementException.class, () -> vehicleService.FindAllbyUpdatedBy("3",""));
        Mockito.verify(vehicleRepository).findAllByUpdatedBy(user);
        Mockito.verify(vehicleRepository).findBySearchStringAndUpdatedBy("test",user);
    }

    @Test
    public void VehicleService_GeneratePdf_ReturnsPdf() throws DocumentException, IOException
    {
        UserEntity user = Mockito.mock(UserEntity.class);
        VehicleEntity entity = Mockito.mock(VehicleEntity.class);
        byte[] pdf = new byte[]{1,3,52};
        String identifier = UUID.randomUUID().toString();
        String userIdentifier = UUID.randomUUID().toString();

        user.setIdentifier(userIdentifier);

        when(vehicleRepository.findByIdentifier(identifier)).thenReturn(Optional.of(entity));
        when(pdfService.GenerateVehiclePdf(any(VehicleEntity.class))).thenReturn(pdf);

        Assertions.assertEquals(pdf, vehicleService.DownloadVehiclePdf(identifier));
    }

    @Test
    public void VehicleService_VehiclesCount_Returns1()
    {
        when(vehicleRepository.count()).thenReturn(1L);

        assertEquals(1,vehicleService.VehiclesCount());
    }

    @Test
    public void VehicleService_DeleteVehicle_RepositoryCount0()
    {
        VehicleEntity entity = Mockito.mock(VehicleEntity.class);

        when(vehicleRepository.findByIdentifier(anyString())).thenReturn(Optional.of(entity));

        vehicleService.DeleteVehicle("3");

        Mockito.verify(vehicleRepository).delete(entity);
    }

    @Test
    public void VehicleService_SaveVehicleWithoutWorks_returnsNothing()
    {
        // Arrange
        Authentication authentication = Mockito.mock(Authentication.class);
        VehicleDisplay vehicleDisplay = new VehicleDisplay();
        vehicleDisplay.setNumber("123");
        vehicleDisplay.setType("Car");
        vehicleDisplay.setStatus("Active");
        vehicleDisplay.setStand("A1");

        // Mock the repository
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        VehicleEntity savedEntity = new VehicleEntity();
        when(vehicleRepository.save(any(VehicleEntity.class))).thenReturn(savedEntity);
        when(userEntityRepository.findByEmailOrTelephoneNumberOrNameOrIdentifier(SecurityContextHolder.getContext().getAuthentication()
                        .getName())).thenReturn(Optional.of(new UserEntity()));

        // Act
        vehicleService.SaveVehicle(vehicleDisplay);

        // Assert
        Mockito.verify(vehicleRepository).save(any(VehicleEntity.class));
    }

    @Test
    public void VehicleService_UpdateVehicle()
    {
        String id = "3";
        VehicleDisplay display = Mockito.mock(VehicleDisplay.class);
        VehicleEntity entity = Mockito.mock(VehicleEntity.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Authentication authentication = Mockito.mock(Authentication.class);


        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(vehicleRepository.findByIdentifier(anyString())).thenReturn(Optional.ofNullable(entity));
        when(userEntityRepository.findByEmailOrTelephoneNumberOrNameOrIdentifier(SecurityContextHolder.getContext().getAuthentication()
                .getName())).thenReturn(Optional.of(new UserEntity()));

        vehicleService.SaveVehicle(display);

        Mockito.verify(vehicleRepository).save(any(VehicleEntity.class));
    }
}


















