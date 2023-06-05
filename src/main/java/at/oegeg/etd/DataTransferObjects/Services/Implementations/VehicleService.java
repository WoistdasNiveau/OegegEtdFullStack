package at.oegeg.etd.DataTransferObjects.Services.Implementations;

import at.oegeg.etd.DataTransferObjects.DisplayModels.VehicleDisplay;
import at.oegeg.etd.DataTransferObjects.DisplayModels.WorkDisplay;
import at.oegeg.etd.DataTransferObjects.Request.WorkRequest;
import at.oegeg.etd.DataTransferObjects.Response.UserResponse;
import at.oegeg.etd.DataTransferObjects.Response.VehicleResponse;
import at.oegeg.etd.DataTransferObjects.Response.WorkResponse;
import at.oegeg.etd.Entities.Enums.Priorities;
import at.oegeg.etd.Entities.UserEntity;
import at.oegeg.etd.Entities.VehicleEntity;
import at.oegeg.etd.Entities.WorkEntity;
import at.oegeg.etd.Repositories.IUserEntityRepository;
import at.oegeg.etd.Repositories.IVehicleRepository;
import at.oegeg.etd.Repositories.IWorkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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

    public long VehiclesCount()
    {
        return _vehicleRepository.count();
    }

    public void DeleteVehicle(String identifier)
    {
        VehicleEntity vehicle = _vehicleRepository.findByIdentifier(identifier).orElseThrow();
        _vehicleRepository.delete(vehicle);
    }

    public void SaveVehicle(VehicleDisplay vehicleRequest)//, String token)
    {
        //UserEntity user = _userRepository.findByIdentifier(_jwtService.ExtractUsername(token)).orElseThrow();
        VehicleEntity entity;
        entity = VehicleEntity.builder()
                .identifier(UUID.randomUUID().toString())
                .number(vehicleRequest.getNumber())
                .type(vehicleRequest.getType())
                .status(vehicleRequest.getStatus())
                .stand(vehicleRequest.getStand())
                .priority(Priorities.NONE)
                //.createdBy(user)
                .build();
        if(vehicleRequest.getWorks()!= null && vehicleRequest.getWorks().stream().count() > 0)
        {
            List<WorkEntity> works = WorkDisplayToWorkEntity(vehicleRequest.getWorks());
            works.forEach(t -> t.setVehicle(entity));
            //works.forEach(t -> t.setCreatedBy(user));
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
        _vehicleRepository.save(entity);
    }


    // == private methods ==

    private VehicleDisplay VehicleEntityToDisplay(VehicleEntity entity)
    {
        return VehicleDisplay.builder()
                .identifier(entity.getIdentifier())
                .number(entity.getNumber())
                .type(entity.getType())
                .status(entity.getStatus())
                .stand(entity.getStand())
                .priority(entity.getPriority())
                .workCount(entity.getWorkCount())
                .works(WorkEntityToWorkDisplay(entity.getWorks()))
                .build();
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
                .build())
                .collect(Collectors.toList());
    }

    private VehicleResponse VehicleEntityToVehicleResponse(VehicleEntity entity)
    {
        List<VehicleEntity> v = new ArrayList<>();
        v.add(entity);
        return (VehicleResponse)((List)VehicleEntitiesToVehicleDisplay(v)).stream().findFirst().orElseThrow();
    }
    private List<WorkEntity> WorkRequestToWorkEntity(List<WorkRequest> requests)
    {
        List<WorkEntity> works = new ArrayList<WorkEntity>();
        for(WorkRequest request : requests)
        {
            UserEntity user = _userRepository.findByEmailOrTelephoneNumberOrNameOrIdentifier(request.getResponsiblePersonEmailOrTelephoneNumber()).orElse(null);
            works.add(WorkEntity.builder()
                    .responsiblePerson(user)
                    .description(request.getDescription())
                    .priority(request.getPriority())
                    .identifier(UUID.randomUUID().toString())
                    .build());
        }
        return works;
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
                    .build());
        }
        return works;
    }

    private List<WorkDisplay> WorkEntityToWorkDisplay(List<WorkEntity> entities)
    {
        List<WorkDisplay> response = new ArrayList<>();
        for(WorkEntity entity : entities)
        {
            WorkDisplay r = WorkDisplay.builder()
                    .description(entity.getDescription())
                    .priority(entity.getPriority())
                    .identifier(entity.getIdentifier())
                    .build();
            if(entity.getResponsiblePerson() != null)
                r.setResponsiblePerson(entity.getResponsiblePerson().getName());
            response.add(r);
        }
        return response;
    }

    private List<WorkResponse> WorkEntityToWorkResponse(List<WorkEntity> entities)
    {
        List<WorkResponse> response = new ArrayList<>();
        for(WorkEntity entity : entities)
        {
            WorkResponse r = WorkResponse.builder()
                    .Description(entity.getDescription())
                    .Priority(entity.getPriority())
                    .identifier(entity.getIdentifier())
                    .build();
            if(entity.getResponsiblePerson() != null)
                r.setResponsiblePerson(entity.getResponsiblePerson().getName());
            response.add(r);
        }
        return response;
    }

    private UserResponse UserEntityToUserResponse(UserEntity user)
    {
        return UserResponse.builder()
                .name(user.getName())
                .email(user.getEmail())
                .telephoneNumber(user.getTelephoneNumber())
                .build();
    }
}
