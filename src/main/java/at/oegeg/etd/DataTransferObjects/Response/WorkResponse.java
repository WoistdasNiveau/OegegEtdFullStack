package at.oegeg.etd.DataTransferObjects.Response;

import at.oegeg.etd.Entities.Enums.Priorities;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class WorkResponse
{
    private String identifier;
    private String ResponsiblePerson;
    private String Description;
    private Priorities Priority;
    private String createdBy;
    private String updatedBy;
}