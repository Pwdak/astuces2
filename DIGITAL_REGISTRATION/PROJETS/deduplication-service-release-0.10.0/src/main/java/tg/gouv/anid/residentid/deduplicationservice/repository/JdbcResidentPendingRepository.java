package tg.gouv.anid.residentid.deduplicationservice.repository;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tg.gouv.anid.residentid.deduplicationservice.model.ResidentPending;

@Repository
public interface JdbcResidentPendingRepository extends CrudRepository<ResidentPending, Long> {

    @Modifying
    @Query("UPDATE residentpending SET status = :status WHERE id = :id")
    void updateResidentPending(@Param(value = "id") Long id, @Param(value = "status") String status);

    @Modifying
    @Query("UPDATE residentpending SET statusMessage = :statusMessage WHERE id = :id")
    void updateResidentPendingStatusMessage(@Param(value = "statusMessage") String statusMessage, @Param(value = "id") Long id);

}
