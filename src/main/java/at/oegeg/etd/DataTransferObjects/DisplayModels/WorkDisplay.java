package at.oegeg.etd.DataTransferObjects.DisplayModels;

import at.oegeg.etd.Entities.Enums.Priorities;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
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
