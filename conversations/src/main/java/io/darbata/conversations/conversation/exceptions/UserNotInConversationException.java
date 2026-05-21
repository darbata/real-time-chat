package io.darbata.conversations.conversation.exceptions;

public class UserNotInConversationException extends RuntimeException {
    public UserNotInConversationException(String message) {
        super(message);
    }
}
