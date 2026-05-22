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
    private final ConversationRepository conversationRepository;
    private final ChatRepository chatRepository;

    ChatAmqpService(AmqpTemplate template, ConversationRepository conversationRepository, ChatRepository chatRepository) {
        this.template = template;
        this.conversationRepository = conversationRepository;
        this.chatRepository = chatRepository;
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

        List<String> recipients = conversationRepository.fetchRecipients(
                incomingChat.conversationId(),
                senderId
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
