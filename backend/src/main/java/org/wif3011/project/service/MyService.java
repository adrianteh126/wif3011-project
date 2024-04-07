package org.wif3011.project.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.*;

@Service
public class MyService {
    /**
     * Generate word map sequentially from a text file
     * @param file
     * @return Map of words with its frequency
     */
    public Map<String, Integer> getWordMap(MultipartFile file) throws IOException {
        // generate a map to store frequency of words
        Map<String, Integer> data = new HashMap<>();
        // read lines sequentially
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(file.getInputStream()));
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            if (line.isEmpty()) continue;
            line = line.toLowerCase().replaceAll("[^a-z\\s]", " "); // turn string to lowercase and remove all symbols
            String[] words = line.split("\\s+");
            for (String word : words) {
                data.put(word, data.getOrDefault(word, 0) + 1);
            }
        }
        return data;
    }

    public String generateHelloMessage(String name) {
        return "Hello "+ name;
    }

}
