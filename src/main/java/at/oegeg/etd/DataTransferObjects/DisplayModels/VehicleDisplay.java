package at.oegeg.etd.DataTransferObjects.DisplayModels;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.context.annotation.Bean;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class VehicleDisplay
{
    private String Number;
    private String Type;
    private String Status;
    private String Stand;
    private String WorkCount;
}
