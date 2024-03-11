package tg.gouv.anid.residentid.deduplicationservice.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tg.gouv.anid.residentid.deduplicationservice.jms.Producer;
import tg.gouv.anid.residentid.deduplicationservice.model.ResidentPending;

@RestController
@RequestMapping("activemq/deduplication/queue")
@CrossOrigin
@Slf4j
public class ProducerController {

    private Producer producer;

    @PostMapping
    public ResponseEntity<String> producer(@RequestBody ResidentPending pending) {
        log.info("Call producer endpoint");
        producer.sendMessage(pending);
        return ResponseEntity.ok("SUCCESS:"+pending.getId());
    }

    @PostMapping(value = "concurrent")
    public ResponseEntity<String> concurrentProducer(@RequestBody ResidentPending pending) {
        log.info("Call concurrent producer endpoint");
        producer.sendConcurrencyMessage(pending);
        return ResponseEntity.ok("SUCCESS:"+pending.getId());
    }

    @Autowired
    public void setProducer(Producer producer) {
        this.producer = producer;
    }
}
