package at.oegeg.etd.DataTransferObjects.Services.Implementations;

import at.oegeg.etd.DataTransferObjects.Request.VehicleRequest;
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
    public List<VehicleResponse> FindAllVehicles(String filterText)
    {
        if(filterText == null || filterText.isEmpty())
        {
            return VehicleEntitiesToVehicleResponse(_vehicleRepository.findAll());
        }
        return VehicleEntitiesToVehicleResponse(_vehicleRepository.findBySearchString(filterText));
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

    public void SaveVehicle(VehicleRequest vehicleRequest)//, String token)
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
            List<WorkEntity> works = WorkRequestToWorkEntity(vehicleRequest.getWorks());
            works.forEach(t -> t.setVehicle(entity));
            //works.forEach(t -> t.setCreatedBy(user));
            entity.setWorks(works);
        }
        _vehicleRepository.save(entity);
    }


    // == private methods ==
    private List<VehicleResponse> VehicleEntitiesToVehicleResponse(List<VehicleEntity> entities)
    {
        return entities.stream().map(e -> VehicleResponse.builder()
                        .identifier(e.getIdentifier())
                        .Number(e.getNumber())
                        .Type(e.getType())
                        .Status(e.getStatus())
                        .Stand(e.getStand())
                        .Priority(e.getPriority())
                        .workCount(_workRepository.countWorkEntityByVehicle(e))
                        .Works(WorkEntityToWorkResponse(e.getWorks())).build())
                .collect(Collectors.toList());
    }

    private VehicleResponse VehicleEntityToVehicleResponse(VehicleEntity entity)
    {
        List<VehicleEntity> v = new ArrayList<>();
        v.add(entity);
        return (VehicleResponse)((List)VehicleEntitiesToVehicleResponse(v)).stream().findFirst().orElseThrow();
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
