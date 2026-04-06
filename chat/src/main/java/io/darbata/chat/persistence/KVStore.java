package io.darbata.chat.persistence;

import io.darbata.chat.message.Message;

import java.util.List;

public interface KVStore {
    public void put(long key, Message value);
    public List<Message> get(long key);
}