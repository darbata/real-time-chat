package io.darbata.dispatcher;

import io.darbata.dispatcher.events.incoming.UserReadChatEvent;
import io.darbata.dispatcher.events.incoming.UserSentChatEvent;
import io.darbata.dispatcher.events.incoming.UserStartedTypingEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class ChatListener {

    private final Logger log = LoggerFactory.getLogger(ChatListener.class);
    private final ChatAmqpService chatService;

    ChatListener(ChatAmqpService chatService) {
        this.chatService = chatService;
    }

    @RabbitListener(queues="chat.sent")
    public void onChatSent(UserSentChatEvent event) {
        chatService.consumeUserSentChatEvent(event);
    }

    @RabbitListener(queues="chat.read.in")
    public void onChatRead(UserReadChatEvent event) {
        chatService.consumeUserReadChatEvent(event);
    }

    @RabbitListener(queues="chat.typing.in")
    public void onUserTyping(UserStartedTypingEvent event) {
        chatService.consumeUserStartedTypingEvent(event);
    }
}