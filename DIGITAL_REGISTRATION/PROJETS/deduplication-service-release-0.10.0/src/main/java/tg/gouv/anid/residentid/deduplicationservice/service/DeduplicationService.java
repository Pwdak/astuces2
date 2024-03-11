package tg.gouv.anid.residentid.deduplicationservice.service;

import com.machinezoo.sourceafis.*;
import jakarta.xml.bind.DatatypeConverter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import tg.gouv.anid.residentid.deduplicationservice.enumeration.DeduplMethod;
import tg.gouv.anid.residentid.deduplicationservice.enumeration.ResidentStatus;
import tg.gouv.anid.residentid.deduplicationservice.model.Fingerprintinfo;
import tg.gouv.anid.residentid.deduplicationservice.model.ResidentPending;
import tg.gouv.anid.residentid.deduplicationservice.repository.JdbcFingerprintInfoRepostory;
import tg.gouv.anid.residentid.deduplicationservice.repository.JdbcResidentPendingRepository;

import java.util.List;
import java.util.Objects;

@Service
@Transactional(readOnly = true)
@Slf4j
public class DeduplicationService {

    private JdbcFingerprintInfoRepostory fingerprintInfoRepostory;
    private JdbcResidentPendingRepository jdbcResidentPendingRepository;
    @Value(value = "${app.deduplication.treshold}")
    private double deduplTreshold;


    @SneakyThrows
    @Transactional
    public void deduplicate(ResidentPending residentPending, DeduplMethod method) {
        log.info("Start deduplication for {}", residentPending.toString());
        jdbcResidentPendingRepository.updateResidentPending(residentPending.getId(), ResidentStatus.IN_PROGRESS.name());
        jdbcResidentPendingRepository.updateResidentPendingStatusMessage("Traitement commencé", residentPending.getId());
        Fingerprintinfo fingerprintInfo = null;
        if (!residentPending.isHandLeftException()
                && !residentPending.isHandRightException()
                && isMatchRightAndLeftFingerprint(residentPending)) {
            residentPending.setStatus(ResidentStatus.DUPLICATION);
            residentPending.setStatusMessage("Les deux empreintes du resident semble être la même");
            jdbcResidentPendingRepository.updateResidentPendingStatusMessage(residentPending.getStatusMessage(), residentPending.getId());
        }
        if(DeduplMethod.SEQUENCE.equals(method) && !ResidentStatus.DUPLICATION.equals(residentPending.getStatus())) {
            if (StringUtils.hasText(residentPending.getResidentFingerprintLeftStr())) {
                fingerprintInfo = deduplJob(residentPending.getResidentFingerprintLeftStr());
            }

            if (Objects.isNull(fingerprintInfo) && StringUtils.hasText(residentPending.getResidentFingerprintRightStr())) {
                fingerprintInfo = deduplJob(residentPending.getResidentFingerprintRightStr());
            }
        }else if (!ResidentStatus.DUPLICATION.equals(residentPending.getStatus())){
            if (StringUtils.hasText(residentPending.getResidentFingerprintLeftStr())) {
                fingerprintInfo = concurrentDeduplJob(residentPending.getResidentFingerprintLeftStr());
            }
            if (Objects.isNull(fingerprintInfo) && StringUtils.hasText(residentPending.getResidentFingerprintRightStr())) {
                fingerprintInfo = concurrentDeduplJob(residentPending.getResidentFingerprintRightStr());
            }
        }

        if(Objects.nonNull(fingerprintInfo)) {
            residentPending.setStatus(ResidentStatus.DUPLICATION);
            jdbcResidentPendingRepository.updateResidentPendingStatusMessage("Traitement Terminé avec une duplication trouvé avec l'id du resident"+ fingerprintInfo.getResident_id(), residentPending.getId());
        }else if (!ResidentStatus.DUPLICATION.equals(residentPending.getStatus())){
            residentPending.setStatus(ResidentStatus.VALIDATE);
            jdbcResidentPendingRepository.updateResidentPendingStatusMessage("Traitement Terminé sans trouvé de doublon", residentPending.getId());
        }
        jdbcResidentPendingRepository.updateResidentPending(residentPending.getId(), residentPending.getStatus().name());
        log.info("Finish deduplication for {}", residentPending);
    }

