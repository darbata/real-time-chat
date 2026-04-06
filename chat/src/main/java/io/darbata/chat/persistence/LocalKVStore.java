package io.darbata.chat.persistence;

import io.darbata.chat.message.Message;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class LocalKVStore implements KVStore {

    private final Map<Long, List<Message>> map;

    public LocalKVStore() {
        this.map = new HashMap<>();
    }

    @Override
    public void put(long key, Message message) {
        List<Message> messages = map.getOrDefault(key, new ArrayList<>());
        messages.add(message);
        map.put(key, messages);
    }

    @Override
    public List<Message> get(long key) {
        Optional<List<Message>> messages = Optional.ofNullable(map.get(key));
        return messages.orElse(Collections.emptyList());
    }

}