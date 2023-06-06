package at.oegeg.etd.Repositories;

import at.oegeg.etd.Entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

public interface IUserEntityRepository extends JpaRepository<UserEntity, Long>
{
    boolean existsByIdentifier(String identifier);

    @Query("SELECT u FROM UserEntity u WHERE u.email = :value OR u.telephoneNumber =:value OR u.name = :value  OR u.identifier = :value")
    Optional<UserEntity> findByEmailOrTelephoneNumberOrNameOrIdentifier(@Param("value") String value);
    Optional<UserEntity> findByIdentifier(String identifier);
    @Query("SELECT e FROM UserEntity e WHERE " +
            "LOWER(e.identifier) LIKE LOWER(concat('%', :searchString, '%')) OR " +
            "LOWER(e.name) LIKE LOWER(concat('%', :searchString, '%')) OR " +
            "LOWER(e.email) LIKE LOWER(concat('%', :searchString, '%')) OR " +
            "LOWER(e.telephoneNumber) LIKE LOWER(concat('%', :searchString, '%')) OR " +
            "LOWER(e.password) LIKE LOWER(concat('%', :searchString, '%'))")
    List<UserEntity> findAllBySearchString(String searchString);


}
