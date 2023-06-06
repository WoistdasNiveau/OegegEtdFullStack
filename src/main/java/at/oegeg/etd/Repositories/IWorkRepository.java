package at.oegeg.etd.Repositories;

import at.oegeg.etd.Entities.UserEntity;
import at.oegeg.etd.Entities.VehicleEntity;
import at.oegeg.etd.Entities.WorkEntity;
import org.hibernate.jdbc.Work;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface IWorkRepository extends JpaRepository<WorkEntity, Long>
{
    Optional<WorkEntity> findByIdentifier(String identifier);
    Optional<List<WorkEntity>> findByResponsiblePerson(UserEntity user);
    Optional<List<WorkEntity>> findByCreatedBy(UserEntity user);
    Optional<List<WorkEntity>> findByUpdatedBy(UserEntity user);

    @Query("SELECT e FROM WorkEntity e " +
            "WHERE e.createdBy = :createdBy " +
            "AND (LOWER(e.identifier) LIKE LOWER(concat('%', :searchString, '%')) OR " +
            "LOWER(e.description) LIKE LOWER(concat('%', :searchString, '%')))")
    List<WorkEntity> findByCreatedByAndSearchString(UserEntity createdBy, String searchString);

    @Query("SELECT e FROM WorkEntity e " +
            "WHERE e.updatedBy = :createdBy " +
            "AND (LOWER(e.identifier) LIKE LOWER(concat('%', :searchString, '%')) OR " +
            "LOWER(e.description) LIKE LOWER(concat('%', :searchString, '%')))")
    List<WorkEntity> findByUpdatedByAndSearchString(UserEntity createdBy, String searchString);

    @Query("SELECT e FROM WorkEntity e " +
            "WHERE e.responsiblePerson = :createdBy " +
            "AND (LOWER(e.identifier) LIKE LOWER(concat('%', :searchString, '%')) OR " +
            "LOWER(e.description) LIKE LOWER(concat('%', :searchString, '%')))")
    List<WorkEntity> findByResponsiblePersonAndSearchString(UserEntity createdBy, String searchString);



}
