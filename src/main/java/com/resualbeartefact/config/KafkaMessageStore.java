package com.resualbeartefact.config;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
public class KafkaMessageStore {
    // Map: (topic + groupId) -> message list
    private final ConcurrentMap<String, List<String>> messageMap = new ConcurrentHashMap<>();

    public void addMessage(String topic, String groupId, String message) {
        System.out.println("KafkaMessageStore: Adding message for key: " + topic + ", message: " + message);
        String key = topic + "::" + groupId;
        messageMap.computeIfAbsent(key, k -> Collections.synchronizedList(new LinkedList<>()));
        List<String> messages = messageMap.get(key);
        synchronized (messages) {
            if (messages.size() >= 100) messages.remove(0);
            messages.add(message);
        }
    }

    public List<String> getMessages(String topic, String groupId) {
        return new ArrayList<>(messageMap.getOrDefault(topic + "::" + groupId, Collections.emptyList()));
    }
}
