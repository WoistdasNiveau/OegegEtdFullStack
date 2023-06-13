package at.oegeg.etd.Services.Implementations;

import at.oegeg.etd.DataTransferObjects.DisplayModels.VehicleDisplay;
import at.oegeg.etd.DataTransferObjects.DisplayModels.WorkDisplay;
import at.oegeg.etd.Entities.Enums.Priorities;
import at.oegeg.etd.Entities.UserEntity;
import at.oegeg.etd.Entities.VehicleEntity;
import at.oegeg.etd.Entities.WorkEntity;
import at.oegeg.etd.Repositories.IUserEntityRepository;
import at.oegeg.etd.Repositories.IVehicleRepository;
import at.oegeg.etd.Repositories.IWorkRepository;
import com.itextpdf.text.DocumentException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VehicleService
{
    // == private fields ==
    private final IVehicleRepository _vehicleRepository;
    private final IUserEntityRepository _userRepository;
    private final IWorkRepository _workRepository;
    private final PdfService _pdfService;

    // == public methods ==
    public List<VehicleDisplay> FindAllVehicles(String filterText)
    {
        if(filterText == null || filterText.isEmpty())
        {
            return VehicleEntitiesToVehicleDisplay(_vehicleRepository.findAll());
        }
        return VehicleEntitiesToVehicleDisplay(_vehicleRepository.findBySearchString(filterText));
    }

    public VehicleDisplay FindVehicleByIdentifier(String identifier)
    {
        VehicleEntity entity = _vehicleRepository.findByIdentifier(identifier).orElseThrow();
        return VehicleEntityToDisplay(entity);
    }

    public List<VehicleDisplay> FindAllbyCreatedBy(String userIdentifier, String filter)
    {
        UserEntity user = _userRepository.findByIdentifier(userIdentifier).orElseThrow();
        if(filter == null || filter.equals(""))
        {
            return VehicleEntitiesToVehicleDisplay(_vehicleRepository.findAllByCreatedBy(user).orElse(new ArrayList<>()));
        }
        return VehicleEntitiesToVehicleDisplay(_vehicleRepository.findBySearchStringAndCreatedBy(filter,user));
    }

    public List<VehicleDisplay> FindAllbyUpdatedBy(String userIdentifier, String filter)
    {
        UserEntity user = _userRepository.findByIdentifier(userIdentifier).orElseThrow();
        if(filter == null || filter.equals(""))
        {
            return VehicleEntitiesToVehicleDisplay(_vehicleRepository.findAllByUpdatedBy(user).orElse(new ArrayList<>()));
        }
        return VehicleEntitiesToVehicleDisplay(_vehicleRepository.findBySearchStringAndUpdatedBy(filter, user));
    }

    public byte[] DownloadVehiclePdf(String identifier)
    {
        VehicleEntity entity = _vehicleRepository.findByIdentifier(identifier).orElseThrow();
        try
        {
            return _pdfService.GenerateVehiclePdf(entity);
        }
        catch (Exception ex)
        {
            return null;
        }
    }

    public long VehiclesCount()
    {
        return _vehicleRepository.count();
    }

    public void DeleteVehicle(String identifier)
    {
        VehicleEntity vehicle = _vehicleRepository.findByIdentifier(identifier).orElseThrow();
        _vehicleRepository.delete(vehicle);
    }

    public void SaveVehicle(VehicleDisplay vehicleDisplay)
    {
        VehicleEntity entity = VehicleDisplayToEntity(vehicleDisplay);
        entity.getCreatedBy().getCreatedVehicles().add(entity);
        if(vehicleDisplay.getWorks()!= null && vehicleDisplay.getWorks().stream().count() > 0)
        {
            List<WorkEntity> works = WorkDisplayToWorkEntity(vehicleDisplay.getWorks());
            works.forEach(t -> t.setVehicle(entity));
            entity.setWorks(works);
        }
        _vehicleRepository.save(entity);
    }

    public void UpdateVehicle(VehicleDisplay display)
    {
        VehicleEntity entity = _vehicleRepository.findByIdentifier(display.getIdentifier()).orElseThrow();
        entity.setNumber(display.getNumber());
        entity.setType(display.getType());
        entity.setStatus(display.getStatus());
        entity.setStand(display.getStand());
        entity.setUpdatedBy(_userRepository.findByEmailOrTelephoneNumberOrNameOrIdentifier(SecurityContextHolder.getContext().getAuthentication().getName()).orElseThrow());
        _vehicleRepository.save(entity);
    }

    // == private methods ==
    private VehicleEntity VehicleDisplayToEntity(VehicleDisplay display)
    {
        return VehicleDisplaysToEntities(List.of(display)).stream().findFirst().orElseThrow();
    }

    private List<VehicleEntity> VehicleDisplaysToEntities(List<VehicleDisplay> displays)
    {
        return displays.stream().map(t -> VehicleEntity.builder()
                .identifier(UUID.randomUUID().toString())
                .number(t.getNumber())
                .type(t.getType())
                .status(t.getStatus())
                .stand(t.getStand())
                .priority(Priorities.NONE)
                .createdBy(_userRepository.findByEmailOrTelephoneNumberOrNameOrIdentifier(SecurityContextHolder.getContext().getAuthentication().getName()).orElseThrow())
                .works(new ArrayList<>())
                .build())
                .collect(Collectors.toList());
    }

    private VehicleDisplay VehicleEntityToDisplay(VehicleEntity entity)
    {
        return VehicleEntitiesToVehicleDisplay(List.of(entity)).stream().findFirst().orElseThrow();
    }
    private List<VehicleDisplay> VehicleEntitiesToVehicleDisplay(List<VehicleEntity> entities)
    {
        return entities.stream().map(e -> VehicleDisplay.builder()
                        .identifier(e.getIdentifier())
                        .number(e.getNumber())
                        .type(e.getType())
                        .status(e.getStatus())
                        .stand(e.getStand())
                        .workCount(e.getWorkCount() != null ? e.getWorkCount() : 0)
                        .works(WorkEntitiesToDisplays(e.getWorks()))
                        .build())
                        .collect(Collectors.toList());
    }

    private List<WorkEntity> WorkDisplayToWorkEntity(List<WorkDisplay> requests)
    {
        List<WorkEntity> works = new ArrayList<WorkEntity>();
        for(WorkDisplay request : requests)
        {
            UserEntity user = _userRepository.findByEmailOrTelephoneNumberOrNameOrIdentifier(request.getResponsiblePerson()).orElse(null);
            works.add(WorkEntity.builder()
                    .responsiblePerson(user)
                    .description(request.getDescription())
                    .priority(request.getPriority())
                    .identifier(UUID.randomUUID().toString())
                    .createdBy(_userRepository.findByEmailOrTelephoneNumberOrNameOrIdentifier(SecurityContextHolder.getContext().getAuthentication().getName()).orElseThrow())
                    .build());
        }
        return works;
    }

    private List<WorkDisplay> WorkEntitiesToDisplays(List<WorkEntity> entities)
    {
        return entities.stream().map(t -> WorkDisplay.builder()
                .identifier(t.getIdentifier())
                .vehicle(t.getVehicle().getNumber())
                .vehicleIdentifier(t.getVehicle().getIdentifier())
                .responsiblePerson(t.getResponsiblePerson() != null ? t.getResponsiblePerson().getName() : "")
                .description(t.getDescription())
                .priority( t.getPriority())
                .createdBy(t.getCreatedBy().getName())
                .updatedBy(t.getUpdatedBy() != null ? t.getUpdatedBy().getName() : "")
                .build()).collect(Collectors.toList());
    }

}
