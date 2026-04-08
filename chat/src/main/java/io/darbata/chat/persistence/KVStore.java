package io.darbata.chat.persistence;

import io.darbata.chat.message.Message;

import java.util.List;

public interface KVStore {
    void put(String partitionKey, String sortKey, Message value);
    List<Message> get(String partitionKey);
}