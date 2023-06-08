package at.oegeg.etd.Repositories;

import at.oegeg.etd.Entities.UserEntity;
import at.oegeg.etd.Entities.VehicleEntity;
import at.oegeg.etd.Entities.WorkEntity;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@RunWith(SpringRunner.class)
public class VehicleRepositoryTest
{
    @Autowired
    private IVehicleRepository _vehicleRepository;
    @Autowired
    private IUserEntityRepository _userRepository;
    @Autowired
    private IWorkRepository _workRepository;

    @Test
    public void VehicleRepository_FindBySearchString_Returns2FromThree()
    {
        _vehicleRepository.save(VehicleEntity.builder()
                .identifier(UUID.randomUUID().toString())
                .number("123")
                .type("ujh")
                .status("o")
                .stand("123")
                .build());
        _vehicleRepository.save(VehicleEntity.builder()
                .identifier(UUID.randomUUID().toString())
                .number("122")
                .type("ujh")
                .status("o")
                .stand("122")
                .build());
        _vehicleRepository.save(VehicleEntity.builder()
                .identifier(UUID.randomUUID().toString())
                .number("test")
                .type("test")
                .status("test")
                .stand("test")
                .build());

        List<VehicleEntity> entites = _vehicleRepository.findBySearchString("ujh");

        Assertions.assertTrue(entites.size() == 2);
    }

    @Test
    public void VehicleRepository_FindBySearchStringAndCreatedBy_Returns1FromThree()
    {
        UserEntity user1 = UserEntity.builder()
                .name("user1")
                .identifier(UUID.randomUUID().toString())
                .IsUserEnabled(true)
                .password("p")
                .email("mail")
                .build();
        _userRepository.save(user1);

        UserEntity user2 = UserEntity.builder()
                .name("user2")
                .identifier(UUID.randomUUID().toString())
                .IsUserEnabled(true)
                .password("p")
                .email("mail2")
                .build();
        _userRepository.save(user2);

        _vehicleRepository.save(VehicleEntity.builder()
                .identifier(UUID.randomUUID().toString())
                .number("123")
                .type("ujh")
                .status("o")
                .stand("123")
                .createdBy(user1)
                .build());
        _vehicleRepository.save(VehicleEntity.builder()
                .identifier(UUID.randomUUID().toString())
                .number("122")
                .type("ujh")
                .status("o")
                .stand("122")
                .createdBy(user2)
                .build());
        _vehicleRepository.save(VehicleEntity.builder()
                .identifier(UUID.randomUUID().toString())
                .number("test")
                .type("test")
                .status("test")
                .stand("test")
                .createdBy(user2)
                .build());

        List<VehicleEntity> entites = _vehicleRepository.findBySearchStringAndCreatedBy("ujh", user1);

        Assertions.assertTrue(entites.size() == 1);
    }

    @Test
    public void VehicleRepository_AddWorkToVehicle_VehicleHasWork()
    {
        WorkEntity e1 = WorkEntity.builder()
                .identifier(UUID.randomUUID().toString())
                .description("dsfhsdfah")
                .build();
        VehicleEntity v1 = VehicleEntity.builder()
                .identifier(UUID.randomUUID().toString())
                .number("test")
                .type("test")
                .status("test")
                .stand("test")
                .build();
        e1.setVehicle(v1);
        v1.setWorks(List.of(e1));
        _vehicleRepository.save(v1);

        List<VehicleEntity> entity = _vehicleRepository.findBySearchString("test");

        Assertions.assertEquals(1, entity.size());
        Assertions.assertEquals(1, entity.stream().findFirst().orElseThrow().getWorks().size());
    }

}
