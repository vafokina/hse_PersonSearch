package hse.personsearch.service;

import java.util.Map;
import org.springframework.core.ParameterizedTypeReference;

public interface RabbitQueueService {

    void createQueue(String queueName, boolean durable, boolean exclusive, boolean autoDelete, Map<String, Object> arguments);

    void createQueue(String queueName, Long autoExpireAfterMillis);

    void createQueue(String queueName);

    <T> T receive(String queueName, ParameterizedTypeReference<T> type) throws InterruptedException;

    void send(String routingKey, Object object);

    public int getMessageCount();

}
