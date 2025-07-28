package com.resualbeartefact.controller;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.resualbeartefact.config.KafkaMessageStore;
import com.resualbeartefact.data.*;
import com.resualbeartefact.service.DynamicKafkaConsumerService;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@RestController
@RequestMapping("kafka/")
@CrossOrigin(origins = "*")
public class KafkaController {
    @Autowired
    private DynamicKafkaConsumerService consumerService;
    @Autowired
    private KafkaMessageStore kafkaMessageStore;
    @PostMapping(value = "/send-to-kafka", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> receiveData(
            @RequestPart(value = "jsonFile", required = false) MultipartFile jsonFile,
            @RequestPart(value = "excelFile", required = false) MultipartFile excelFile,
            @RequestPart(value = "sql",required = false) SqlInfo sqlInfo,
            @RequestPart("kafkaConfig") KafkaConfig kafkaConfig
    )  {
            try {
                List<Object> payloads = new ArrayList<>();
                if(jsonFile != null ) {
                    JsonMessageSource jsonMessageSource = new JsonMessageSource(jsonFile);
                    payloads.addAll(jsonMessageSource.getMessages());
                }
                if(excelFile != null) {
                    ExcelMessageSource excelMessageSource = new ExcelMessageSource(excelFile);
                    payloads.addAll(excelMessageSource.getMessages());
                }
                // Access SQL info
                if(sqlInfo != null) {
                    SqlMessageSource sqlMessageSource = new SqlMessageSource(sqlInfo);
                    payloads.addAll(sqlMessageSource.getMessages());
                }
                System.out.println("Total payloads count: " + payloads.size());
                if (!payloads.isEmpty()) {
                    System.out.println("Sample payload 0: " + payloads.get(0).getClass().getName() + " -> " + payloads.get(0).toString());
                    if (payloads.size() > 1) {
                        System.out.println("Sample payload 1: " + payloads.get(1).getClass().getName() + " -> " + payloads.get(1).toString());
                    }
                }
                System.out.println("Kafka Topic: " + kafkaConfig.getTopic());
                consumerService.startListener(kafkaConfig.getTopic(), kafkaConfig.getGroupName(), kafkaConfig.getBootstrapServers(), kafkaConfig.getAdditionalConfig() != null ? kafkaConfig.getAdditionalConfig() : null);

                sendToKafka(kafkaConfig, payloads);
                // TODO: send to Kafka or further processing...
                return ResponseEntity.ok("Data received successfully");
            } catch (Exception e) {
                e.printStackTrace();
                return ResponseEntity.status(500).body("Error processing request");
            }
        }
    @GetMapping("/messages")
    public ResponseEntity<?> getMessages(
            @RequestParam("topic") String topic,
            @RequestParam("groupId") String groupId
    ) {
        List<String> messages = kafkaMessageStore.getMessages(topic, groupId);
        return ResponseEntity.ok(messages);
    }
    private void sendToKafka(KafkaConfig config, List<Object> payloads) {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, config.getBootstrapServers());
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");

        // Handle security configs from API key/secret/SSL if needed
//        if (config.getApiKey() != null && config.getApiSecret() != null) {
//            // Example for SASL config - adjust as needed
//            props.put("security.protocol", "SASL_SSL");
//            props.put("sasl.mechanism", "PLAIN");
//            String jaasCfg = String.format(
//                    "org.apache.kafka.common.security.plain.PlainLoginModule required username=\"%s\" password=\"%s\";",
//                    config.getApiKey(), config.getApiSecret()
//            );
//            props.put("sasl.jaas.config", jaasCfg);
//        }

        // Parse additionalConfig JSON string into Properties and add
        if (config.getAdditionalConfig() != null && !config.getAdditionalConfig().isEmpty()) {
            try {
                Map<String, String> extraProps = new ObjectMapper().readValue(config.getAdditionalConfig(), new TypeReference<Map<String,String>>(){});
                extraProps.forEach(props::put);
            } catch (Exception e) {
                throw new RuntimeException("Invalid additionalConfig JSON: " + e.getMessage());
            }
        }

        Producer<String, String> producer = new KafkaProducer<>(props);
        String topic = config.getTopic();

        for (Object payload : payloads) {
            try {
                String value = new ObjectMapper().writeValueAsString(payload);
                System.out.println("Payload object class: " + payload.getClass().getName());
                System.out.println("Serialized value length: " + value.length() + " bytes");
                if (value.length() > 1000) { // Print only for larger values
                    System.out.println("Sample serialized value (first 200 chars): " + value.substring(0, Math.min(value.length(), 200)));
                }
                ProducerRecord<String, String> record = new ProducerRecord<>(topic, value);
                producer.send(record);
            } catch (Exception e) {
                System.err.println("Failed to send message to Kafka: " + e.getMessage());
            }
        }
        producer.flush();
        producer.close();
    }
}
