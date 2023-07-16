package hse.personsearch.service.impl;

import hse.personsearch.service.RabbitQueueService;
import java.util.Map;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueInformation;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistry;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import static hse.personsearch.domain.Constants.REPORT_QUEUE_NAME;
import static hse.personsearch.domain.Constants.REQUEST_QUEUE_NAME;

@Service
public class RabbitQueueServiceImpl implements RabbitQueueService {

    private RabbitAdmin rabbitAdmin;
    private RabbitListenerEndpointRegistry rabbitListenerEndpointRegistry;

    private final Logger log = LoggerFactory.getLogger(RabbitQueueService.class);

    public RabbitQueueServiceImpl(RabbitAdmin rabbitAdmin, RabbitListenerEndpointRegistry rabbitListenerEndpointRegistry) {
        this.rabbitAdmin = rabbitAdmin;
        this.rabbitListenerEndpointRegistry = rabbitListenerEndpointRegistry;

        QueueInformation requestQueueInfo = rabbitAdmin.getQueueInfo(REQUEST_QUEUE_NAME);
        if (requestQueueInfo == null) {
            createQueue(REQUEST_QUEUE_NAME);
        }
        QueueInformation reportQueueInfo = rabbitAdmin.getQueueInfo(REPORT_QUEUE_NAME);
        if (reportQueueInfo == null) {
            createQueue(REPORT_QUEUE_NAME);
        }
    }

    /**
     * Create a queue
     * @param queueName name of the queue
     * @param durable the queue will survive a broker restart
     * @param exclusive used by only one connection and the queue will be deleted when that connection closes
     * @param autoDelete queue that has had at least one consumer is deleted when last consumer unsubscribes
     */
    @Override
    public void createQueue(String queueName, boolean durable, boolean exclusive, boolean autoDelete, Map<String, Object> arguments) {
        Queue queue = new Queue(queueName, durable, exclusive, autoDelete, arguments);
        rabbitAdmin.declareQueue(queue);
    }

    /**
     * Create a queue that is automatically deleted after a given number of milliseconds
     * @param queueName name of the queue
     * @param autoExpireAfterMillis number of milliseconds
     */
    @Override
    public void createQueue(String queueName, Long autoExpireAfterMillis) {
        Map<String, Object> arguments = Map.of("x-expires", autoExpireAfterMillis);
        createQueue(queueName, false, false, false, arguments);
    }

    /**
     * Create a durable queue
     * @param queueName name of the queue
     */
    @Override
    public void createQueue(String queueName) {
        createQueue(queueName, true, false, false, null);
    }

    @Override
    public <T> T receive(String queueName, ParameterizedTypeReference<T> type) throws InterruptedException {
        RabbitTemplate template = rabbitAdmin.getRabbitTemplate();
        T object = template.receiveAndConvert(queueName, type);
        while (object == null) {
            Thread.sleep(1000);
            object = template.receiveAndConvert(queueName, type);
        }
        Assert.notNull(object, "object from message can't be null");
        log.debug("Receive from queue " + queueName + " message with " + object);
        return object;
    }

    @Override
    public void send(String routingKey, Object object) {
        rabbitAdmin.getRabbitTemplate().convertAndSend(routingKey, object);
        log.debug("Send to routingKey " + routingKey + " message with " + object);
    }

    @Override
    public int getMessageCount() {
        int a = Integer.parseInt(Objects.requireNonNull(rabbitAdmin.getQueueProperties(REQUEST_QUEUE_NAME))
                .get(RabbitAdmin.QUEUE_MESSAGE_COUNT).toString());
        int b = Integer.parseInt(Objects.requireNonNull(rabbitAdmin.getQueueProperties(REPORT_QUEUE_NAME))
                .get(RabbitAdmin.QUEUE_MESSAGE_COUNT).toString());
        return a + b;
    }
}