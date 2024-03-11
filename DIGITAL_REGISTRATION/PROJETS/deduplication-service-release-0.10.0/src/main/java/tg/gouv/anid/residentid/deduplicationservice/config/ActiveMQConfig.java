package tg.gouv.anid.residentid.deduplicationservice.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
import org.apache.activemq.artemis.jms.client.ActiveMQQueue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.util.ErrorHandler;

import javax.jms.JMSException;
import javax.jms.Queue;

@Configuration
@EnableJms
@Slf4j
public class ActiveMQConfig {
    @Value("${activemq.broker.url}")
    private String brokerUrl;

    @Bean
    public Queue queue() {
        return new ActiveMQQueue("residentid-deduplication-queue-prod");
    }

    @Bean
    public Queue concurrentQueue() {
        return new ActiveMQQueue("residentid-deduplication-concurrentQueue-prod");
    }

    @Bean
    public ActiveMQConnectionFactory activeMQConnectionFactory() throws JMSException {
        ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory();
        activeMQConnectionFactory.setBrokerURL(brokerUrl);
        return activeMQConnectionFactory;
    }

    @Bean
    public JmsTemplate jmsTemplate() throws JMSException {
        return new JmsTemplate(activeMQConnectionFactory());
    }

}
