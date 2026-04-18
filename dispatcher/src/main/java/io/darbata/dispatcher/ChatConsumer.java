package io.darbata.dispatcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class ChatConsumer {

    private Logger logger = LoggerFactory.getLogger(ChatConsumer.class);

    @RabbitListener(queues="chats")
    public void onMessage(String message) {
        logger.info("received chat message: {}", message);
    }
}