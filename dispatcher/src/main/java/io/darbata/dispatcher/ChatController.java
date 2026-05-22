package io.darbata.dispatcher;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/conversations")
public class ChatController {

    private final ChatService chatService;
    private final ConversationService conversationService;

    ChatController(ChatService chatService, ConversationService conversationService) {
        this.chatService = chatService;
        this.conversationService = conversationService;
    }

    @GetMapping("/{conversationId}/messages")
    public ResponseEntity<MessagesDTO> getMessages (
        @PathVariable("conversationId") long conversationId,
        @RequestParam(required = false) String before,
        @RequestParam(defaultValue = "50") int limit
    ) {
        return ResponseEntity.ok(chatService.fetchConversationMessages(conversationId, Optional.ofNullable(before), limit));
    }

    @PutMapping("/{conversationId}/messages/{messageId}")
    public ResponseEntity<?> updateMessage (
        @PathVariable("conversationId") long conversationId,
        @PathVariable("messageId") String messageId,
        @RequestBody String updatedMessageContent
    ) {
        chatService.updateMessage(conversationId, messageId, updatedMessageContent);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{conversationId}/messages/{messageId}")
    public ResponseEntity<?> deleteMessage (
        @PathVariable("conversationId") long conversationId,
        @PathVariable("messageId") String messageId
    ) {
        chatService.deleteMessage(conversationId, messageId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{conversationId}/participants")
    public ResponseEntity<List<String>> getParticipants (
        @RequestHeader("X-User-Id") String userId,
        @PathVariable("conversationId") long conversationId
    ) {
        List<String> participants = conversationService.fetchConversationParticipants(userId, conversationId);
        return ResponseEntity.ok(participants);
    }

}