package tg.gouv.anid.residentid.deduplicationservice.jms;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import tg.gouv.anid.residentid.deduplicationservice.enumeration.DeduplMethod;
import tg.gouv.anid.residentid.deduplicationservice.model.ResidentPending;
import tg.gouv.anid.residentid.deduplicationservice.service.DeduplicationService;

@Component
@Slf4j
public class Consumer {

    private DeduplicationService deduplicationService;

    @JmsListener(destination = "residentid-deduplication-queue-prod")
    public void consumeMessage(ResidentPending message) {
        log.info("Message received from activemq "+message);
        deduplicationService.deduplicate(message, DeduplMethod.SEQUENCE);
    }

    @JmsListener(destination = "residentid-deduplication-concurrentQueue-prod")
    public void consumeMessageConcurrently(ResidentPending message) {
        log.info("Message received from concurrent activemq "+message);
        deduplicationService.deduplicate(message, DeduplMethod.CONCURRENT);
    }

    @Autowired
    public void setDeduplicationService(DeduplicationService deduplicationService) {
        this.deduplicationService = deduplicationService;
    }
}
