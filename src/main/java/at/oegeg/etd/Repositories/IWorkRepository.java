package at.oegeg.etd.Repositories;

import at.oegeg.etd.Entities.UserEntity;
import at.oegeg.etd.Entities.VehicleEntity;
import at.oegeg.etd.Entities.WorkEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface IWorkRepository extends JpaRepository<WorkEntity, Long>
{
    Optional<List<WorkEntity>> findWorkEntitiesByVehicle(VehicleEntity vehicle, Pageable pageable);
    Optional<List<WorkEntity>> findAllByVehicleAndIdAfterOrderByDescriptionAsc(VehicleEntity vehicle, long id, Pageable pageable);
    Optional<List<WorkEntity>> findAllByVehicleAndIdAfterOrderByDescriptionDesc(VehicleEntity vehicle, long id, Pageable pageable);
    Optional<List<WorkEntity>> findAllByVehicleAndIdAfterOrderByPriorityAsc(VehicleEntity vehicle, long id, Pageable pageable);
    Optional<List<WorkEntity>> findAllByVehicleAndIdAfterOrderByPriorityDesc(VehicleEntity vehicle, long id, Pageable pageable);
    Optional<List<WorkEntity>> findAllByVehicleAndIdAfterOrderByResponsiblePersonAsc(VehicleEntity vehicle, long id, Pageable pageable);
    Optional<List<WorkEntity>> findAllByVehicleAndIdAfterOrderByResponsiblePersonDesc(VehicleEntity vehicle, long id, Pageable pageable);
    long countByVehicle(VehicleEntity vehicle);
    Optional<WorkEntity> findByIdentifier(String identifier);
    long countWorkEntityByVehicle(VehicleEntity vehicle);

    long countAllByUpdatedBy(UserEntity user);
    long countAllByCreatedBy(UserEntity user);
    long countAllByResponsiblePerson(UserEntity user);
}
