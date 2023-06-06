package at.oegeg.etd.DataTransferObjects.Services.Implementations;

import at.oegeg.etd.Entities.UserEntity;
import at.oegeg.etd.Repositories.IUserEntityRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService
{
    private final IUserEntityRepository _userRepository;

    // == private methods ==
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
    }
}
