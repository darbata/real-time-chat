package io.darbata.conversations;

import io.darbata.conversations.dto.FetchedMessagesDTO;
import io.darbata.conversations.exceptions.ChatNotFoundException;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

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

    public void updateMessage(long conversationId, String messageId, String updatedMessageContent) {
        client
            .put()
            .uri(uriBuilder -> uriBuilder
                    .path("/conversations/{conversationId}/messages/{messageId}")
                    .build(conversationId, messageId))
                .body(updatedMessageContent)
            .retrieve()
            .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                throw new ChatNotFoundException("Chat not found");
            })
            .toBodilessEntity();
    }

    public void deleteMessage(long conversationId, String messageId) {
        client
            .delete()
            .uri(uriBuilder -> uriBuilder
                .path("/conversations/{conversationId}/messages/{messageId}")
                    .build(conversationId, messageId))
            .retrieve()
            .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                throw new ChatNotFoundException("Chat not found");
            })
            .toBodilessEntity();
    }
}