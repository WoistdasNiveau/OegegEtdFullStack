package at.oegeg.etd.Repositories;

import at.oegeg.etd.Entities.UserEntity;
import at.oegeg.etd.Entities.VehicleEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.security.core.userdetails.User;

import java.util.List;
import java.util.Optional;

public interface IVehicleRepository extends JpaRepository<VehicleEntity, Long>
{
    @Query("SELECT e FROM VehicleEntity e WHERE " +
            "LOWER(e.number) LIKE LOWER(concat('%', :searchString, '%')) OR " +
            "LOWER(e.type) LIKE LOWER(concat('%', :searchString, '%')) OR " +
            "LOWER(e.status) LIKE LOWER(concat('%', :searchString, '%')) OR " +
            "LOWER(e.stand) LIKE LOWER(concat('%', :searchString, '%'))")
    List<VehicleEntity> findBySearchString(String searchString);

    Optional<VehicleEntity> findByIdentifier(String identifier);

    Optional<List<VehicleEntity>> findAllByCreatedBy(UserEntity user);
    Optional<List<VehicleEntity>> findAllByUpdatedBy(UserEntity user);

    @Query("SELECT e FROM VehicleEntity e WHERE " +
            "(LOWER(e.number) LIKE LOWER(concat('%', :searchString, '%')) OR " +
            "LOWER(e.type) LIKE LOWER(concat('%', :searchString, '%')) OR " +
            "LOWER(e.status) LIKE LOWER(concat('%', :searchString, '%')) OR " +
            "LOWER(e.stand) LIKE LOWER(concat('%', :searchString, '%'))) AND " +
            "e.createdBy = :createdBy")
    List<VehicleEntity> findBySearchStringAndCreatedBy(String searchString, UserEntity createdBy);

    @Query("SELECT e FROM VehicleEntity e WHERE " +
            "(LOWER(e.number) LIKE LOWER(concat('%', :searchString, '%')) OR " +
            "LOWER(e.type) LIKE LOWER(concat('%', :searchString, '%')) OR " +
            "LOWER(e.status) LIKE LOWER(concat('%', :searchString, '%')) OR " +
            "LOWER(e.stand) LIKE LOWER(concat('%', :searchString, '%'))) AND " +
            "e.updatedBy = :createdBy")
    List<VehicleEntity> findBySearchStringAndUpdatedBy(String searchString, UserEntity createdBy);

}
