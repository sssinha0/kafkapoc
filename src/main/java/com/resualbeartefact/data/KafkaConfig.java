package com.resualbeartefact.data;

import lombok.Data;

@Data
public class KafkaConfig {
    private String topic;
    private String bootstrapServers;
    private String apiKey;
    private String apiSecret;
    private Boolean useSSL;
    private String groupName;
    private String additionalConfig;
}

