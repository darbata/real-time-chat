package io.darbata.conversations.exceptions;

public class UserNotInConversationException extends RuntimeException {
    public UserNotInConversationException(String message) {
        super(message);
    }
}
