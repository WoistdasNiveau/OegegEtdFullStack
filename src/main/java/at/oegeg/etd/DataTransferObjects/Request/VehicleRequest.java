package at.oegeg.etd.DataTransferObjects.Request;

import at.oegeg.etd.Entities.Enums.Priorities;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


//public record VehicleRequest(String Number, String Type, String Status, String Stand, Priorities Priority, List<WorkRequest> Works)
//{
//}

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleRequest
{
    private String identifier;
    private String number;
    private String type;
    private String status;
    private String stand;
    private Priorities priority;
    private List<WorkRequest> works;
}
