package at.oegeg.etd.DataTransferObjects.Request;

import at.oegeg.etd.Entities.Enums.Role;
import at.oegeg.etd.Entities.VehicleEntity;
import at.oegeg.etd.Entities.WorkEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRequest
{
    private String identifier;
    private String name;
    private String email;
    private String telephoneNumber;
    private Role role;
    private List<WorkEntity> responsibleFor;
    private List<WorkEntity> createdWorks;
    private List<VehicleEntity> createdVehicles;

    // replace with automated password genereated and change after first login
    private String password;
}
