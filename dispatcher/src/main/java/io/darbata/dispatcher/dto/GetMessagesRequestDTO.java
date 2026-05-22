package io.darbata.dispatcher.dto;

import java.util.Optional;

public class GetMessagesRequestDTO {
    Optional<String> beforeMessageId;
    int limit;
}