package at.oegeg.etd.DataTransferObjects.Request;

import at.oegeg.etd.Entities.Enums.Priorities;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

//public record WorkRequest(String ResponsiblePerson, String Description, Priorities Priority)
//{
//}

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WorkRequest
{
    private String responsiblePersonEmailOrTelephoneNumber;
    private String responsiblePersonIdentifier;
    private String description;
    @Builder.Default
    private Priorities priority = Priorities.NONE;
}
