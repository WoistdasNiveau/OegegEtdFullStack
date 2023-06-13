package at.oegeg.etd.Services.Implementations;

import at.oegeg.etd.DataTransferObjects.DisplayModels.WorkDisplay;
import at.oegeg.etd.Entities.Enums.Priorities;
import at.oegeg.etd.Entities.UserEntity;
import at.oegeg.etd.Entities.VehicleEntity;
import at.oegeg.etd.Entities.WorkEntity;
import at.oegeg.etd.Repositories.IUserEntityRepository;
import at.oegeg.etd.Repositories.IVehicleRepository;
import at.oegeg.etd.Repositories.IWorkRepository;
import com.vaadin.flow.server.VaadinRequest;
import com.vaadin.flow.server.VaadinSession;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WorkService
{
    // == private fields ==
    private final IWorkRepository _workRepository;
    private final IUserEntityRepository _userRepository;
    private final IVehicleRepository _vehicleRepository;

    // == public methods ==
    public void SaveWork(WorkDisplay workDisplay, String vehicleIdentifier)
    {
        if(workDisplay.getIdentifier() == null || workDisplay.getIdentifier().equals(""))
        {
            WorkEntity entity = WorkDisplayToEntity(workDisplay);
            VehicleEntity vehicle = _vehicleRepository.findByIdentifier(vehicleIdentifier).orElseThrow();
            entity.setVehicle(vehicle);
            entity.setCreatedBy(_userRepository.findByEmailOrTelephoneNumberOrNameOrIdentifier(SecurityContextHolder.getContext().getAuthentication().getName()).orElseThrow());
            vehicle.getWorks().add(entity);
            _workRepository.save(entity);
            _vehicleRepository.save(vehicle);
        }
        else
        {
            WorkEntity workEntity = _workRepository.findByIdentifier(workDisplay.getIdentifier()).orElseThrow();
            workEntity.setResponsiblePerson(_userRepository.findByEmailOrTelephoneNumberOrNameOrIdentifier(workDisplay.getResponsiblePerson()).orElse(
                    _userRepository.findByEmailOrTelephoneNumberOrNameOrIdentifier("defaultUser").orElseThrow()
            ));
            workEntity.setPriority(workDisplay.getPriority());
            workEntity.setUpdatedBy(_userRepository.findByEmailOrTelephoneNumberOrNameOrIdentifier(SecurityContextHolder.getContext().getAuthentication().getName())
                    .orElseThrow());
            workEntity.setDescription(workDisplay.getDescription());
            _workRepository.save(workEntity);
        }
    }

    public List<WorkDisplay> FindAllByVehicle(String vehicleIdentifier, String filter)
    {
        if(filter == null || filter.equals(""))
        {
            return WorkEntitiesToDisplay(_workRepository.findByVehicle(_vehicleRepository.findByIdentifier(vehicleIdentifier).orElseThrow()).orElseThrow());
        }
        return WorkEntitiesToDisplay(_workRepository.findByVehicleAndSearchString(vehicleIdentifier, filter));
    }

    public List<WorkDisplay> FindAllByResponsiblePerson(String responsiblePersonIdentifier, String filter)
    {
        UserEntity user = _userRepository.findByIdentifier(responsiblePersonIdentifier).orElseThrow();
        if(filter == null || filter.equals(""))
        {
            return WorkEntitiesToDisplay(_workRepository.findByResponsiblePerson(user).orElse(new ArrayList<>()));
        }
        return  WorkEntitiesToDisplay(_workRepository.findByResponsiblePersonAndSearchString(user,filter));
    }

    public List<WorkDisplay> FindAllByCreatedBy(String createdByIdentifier, String filter)
    {
        UserEntity user = _userRepository.findByIdentifier(createdByIdentifier).orElseThrow();
        if(filter == null || filter.equals(""))
        {
            return WorkEntitiesToDisplay(_workRepository.findByCreatedBy(user).orElse(new ArrayList<>()));
        }
        return WorkEntitiesToDisplay(_workRepository.findByCreatedByAndSearchString(user,filter));
    }

    public List<WorkDisplay> FindAllByUpdatedBy(String updatedByIdentifier, String filter)
    {
        UserEntity user = _userRepository.findByIdentifier(updatedByIdentifier).orElseThrow();
        if(filter == null || filter.equals(""))
        {
            return WorkEntitiesToDisplay(_workRepository.findByUpdatedBy(user).orElse(new ArrayList<>()));
        }
        return WorkEntitiesToDisplay(_workRepository.findByUpdatedByAndSearchString(user,filter));
    }

    @Transactional
    public void DeleteWork(String workIdentifier)
    {
        WorkEntity work = _workRepository.findByIdentifier(workIdentifier).orElseThrow();
        work.getVehicle().getWorks().remove(work);
        _vehicleRepository.save(work.getVehicle());
        _workRepository.delete(work);
    }

    // == private methods
    private WorkEntity WorkDisplayToEntity(WorkDisplay display)
    {
        return WorkDisplaysToEntities(List.of(display)).stream().findFirst().orElseThrow();
    }

    private List<WorkEntity> WorkDisplaysToEntities(List<WorkDisplay> workDisplays)
    {
        return workDisplays.stream().map(w -> WorkEntity.builder()
                .identifier(UUID.randomUUID().toString())
                .responsiblePerson(_userRepository.findByEmailOrTelephoneNumberOrNameOrIdentifier(w.getResponsiblePerson()).orElse(
                        _userRepository.findByEmailOrTelephoneNumberOrNameOrIdentifier("defaultUser").orElseThrow()))
                .description(w.getDescription())
                .priority(w.getPriority() != null ? w.getPriority() : Priorities.NONE)
                .build()).collect(Collectors.toList());
    }

    private WorkDisplay WorkEntityToDisplay(WorkEntity entity)
    {
        return WorkEntitiesToDisplay(List.of(entity)).stream().findFirst().orElseThrow();
    }
    private List<WorkDisplay> WorkEntitiesToDisplay(List<WorkEntity> entities)
    {
        return entities.stream().map(w -> WorkDisplay.builder()
                .identifier(w.getIdentifier())
                .vehicle(w.getVehicle().getNumber())
                .vehicleIdentifier(w.getVehicle().getIdentifier())
                .responsiblePerson(w.getResponsiblePerson() != null ? w.getResponsiblePerson().getName() : "-")
                .description(w.getDescription())
                .priority(w.getPriority())
                .createdBy(w.getCreatedBy().getName())
                .updatedBy(w.getUpdatedBy() != null ? w.getUpdatedBy().getName() : "")
                .build()).collect(Collectors.toList());
    }
}
