package com.resualbeartefact.service;

import com.resualbeartefact.config.KafkaMessageStore;
import jakarta.annotation.PreDestroy;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class DynamicKafkaConsumerService {
    private final KafkaMessageStore store;
    private final Map<String, MessageListenerContainer> containerMap = new ConcurrentHashMap<>();

    @Autowired
    public DynamicKafkaConsumerService(KafkaMessageStore store) {
        this.store = store;
    }

    public void startListener(String topic, String groupId, String bootstrapServers, String extraProps) {
        String key = topic + "::" + groupId;
        if (containerMap.containsKey(key)) return; // Listener already running

        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
//        props.put(extraProps);

        DefaultKafkaConsumerFactory<String, String> cf = new DefaultKafkaConsumerFactory<>(props);

        ContainerProperties containerProps = new ContainerProperties(topic);
        containerProps.setMessageListener((MessageListener<String, String>) record -> {
            System.out.println("Consumer received message from topic: " + record.topic() + ", offset: " + record.offset() + ", value: " + record.value());
            store.addMessage(topic, groupId, record.value());
        });

        ConcurrentMessageListenerContainer<String, String> container =
                new ConcurrentMessageListenerContainer<>(cf, containerProps);
        container.start();
        containerMap.put(key, container);
    }

    @PreDestroy
    public void shutdown() {
        for (MessageListenerContainer container : containerMap.values()) {
            container.stop();
        }
    }
}

