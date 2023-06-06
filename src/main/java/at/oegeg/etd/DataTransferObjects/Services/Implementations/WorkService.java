package at.oegeg.etd.DataTransferObjects.Services.Implementations;

import at.oegeg.etd.DataTransferObjects.DisplayModels.WorkDisplay;
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
        if(workDisplay.getIdentifier().equals(""))
        {
            WorkEntity entity = WorkDisplayToEntity(workDisplay);
            entity.setVehicle(_vehicleRepository.findByIdentifier(vehicleIdentifier).orElseThrow());
            entity.setUpdatedBy(_userRepository.findByEmailOrTelephoneNumberOrNameOrIdentifier(VaadinSession.getCurrent().getAttribute("username")
                    .toString()).orElseThrow());
            _workRepository.save(entity);
        }
        else
        {
            WorkEntity workEntity = _workRepository.findByIdentifier(workDisplay.getIdentifier()).orElseThrow();
            workEntity.setResponsiblePerson(_userRepository.findByEmailOrTelephoneNumberOrNameOrIdentifier(workDisplay.getResponsiblePerson()).orElse(
                    _userRepository.findByEmailOrTelephoneNumberOrNameOrIdentifier("defaultUser").orElseThrow()
            ));
            workEntity.setPriority(workDisplay.getPriority());
            workEntity.setCreatedBy(_userRepository.findByEmailOrTelephoneNumberOrNameOrIdentifier(SecurityContextHolder.getContext().getAuthentication().getName())
                    .orElseThrow());
            workEntity.setDescription(workDisplay.getDescription());
            _workRepository.save(workEntity);
        }
    }

    @Transactional
    public void DeleteWork(String workIdentifier)
    {
        _workRepository.delete(_workRepository.findByIdentifier(workIdentifier).orElseThrow());
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
                .priority(w.getPriority())
                .build()).collect(Collectors.toList());
    }
}
