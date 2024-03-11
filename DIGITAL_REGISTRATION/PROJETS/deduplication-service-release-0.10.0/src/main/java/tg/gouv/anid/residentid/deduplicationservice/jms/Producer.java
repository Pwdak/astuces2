package tg.gouv.anid.residentid.deduplicationservice.jms;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;
import tg.gouv.anid.residentid.deduplicationservice.model.ResidentPending;
import tg.gouv.anid.residentid.deduplicationservice.repository.JdbcResidentPendingRepository;

import javax.jms.Queue;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

@Component
@Slf4j
public class Producer {
    private JmsTemplate jmsTemplate;

    private Queue queue;

    private JdbcResidentPendingRepository jdbcResidentPendingRepository;

    private Queue concurrentQueue;

    public void sendMessage(ResidentPending pending) {
        try{
            jmsTemplate.convertAndSend(queue, pending);
            pending.setStatusMessage("Ajouté au queue le "+ LocalDateTime.now().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL)));
            jdbcResidentPendingRepository.updateResidentPendingStatusMessage(pending.getStatusMessage(), pending.getId());
        }catch (Exception e) {
            log.error("An error occur in producer sendMessage  ==> {}", e.getMessage());
        }
    }

    public void sendConcurrencyMessage(ResidentPending pending) {
        try{
            jmsTemplate.convertAndSend(concurrentQueue, pending);
        }catch (Exception e) {
            log.error("An error occur in producer sendMessage  ==> {}", e.getMessage());
        }
    }

    @Autowired
    public void setJmsTemplate(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    @Autowired
    public void setQueue(Queue queue) {
        this.queue = queue;
    }

    @Autowired
    public void setJdbcResidentPendingRepository(JdbcResidentPendingRepository jdbcResidentPendingRepository) {
        this.jdbcResidentPendingRepository = jdbcResidentPendingRepository;
    }

    @Autowired
    public void setConcurrentQueue(@Qualifier(value = "concurrentQueue") Queue concurrentQueue) {
        this.concurrentQueue = concurrentQueue;
    }
}
