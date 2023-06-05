package at.oegeg.etd.Repositories;

import at.oegeg.etd.Entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface IUserEntityRepository extends JpaRepository<UserEntity, Long>
{
    @Query("SELECT u FROM UserEntity u WHERE u.email = :value OR u.telephoneNumber =:value OR u.name = :value  OR u.identifier = :value")
    Optional<UserEntity> findByEmailOrTelephoneNumberOrNameOrIdentifier(@Param("value") String value);
    Optional<UserEntity> findByIdentifier(String identifier);
}
