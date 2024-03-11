package tg.gouv.anid.residentid.deduplicationservice.repository;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import tg.gouv.anid.residentid.deduplicationservice.model.Fingerprintinfo;

import java.util.List;

@Repository
public interface JdbcFingerprintInfoRepostory extends CrudRepository<Fingerprintinfo, Long> {

    @Query("select * from fingerprintinfo")
    List<Fingerprintinfo> getAll();

}
