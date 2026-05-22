package io.darbata.dispatcher;

import io.darbata.dispatcher.models.Chat;

import java.util.List;
import java.util.Optional;

public interface KVStore {
    void put(String partitionKey, String sortKey, Chat value);
    List<Chat> get(String partitionKey, Optional<String> beforeSortKey, int limit);
}