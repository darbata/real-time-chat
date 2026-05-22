package io.darbata.dispatcher;

import io.darbata.dispatcher.events.DispatchChatEvent;
import io.darbata.dispatcher.events.RecentMessageEvent;
import io.darbata.dispatcher.models.Chat;
import io.darbata.dispatcher.models.IncomingChat;
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

    public void consumeIncomingMessage(IncomingChat incomingChat) {
        produceDispatchChatEvent(incomingChat);
        produceRecentMessageEvent(incomingChat.conversationId(),  incomingChat.content());
    }

    private void produceRecentMessageEvent(Long conversationId, String content) {
        RecentMessageEvent recentMessageEvent = new RecentMessageEvent(conversationId, content);
        this.template.convertAndSend("recent-messages", recentMessageEvent);
    }

    private void produceDispatchChatEvent(IncomingChat incomingChat) {

        String senderId = incomingChat.senderId();

        // sends to all participants (including sender)
        // allows the sender to perform the 'sent'
        List<String> recipients = conversationService.fetchConversationParticipants(
                senderId,
                incomingChat.conversationId()
        );

        Chat chat = chatRepository.save(incomingChat);

        DispatchChatEvent dispatchChatEvent = new DispatchChatEvent(
                chat.conversationId(),
                chat.id(),
                senderId,
                chat.content(),
                recipients,
                chat.sentAt()
        );

        this.template.convertAndSend("dispatch", dispatchChatEvent);
    }
}
