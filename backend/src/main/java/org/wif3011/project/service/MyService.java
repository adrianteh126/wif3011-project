package org.wif3011.project.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Service
public class MyService {

    private final Set<String> stopWordsSet;

    public MyService() throws IOException {
        Path path = Paths.get("src", "main", "resources", "stop-words.txt");
        String[] stopWords = Files.readString(path).split("\\s+");
        this.stopWordsSet = new HashSet<>(Arrays.asList(stopWords));
    }

    /**
     * Generate word map sequentially from a text file
     *
     * @param file
     * @return Map of words with its frequency
     */
    public Map<String, Integer> getWordMap(MultipartFile file) throws IOException {
        Map<String, Integer> data = new HashMap<>();
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                // turn to lowercase, and remove all symbols & number
                line = line.toLowerCase().replaceAll("[^a-zA-Z']+|(?<![a-zA-Z])'|'(?![a-zA-Z])", " ");
                if (line.isBlank()) continue;
                String[] words = line.trim().split("\\s+");
                for (String word : words) {
                    // remove stop words
                    if (!stopWordsSet.contains(word)) {
                        // update word map
                        data.compute(word, (key, value) -> value == null ? 1 : value + 1);
                    }
                }
            }
        }
        return data;
    }

}
