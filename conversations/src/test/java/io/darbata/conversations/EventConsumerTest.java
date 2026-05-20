package io.darbata.conversations;

import io.darbata.conversations.conversation.ConversationService;
import io.darbata.conversations.conversation.EventConsumer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class EventConsumerTest {

    @Mock
    private ConversationService conversationService;

    private EventConsumer eventConsumer;

    @BeforeEach
    void setUp() {
        eventConsumer = new EventConsumer(conversationService);
    }

    @Test
    void onRce
}