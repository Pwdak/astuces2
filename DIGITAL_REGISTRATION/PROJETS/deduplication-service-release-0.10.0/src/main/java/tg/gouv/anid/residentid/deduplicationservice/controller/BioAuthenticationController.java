package tg.gouv.anid.residentid.deduplicationservice.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tg.gouv.anid.residentid.deduplicationservice.dto.FingerprintWrapper;
import tg.gouv.anid.residentid.deduplicationservice.service.DeduplicationService;

@RestController
@RequestMapping("bio-auth")
@CrossOrigin
@Slf4j
public class BioAuthenticationController {

    private DeduplicationService deduplicationService;

    @PostMapping
    public ResponseEntity<String> bioAuth(@RequestBody FingerprintWrapper wrapper) {
        log.info("call bio auth api");
        return ResponseEntity.ok(deduplicationService.bioAuth(wrapper.getFingerprint()));
    }

    @Autowired
    public void setDeduplicationService(DeduplicationService deduplicationService) {
        this.deduplicationService = deduplicationService;
    }
}
