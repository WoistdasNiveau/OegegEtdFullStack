package at.oegeg.etd.Repositories;

import at.oegeg.etd.Entities.UserEntity;
import at.oegeg.etd.Entities.VehicleEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

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
    //Optional<List<VehicleEntity>> findAllOrderByType(Pageable page);
    Optional<List<VehicleEntity>> findAllByTypeAfterOrderByTypeAsc(String type, Pageable page);
    Optional<List<VehicleEntity>> findAllByTypeAfterOrderByTypeDesc(String type, Pageable page);
    Optional<List<VehicleEntity>> findAllByNumberAfterOrderByNumberAsc(String number, Pageable page);
    Optional<List<VehicleEntity>> findAllByNumberAfterOrderByNumberDesc(String number, Pageable page);
    Optional<List<VehicleEntity>> findAllByStatusAfterOrderByStatusAsc(String status, Pageable page);
    Optional<List<VehicleEntity>> findAllByStatusAfterOrderByStatusDesc(String status, Pageable page);
    Optional<List<VehicleEntity>> findAllByStandAfterOrderByStandAsc(String stand, Pageable page);
    Optional<List<VehicleEntity>> findAllByStandAfterOrderByStandDesc(String stand, Pageable page);

    long countAllByCreatedBy(UserEntity user);
    long countAllByUpdatedBy(UserEntity user);

}
