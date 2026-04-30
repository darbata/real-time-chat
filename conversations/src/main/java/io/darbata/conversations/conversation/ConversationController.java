package io.darbata.conversations.conversation;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/conversations")
class ConversationController {

    private final ConversationService conversationService;

    public ConversationController(ConversationService conversationService) {
        this.conversationService = conversationService;
    }

    @GetMapping()
    ResponseEntity<List<ConversationDTO>> getConversations(
            @RequestHeader("X-User-Id") String userId, // custom header
            @RequestParam("limit") int limit,
            @RequestParam("offset") int offset
    ) {
        return ResponseEntity.ok(
            conversationService.fetchConversations(userId, limit, offset)
        );
    }

}