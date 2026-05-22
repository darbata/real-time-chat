package io.darbata.dispatcher;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/conversations")
public class ChatController {

    private final ChatService chatService;

    ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @GetMapping("/{conversationId}/messages")
    public ResponseEntity<MessagesDTO> getMessages (
        @PathVariable("conversationId") long conversationId,
        @RequestParam Optional<String> beforeMessageId,
        @RequestParam int limit
    ) {
        return ResponseEntity.ok(chatService.fetchConversationMessages(conversationId, beforeMessageId, limit));
    }

}