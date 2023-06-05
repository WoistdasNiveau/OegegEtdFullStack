package at.oegeg.etd.Entities;

import at.oegeg.etd.Entities.Enums.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

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

    @OneToMany(mappedBy = "createdBy")
    @Cascade(CascadeType.ALL)
    private List<VehicleEntity> createdVehicles;

    @OneToMany(mappedBy = "createdBy")
    @Cascade(CascadeType.SAVE_UPDATE)
    private List<WorkEntity> createdWorks;

    @OneToMany(mappedBy = "responsiblePerson")
    @Cascade(CascadeType.SAVE_UPDATE)
    private List<WorkEntity> responsibleFor;

    @OneToMany(mappedBy = "updatedBy")
    @Cascade(CascadeType.SAVE_UPDATE)
    private List<VehicleEntity> updatedVehicles;

    @OneToMany(mappedBy = "updatedBy")
    @Cascade(CascadeType.SAVE_UPDATE)
    private List<WorkEntity> updatedWorks;

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
}
