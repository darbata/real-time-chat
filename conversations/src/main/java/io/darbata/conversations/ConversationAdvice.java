package io.darbata.conversations;

import io.darbata.conversations.exceptions.ChatNotFoundException;
import io.darbata.conversations.exceptions.NoConversationException;
import io.darbata.conversations.exceptions.UserNotFoundException;
import io.darbata.conversations.exceptions.UserNotInConversationException;
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

    @ExceptionHandler(NoConversationException.class)
    public ResponseEntity<String> handleNoConversationException(NoConversationException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<String> handleUserNotFoundException(UserNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    @ExceptionHandler(ChatNotFoundException.class)
    public ResponseEntity<String> handleChatNotFoundException(ChatNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

}