package io.darbata.dispatcher;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

@Service
public class ConversationService {

    private final RestClient client;

    public ConversationService(RestClient client) {
        this.client = client;
    }

    public List<String> fetchConversationParticipants(String userId, Long conversationId) {
        return client.get()
            .uri(uriBuilder -> uriBuilder
                .path("/conversations/{conversationId}/participants")
                .build(conversationId))
            .header("X-User-Id", userId)
            .retrieve()
            .body(new ParameterizedTypeReference<>() {});
    }

}