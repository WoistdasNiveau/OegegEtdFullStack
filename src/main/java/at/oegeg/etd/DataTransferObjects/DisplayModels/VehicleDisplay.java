package at.oegeg.etd.DataTransferObjects.DisplayModels;

import at.oegeg.etd.Entities.Enums.Priorities;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class VehicleDisplay
{
    private String identifier;
    private String number;
    private String type;
    private String status;
    private String stand;
    private Priorities priority;
    private List<WorkDisplay> works;
    private String createdBy;
    private Long workCount;
    private String updatedBy;
}
