package at.oegeg.etd.DataTransferObjects.Response;

import at.oegeg.etd.Entities.Enums.Priorities;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class VehicleResponse
{
    private String identifier;
    private String Number;
    private String Type;
    private String Status;
    private String Stand;
    private Priorities Priority;
    private List<WorkResponse> Works;
    private String createdBy;
    private Long workCount;
    private String updatedBy;
}
