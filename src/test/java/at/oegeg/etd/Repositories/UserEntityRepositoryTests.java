package at.oegeg.etd.Repositories;

import at.oegeg.etd.Application;
import at.oegeg.etd.Entities.UserEntity;
import jakarta.annotation.Resource;
import jakarta.transaction.Transactional;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.BootstrapWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

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
                .IsUserEnabled(true)
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
                .IsUserEnabled(true)
                .email("mail1")
                .password("password")
                .build());
        _userEntityRepository.save(UserEntity.builder()
                .identifier(UUID.randomUUID().toString())
                .name("test2")
                .IsUserEnabled(true)
                .email("mail2")
                .password("password")
                .build());
        _userEntityRepository.save(UserEntity.builder()
                .identifier(UUID.randomUUID().toString())
                .name("hi")
                .IsUserEnabled(true)
                .email("mail3")
                .password("password")
                .build());

        List<UserEntity> users = _userEntityRepository.findAllBySearchString("test");

        Assertions.assertTrue(users.size() == 2);
    }
}
