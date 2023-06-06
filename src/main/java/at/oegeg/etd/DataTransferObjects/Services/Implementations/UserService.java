package at.oegeg.etd.DataTransferObjects.Services.Implementations;

import at.oegeg.etd.DataTransferObjects.DisplayModels.UserDisplay;
import at.oegeg.etd.Entities.Enums.Role;
import at.oegeg.etd.Entities.UserEntity;
import at.oegeg.etd.Repositories.IUserEntityRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService
{
    // == private fields ==
    private final IUserEntityRepository _userRepository;
    private final PasswordEncoder _passwordEncoder;
    private final EmailSenderService _emailSenderService;

    // == public methods ==
    public List<UserDisplay> GetAllUsers(String name)
    {
        List<UserEntity> entities = new ArrayList<>();
        if(name == null || name.equals(""))
        {
            entities = _userRepository.findAll();
        }
        else
        {
            entities = _userRepository.findAllBySearchString(name);
        }
        entities.removeIf(t -> t.getName().equals("-"));
        return UserEntitiesToDisplays(entities);
    }

    public void SaveUser(UserDisplay userDisplay)
    {
        UserEntity user;
        if(userDisplay.getIdentifier() == null || userDisplay.getIdentifier().isEmpty())
        {
            user = UserDisplayToEntity(userDisplay);
            user.setPassword(_passwordEncoder.encode(user.getIdentifier()));
            _userRepository.save(user);
            if(user.getEmail() != null && !user.getEmail().equals(""))
            {
                _emailSenderService.SendSetPasswortMail("oliver01@kabsi.at", user.getUsername(),user.getName());
            }
            return;
        }
        user = _userRepository.findByEmailOrTelephoneNumberOrNameOrIdentifier(userDisplay.getIdentifier()).orElseThrow();
        user.setName(userDisplay.getName());
        user.setEmail(userDisplay.getEmail());
        user.setTelephoneNumber(userDisplay.getTelephoneNumber());
        user.setRole(userDisplay.getRole());
        _userRepository.save(user);
    }

    public UserDisplay FindByIdentifier(String identifier)
    {
        return UserEntityToDisplay(_userRepository.findByIdentifier(identifier).orElseThrow());
    }
    public boolean ExistsByIdentifier(String identifier)
    {
        return _userRepository.existsByIdentifier(identifier);
    }

    public void SetInitialPassword(String identifier, String password)
    {
        UserEntity user = _userRepository.findByEmailOrTelephoneNumberOrNameOrIdentifier(identifier).orElseThrow();
        user.setIsUserEnabled(true);
        user.setPassword(_passwordEncoder.encode(password));
        _userRepository.save(user);
    }

    // == private methods ==
    private UserDisplay UserEntityToDisplay(UserEntity user)
    {
        return UserEntitiesToDisplays(List.of(user)).stream().findFirst().orElseThrow();
    }
    private List<UserDisplay> UserEntitiesToDisplays(List<UserEntity> entities)
    {
        return entities.stream().map(u -> UserDisplay.builder()
                .identifier(u.getIdentifier())
                .name(u.getName())
                .email(u.getEmail() != null ? u.getEmail() : "")
                .telephoneNumber(u.getTelephoneNumber() != null ? u.getTelephoneNumber() : "")
                .role(u.getRole())
                .isEnabled(u.isEnabled())
                .responsibleFor(u.getResponsibleFor() != null ? u.getResponsibleFor().size() : 0)
                .createdVehicles(u.getCreatedVehicles() != null ? u.getCreatedVehicles().size() : 0)
                .createdWorks(u.getCreatedWorks() != null ? u.getCreatedWorks().size() : 0)
                .updatedWorks(u.getUpdatedWorks() != null ? u.getUpdatedWorks().size() : 0)
                .updatedVehicles(u.getUpdatedVehicles() != null ? u.getUpdatedVehicles().size() : 0)
                .build()).collect(Collectors.toList());
    }

    private UserEntity UserDisplayToEntity(UserDisplay display)
    {
        return UserDisplaysToEntities(List.of(display)).stream().findFirst().orElseThrow();
    }
    private List<UserEntity> UserDisplaysToEntities(List<UserDisplay> displays)
    {
        return displays.stream().map(u -> UserEntity.builder()
                .identifier(UUID.randomUUID().toString())
                .name(!u.getName().equals("") ? u.getName() : null)
                .email(!u.getEmail().equals("") ? u.getEmail() : null)
                .telephoneNumber(!u.getTelephoneNumber().equals("") ? u.getTelephoneNumber() : null)
                .role(u.getRole())
                .build()).collect(Collectors.toList());
    }
    @PostConstruct
    private void Init()
    {
        if(_userRepository.findByEmailOrTelephoneNumberOrNameOrIdentifier("defaultUser").isEmpty())
        {
            _userRepository.save(UserEntity.builder()
                    .identifier("defaultUser")
                    .name("-")
                    .password(UUID.randomUUID().toString())
                    .IsUserEnabled(false)
                    .build());
        }
        if(_userRepository.findByEmailOrTelephoneNumberOrNameOrIdentifier("User").isEmpty())
        {
            _userRepository.save(UserEntity.builder()
                    .identifier(UUID.randomUUID().toString())
                    .name("User")
                    .password(_passwordEncoder.encode("Passwort"))
                    .IsUserEnabled(true)
                    .role(Role.USER)
                    .email("User")
                    .build());
        }
        if(_userRepository.findByEmailOrTelephoneNumberOrNameOrIdentifier("Leader").isEmpty())
        {
            _userRepository.save(UserEntity.builder()
                    .identifier(UUID.randomUUID().toString())
                    .name("Leader")
                    .password(_passwordEncoder.encode("Passwort"))
                    .IsUserEnabled(true)
                    .role(Role.LEADER)
                    .email("Leader")
                    .build());
        }
        if(_userRepository.findByEmailOrTelephoneNumberOrNameOrIdentifier("Admin").isEmpty())
        {
            _userRepository.save(UserEntity.builder()
                    .identifier(UUID.randomUUID().toString())
                    .name("Admin")
                    .password(_passwordEncoder.encode("Passwort"))
                    .IsUserEnabled(true)
                    .role(Role.ADMIN)
                    .email("Admin")
                    .build());
        }
    }
}
