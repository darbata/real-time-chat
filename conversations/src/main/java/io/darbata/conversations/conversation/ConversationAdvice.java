package io.darbata.conversations.conversation;

import io.darbata.conversations.conversation.exceptions.UserNotInConversationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ConversationAdvice {

    @ExceptionHandler(UserNotInConversationException.class)
    public ResponseEntity<String> handleUserNotInConversationException(UserNotInConversationException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
    }

}