    public String bioAuth(String fingerprint) {
        Fingerprintinfo fingerprintInfo = deduplJob(fingerprint);
        if (Objects.nonNull(fingerprintInfo)) {
            return "MATCH|"+ fingerprintInfo.getResident_id();
        }else {
            return "NOT MATCH";
        }
    }

    private boolean isMatchRightAndLeftFingerprint(ResidentPending pending) {
        FingerprintTemplate probe = getFingerprintTemplate(pending.getResidentFingerprintLeftStr());
        FingerprintTemplate candidate = getFingerprintTemplate(pending.getResidentFingerprintRightStr());
        FingerprintMatcher matcher = new FingerprintMatcher(probe);
        double score = matcher.match(candidate);
        return score > 35 ? Boolean.TRUE : Boolean.FALSE;
    }

    public Fingerprintinfo deduplJob(String fingerprint) {
        Iterable<Fingerprintinfo> candidates = fingerprintInfoRepostory.getAll();
        FingerprintTemplate probeTemplate = getFingerprintTemplate(fingerprint);
        FingerprintMatcher matcher = new FingerprintMatcher(probeTemplate);
        double high = 0;
        Fingerprintinfo match = null;
        for (Fingerprintinfo candidate : candidates) {
            double score = matcher.match(candidate.getTemplate());
            log.info("score :"+ candidate.getId()+ " : "+ score);
            if (score > high) {
                high = score;
                match = candidate;
            }
            if (score > deduplTreshold) {
                break;
            }
        }
        //parameterService.findByKey(ParameterConstant.DEDUPL_TRESHOLD)
        double threshold = deduplTreshold != 0 ? deduplTreshold : 25;
        return high >= threshold ? match : null;
    }

    public Fingerprintinfo concurrentDeduplJob(String fingerprint) {
        List<Fingerprintinfo> candidates = fingerprintInfoRepostory.getAll();
        FingerprintTemplate probeTemplate = getFingerprintTemplate(fingerprint);
        FingerprintMatcher matcher = new FingerprintMatcher(probeTemplate);
        final double[] high = {0};
        final Fingerprintinfo[] match = {null};
        double threshold = deduplTreshold != 0 ? deduplTreshold : 25;
        candidates.parallelStream()
                .takeWhile(candidate -> {
                    double score = matcher.match(candidate.getTemplate());
                    if (score >= deduplTreshold) {
                        high[0] = score;
                        match[0] = candidate;
                    }
                    return score <= deduplTreshold;})
                .forEach(candidate -> {
                    double score = matcher.match(candidate.getTemplate());
                    if (score > high[0]) {
                        high[0] = score;
                        match[0] = candidate;
                    }
                    log.info("NOT MATCH :"+ candidate.getId()+ "; score: "+ score);
                    });
        return high[0] >= threshold ? match[0] : null;
    }

    private FingerprintTemplate getFingerprintTemplate(String fingerprintBase64) {
        return new FingerprintTemplate(
                new FingerprintImage(
                        DatatypeConverter.parseBase64Binary(fingerprintBase64),
                        new FingerprintImageOptions()
                                .dpi(500)));
    }


    @Autowired
    public void setFingerprintInfoRepostory(JdbcFingerprintInfoRepostory fingerprintInfoRepostory) {
        this.fingerprintInfoRepostory = fingerprintInfoRepostory;
    }

    @Autowired
    public void setJdbcResidentPendingRepository(JdbcResidentPendingRepository jdbcResidentPendingRepository) {
        this.jdbcResidentPendingRepository = jdbcResidentPendingRepository;
    }
}
