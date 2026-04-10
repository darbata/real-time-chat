package io.darbata.conversations.conversation;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/conversations")
class ConversationController {

    private final ConversationService conversationService;

    public ConversationController(ConversationService conversationService) {
        this.conversationService = conversationService;
    }

    @GetMapping("")
    ResponseEntity<?> getConversations(
            @RequestParam long userId
    ) {
        return ResponseEntity.ok(
                conversationService.fetchUserInbox(userId, 10, 0, 50)
        );
    }

}