package io.darbata.dispatcher;

import io.darbata.dispatcher.events.outgoing.DeliveredChatEvent;
import io.darbata.dispatcher.events.incoming.UserReadChatEvent;
import io.darbata.dispatcher.events.incoming.UserStartedTypingEvent;
import io.darbata.dispatcher.events.outgoing.DispatchUserReadChatEvent;
import io.darbata.dispatcher.events.outgoing.DispatchUserStartedTypingEvent;
import io.darbata.dispatcher.exceptions.ChatNotFoundException;
import io.darbata.dispatcher.models.Chat;
import io.darbata.dispatcher.events.incoming.UserSentChatEvent;
import io.darbata.dispatcher.models.ChatStatus;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
class ChatAmqpService {

    private final AmqpTemplate template;
    private final ChatRepository chatRepository;
    private final ConversationService conversationService;

    ChatAmqpService(AmqpTemplate template, ChatRepository chatRepository, ConversationService conversationService) {
        this.template = template;
        this.chatRepository = chatRepository;
        this.conversationService = conversationService;
    }

    public void consumeUserSentChatEvent(UserSentChatEvent event) {
        // fan out
        produceDeliveredChatEvent(event.conversationId(), event.senderId(), event.content());
    }

    public void consumeUserReadChatEvent(UserReadChatEvent event) {
        // update the status of the chat here that
        handleReadChat(event.conversationId(), event.messageId());
        List<String> recipients = conversationService.fetchConversationParticipants(
            event.userId(),
            event.conversationId()
        );
        var dispatch = new DispatchUserReadChatEvent(event, recipients);
        this.template.convertAndSend("chat.read.out", dispatch);
    }

    public void consumeUserStartedTypingEvent(UserStartedTypingEvent event) {
        List<String> recipients = conversationService.fetchConversationParticipants(
            event.userId(),
            event.conversationId()
        );


        var dispatch = new DispatchUserStartedTypingEvent(event, recipients);

        this.template.convertAndSend("chat.typing.out", dispatch);
    }

    private void produceDeliveredChatEvent(Long conversationId, String senderId, String content) {

        // sends to all participants (including sender)
        // allows the sender to perform the 'sent'
        List<String> recipients = conversationService.fetchConversationParticipants(
                senderId,
                conversationId
        );

        Chat chat = chatRepository.save(conversationId, senderId, content);

        DeliveredChatEvent event = new DeliveredChatEvent(chat, recipients);

        this.template.convertAndSend("chat.delivered", event);
    }

    private void handleReadChat(Long conversationId, String messageId) {
        Chat chat = chatRepository.fetchMessage(Long.toString(conversationId), messageId).orElseThrow(() -> new ChatNotFoundException("Chat not found"));
        Chat updated = new Chat(
            chat.id(),
            chat.conversationId(),
            chat.senderId(),
            chat.content(),
            chat.sentAt(),
            ChatStatus.READ
        );
        chatRepository.update(conversationId, messageId, updated);
    }

}
