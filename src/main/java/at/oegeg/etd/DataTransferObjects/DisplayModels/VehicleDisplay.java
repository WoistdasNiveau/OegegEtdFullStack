package at.oegeg.etd.DataTransferObjects.DisplayModels;

import at.oegeg.etd.Entities.Enums.Priorities;
import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Getter
@Setter
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
