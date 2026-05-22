package io.darbata.conversations;

import io.darbata.conversations.conversation.dto.FetchedMessagesDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Optional;

@Service
public class MessageClient {

    private final RestClient client;

    public MessageClient(RestClient client) {
        this.client = client;
    }

    public FetchedMessagesDTO fetchConversationMessages (Long conversationId, int limit) {
        return client
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/conversations/{conversationId}/messages")
                        .queryParam("limit", limit)
                        .build(conversationId))
                .retrieve()
                .body(FetchedMessagesDTO.class);
    }

    public FetchedMessagesDTO fetchConversationMessages (Long conversationId, String before, int limit) {
        return client
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/conversations/{conversationId}/messages")
                        .queryParam("before", before)
                        .queryParam("limit", limit)
                        .build(conversationId))
                .retrieve()
                .body(FetchedMessagesDTO.class);
    }
}