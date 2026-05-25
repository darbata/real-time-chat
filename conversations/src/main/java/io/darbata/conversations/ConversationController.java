package io.darbata.conversations;

import io.darbata.conversations.dto.*;
import io.darbata.conversations.services.ConversationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/conversations")
class ConversationController {

    private final ConversationService conversationService;

    public ConversationController(ConversationService conversationService) {
        this.conversationService = conversationService;
    }

    @GetMapping()
    ResponseEntity<List<ConversationDTO>> getConversations (
            @RequestHeader("X-User-Id") String userId,
            @RequestParam("limit") int limit,
            @RequestParam("offset") int offset
    ) {
        return ResponseEntity.ok(
            conversationService.fetchConversations(userId, limit, offset)
        );
    }

    @GetMapping("/{conversationId}")
    ResponseEntity<ConversationDTO> getConversationById (
            @RequestHeader("X-User-Id") String userId,
            @PathVariable Long conversationId
    ) {
        return ResponseEntity.ok(
            conversationService.fetchConversationById(userId, conversationId)
        );
    }

    @GetMapping("/{conversationId}/participants")
    ResponseEntity<List<String>> getConversationParticipantIds (
        @RequestHeader("X-User-Id") String userId,
        @PathVariable Long conversationId
    ) {
        return ResponseEntity.ok(
            conversationService.fetchConversationParticipantIds(userId, conversationId)
        );
    }

    @GetMapping("/{conversationId}/messages")
    ResponseEntity<FetchedMessagesDTO> fetchConversationMessages (
        @RequestHeader("X-User-Id") String userId,
        @PathVariable("conversationId") long conversationId,
        @RequestParam(required = false) String before,
        @RequestParam(defaultValue = "50", required = false) int limit
    ) {
        return ResponseEntity.ok(
            conversationService.fetchConversationMessages(userId, conversationId, before, limit)
        );
    }

    @PostMapping("")
    ResponseEntity<ConversationDTO> createConversation (
        @RequestHeader("X-User-Id") String userId,
        @RequestBody CreateConversationRequestDTO request
    ) {
        ConversationDTO conversation =  conversationService.createConversation(userId, request.participantIds());

        return ResponseEntity
            .created(URI.create("api/conversations/" + conversation.id()))
            .body(conversation);
    }

    @PostMapping("/{conversationId}/messages")
    ResponseEntity<Chat> createMessage (
            @RequestHeader("X-User-Id") String userId,
            @PathVariable Long conversationId,
            @RequestBody String content
    ) {
        Chat chat = conversationService.createMessage(userId, conversationId, content);
        return ResponseEntity.ok(chat);
    }

    @DeleteMapping("/{conversationId}/leave")
    ResponseEntity<Void> removeUserFromConversation (
        @RequestHeader("X-User-Id") String userId,
        @PathVariable Long conversationId
    ) {
        conversationService.leaveConversation(userId, conversationId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{conversationId}/messages/{messageId}")
    public ResponseEntity<?> updateMessage (
        @RequestHeader("X-User-Id") String userId,
        @PathVariable("conversationId") long conversationId,
        @PathVariable("messageId") String messageId,
        @RequestBody String updatedMessageContent
    ) {
        conversationService.updateMessage(userId, conversationId, messageId, updatedMessageContent);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{conversationId}/messages/{messageId}")
    public ResponseEntity<?> deleteMessage (
        @RequestHeader("X-User-Id") String userId,
        @PathVariable("conversationId") long conversationId,
        @PathVariable("messageId") String messageId
    ) {
        conversationService.deleteMessage(userId, conversationId, messageId);
        return ResponseEntity.noContent().build();
    }
}