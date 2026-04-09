package io.darbata.conversations.conversation;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConversationService {

    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;

    public ConversationService(ConversationRepository conversationRepository, MessageRepository messageRepository) {
        this.conversationRepository = conversationRepository;
        this.messageRepository = messageRepository;
    }

    /*
        conversations = [  <-- fetch recent 20
            {
                conversationId: long
                messages: Message[]
            },
            {
                conversationId: long
                messages: Message[]
            }
        ]
    */
    // TODO: implement pagination
    public List<Long> fetchConversations(long userId) {
        // fetch recent conversations from relational database
        // for each conversation fetch messages from key value store
        // create single object
        return null;
    }

}