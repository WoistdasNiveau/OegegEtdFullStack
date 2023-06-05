package at.oegeg.etd.DataTransferObjects.Response;

import at.oegeg.etd.Entities.Enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.context.annotation.Bean;

import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserResponse
{
    private String identifier;
    private String name;
    private String email;
    private String telephoneNumber;
    public long responsibleForCount;
    public long createdWorksCount;
    public long createdVehiclesCount;
    public long updatedWorksCount;
    public long updatedVehiclesCount;
    public boolean isEnabled;
    public Role role;
    private List<WorkResponse> responsibleFor;
    private List<WorkResponse> createdWorks;
    private List<WorkResponse> updatedWorks;
    private List<VehicleResponse> createdVehicles;
    private List<VehicleResponse> updatedVehicles;
}
