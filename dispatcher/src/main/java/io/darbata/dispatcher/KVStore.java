package io.darbata.dispatcher;

import io.darbata.dispatcher.models.Chat;

import java.util.List;
import java.util.Optional;

public interface KVStore {
    void put(String partitionKey, String sortKey, Chat value);
    void delete(String partitionKey, String sortKey);
    List<Chat> getMessages(String partitionKey, Optional<String> beforeSortKey, int limit);
    Optional<Chat> getMessage(String partitionKey, String sortKey);
    boolean exists(String partitionKey, String sortKey);
}