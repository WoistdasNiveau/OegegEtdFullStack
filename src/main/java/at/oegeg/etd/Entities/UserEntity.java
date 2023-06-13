package at.oegeg.etd.Entities;

import at.oegeg.etd.Entities.Enums.Role;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class UserEntity implements UserDetails
{
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(unique = true)
    private String identifier;
    @Column(unique = true)
    @NotBlank
    private String name;
    @Column(unique = true)
    private String email;
    @Column(unique = true)
    private String telephoneNumber;
    @NotBlank
    private String password;
    private boolean IsUserEnabled = false;
    private Role role;

    @OneToMany(mappedBy = "createdBy", fetch = FetchType.EAGER)
    @Cascade(CascadeType.ALL)
    private List<VehicleEntity> createdVehicles = new ArrayList<>();

    @OneToMany(mappedBy = "createdBy", fetch = FetchType.EAGER)
    @Cascade(CascadeType.SAVE_UPDATE)
    private List<WorkEntity> createdWorks = new ArrayList<>();

    @OneToMany(mappedBy = "responsiblePerson", fetch = FetchType.EAGER)
    @Cascade(CascadeType.SAVE_UPDATE)
    private List<WorkEntity> responsibleFor = new ArrayList<>();

    @OneToMany(mappedBy = "updatedBy", fetch = FetchType.EAGER)
    @Cascade(CascadeType.SAVE_UPDATE)
    private List<VehicleEntity> updatedVehicles = new ArrayList<>();

    @OneToMany(mappedBy = "updatedBy", fetch = FetchType.EAGER)
    @Cascade(CascadeType.SAVE_UPDATE)
    private List<WorkEntity> updatedWorks = new ArrayList<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities()
    {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public String getUsername()
    {
        return getIdentifier().toString();
    }

    @Override
    public boolean isAccountNonExpired()
    {
        return true;
    }

    @Override
    public boolean isAccountNonLocked()
    {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired()
    {
        return true;
    }

    @Override
    public boolean isEnabled()
    {
        return IsUserEnabled;
    }

    @Override
    public String toString()
    {
        return getName() + getEmail() + getTelephoneNumber(); //+ getAuthorities();
    }

    @PreRemove
    private void PreRemove()
    {
        if(createdWorks != null)
        {
            for(WorkEntity entity : createdWorks)
                entity.setCreatedBy(null);
        }
        if(updatedWorks != null)
        {
            for(WorkEntity entity : updatedWorks)
                entity.setCreatedBy(null);
        }
        if(createdVehicles != null)
        {
            for(VehicleEntity entity : createdVehicles)
                entity.setCreatedBy(null);
        }
        if(updatedVehicles != null)
        {
            for(VehicleEntity entity : updatedVehicles)
                entity.setCreatedBy(null);
        }
        if(responsibleFor != null)
        {
            for(WorkEntity entity : responsibleFor)
                entity.setCreatedBy(null);
        }
    }
}
