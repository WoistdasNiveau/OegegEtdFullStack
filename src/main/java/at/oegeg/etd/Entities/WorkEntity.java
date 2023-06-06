package at.oegeg.etd.Entities;

import at.oegeg.etd.Entities.Enums.Priorities;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Objects;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class WorkEntity
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String identifier;
    @ManyToOne
    private UserEntity responsiblePerson;
    @NotBlank
    private String description;
    private Priorities priority;
    @ManyToOne
    @NotNull
    private VehicleEntity vehicle;

    @ManyToOne
    private  UserEntity createdBy;
    @ManyToOne
    UserEntity updatedBy;

    @Override
    public int hashCode()
    {
        return Objects.hash(identifier);
    }

    @Override
    public boolean equals(Object obj)
    {
        if(this == obj)
            return true;
        if(obj == null || getClass() != obj.getClass())
            return false;
        WorkEntity entity = (WorkEntity) obj;
        return Objects.equals(identifier,entity.getIdentifier());
    }
}
