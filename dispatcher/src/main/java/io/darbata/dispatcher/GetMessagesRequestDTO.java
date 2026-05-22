package io.darbata.dispatcher;

import java.util.Optional;

public class GetMessagesRequestDTO {
    Optional<String> beforeMessageId;
    int limit;
}