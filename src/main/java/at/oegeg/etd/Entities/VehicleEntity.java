package at.oegeg.etd.Entities;


import at.oegeg.etd.Entities.Enums.Priorities;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class VehicleEntity
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String identifier;
    @Column(unique = true)
    @NotBlank
    private String number;
    @NotBlank
    private String type;
    @NotBlank
    private String status;
    @Column(unique = true)
    @NotBlank
    private String stand;
    private Priorities priority;

    @OneToMany(mappedBy="vehicle", fetch = FetchType.EAGER)
    @Cascade(CascadeType.ALL)
    private List<WorkEntity> works;

    @ManyToOne
    private UserEntity createdBy;

    @ManyToOne
    private UserEntity updatedBy;

    @Override
    public String toString()
    {
        return identifier + "; " + number;
    }
}
