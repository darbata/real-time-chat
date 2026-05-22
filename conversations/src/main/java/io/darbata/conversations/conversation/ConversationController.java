package io.darbata.conversations.conversation;

import io.darbata.conversations.conversation.dto.ConversationDTO;
import io.darbata.conversations.conversation.dto.CreateConversationRequestDTO;
import io.darbata.conversations.conversation.dto.FetchedMessagesDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Optional;

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

    @DeleteMapping("/{conversationId}/leave")
    ResponseEntity<Void> removeUserFromConversation (
        @RequestHeader("X-User-Id") String userId,
        @PathVariable Long conversationId
    ) {
        conversationService.leaveConversation(userId, conversationId);
        return ResponseEntity.noContent().build();
    }
}