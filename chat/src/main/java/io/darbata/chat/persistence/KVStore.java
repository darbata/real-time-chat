package io.darbata.chat.persistence;

import io.darbata.chat.message.Message;

public interface KVStore {
    void put(String partitionKey, String sortKey, Message value);
}