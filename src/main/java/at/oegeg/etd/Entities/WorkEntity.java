package at.oegeg.etd.Entities;

import at.oegeg.etd.Entities.Enums.Priorities;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
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

}
