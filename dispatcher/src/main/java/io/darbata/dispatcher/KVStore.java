package io.darbata.dispatcher;

public interface KVStore {
    void put(String partitionKey, String sortKey, Chat value);
}