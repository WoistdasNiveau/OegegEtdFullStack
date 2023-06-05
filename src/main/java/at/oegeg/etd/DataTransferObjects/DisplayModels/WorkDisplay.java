package at.oegeg.etd.DataTransferObjects.DisplayModels;

import at.oegeg.etd.Entities.Enums.Priorities;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WorkDisplay
{
    private String identifier;
    private String responsiblePerson;
    private String description;
    private Priorities priority;
    private String createdBy;
    private String updatedBy;
}
