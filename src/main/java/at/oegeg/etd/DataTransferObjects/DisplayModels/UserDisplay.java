package at.oegeg.etd.DataTransferObjects.DisplayModels;

import at.oegeg.etd.Entities.Enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDisplay
{
    private String identifier;
    private String password;
    private boolean isEnabled;
    private String name;
    private String email;
    private String telephoneNumber;
    private Role role;
    private long responsibleFor;
    private long createdVehicles;
    private long updatedVehicles;
    private long createdWorks;
    private long updatedWorks;
}
