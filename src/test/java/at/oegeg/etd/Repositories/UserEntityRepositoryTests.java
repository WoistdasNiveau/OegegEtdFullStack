package at.oegeg.etd.Repositories;

import at.oegeg.etd.Entities.UserEntity;
import jakarta.annotation.Resource;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.UUID;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@RunWith(SpringRunner.class)
public class UserEntityRepositoryTests
{
    @Resource
    private IUserEntityRepository _userEntityRepository;

    @Test
    public void UserEntityRepository_ExistsByIdentifier_returnTrue()
    {
        //Arrange
        String identifier = UUID.randomUUID().toString();
        _userEntityRepository.save(UserEntity.builder()
                .identifier(identifier)
                .name("test")
                .isUserEnabled(true)
                .email("mail")
                .password("password")
                .build());

        //Act
        boolean exists = _userEntityRepository.existsByIdentifier(identifier);

        //Assert
        Assertions.assertTrue(exists );
    }

    @Test
    public void UserEntityRepository_FindTwoFromThreeBySearchString()
    {
        _userEntityRepository.save(UserEntity.builder()
                .identifier(UUID.randomUUID().toString())
                .name("test1")
                .isUserEnabled(true)
                .email("mail1")
                .password("password")
                .build());
        _userEntityRepository.save(UserEntity.builder()
                .identifier(UUID.randomUUID().toString())
                .name("test2")
                .isUserEnabled(true)
                .email("mail2")
                .password("password")
                .build());
        _userEntityRepository.save(UserEntity.builder()
                .identifier(UUID.randomUUID().toString())
                .name("hi")
                .isUserEnabled(true)
                .email("mail3")
                .password("password")
                .build());

        List<UserEntity> users = _userEntityRepository.findAllBySearchString("test");

        Assertions.assertTrue(users.size() == 2);
    }
}
