package com.resualbeartefact.data;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JsonMessageSource {
    private MultipartFile jsonFile;

    public JsonMessageSource(MultipartFile jsonFile) {
        this.jsonFile = jsonFile;
    }
    public List<Map<String, Object>> getMessages() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        // Extract JSON file data
        List<Map<String, Object>> jsonData = new ArrayList<>();
        if (jsonFile != null && !jsonFile.isEmpty()) {
            try (InputStream is = jsonFile.getInputStream()) {
                jsonData = objectMapper.readValue(is, new TypeReference<List<Map<String, Object>>>() {});
                System.out.println("JSON Data: " + jsonData);
            } catch (IOException e) {
//                return ResponseEntity.badRequest().body("Invalid JSON file");
            }
        }
        return jsonData;
    }
}

