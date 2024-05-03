package org.wif3011.project.utility;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Stream;

@Service
@Slf4j
public class ProcessFileServiceUtil {

    private final Path STOP_WORD_FILE_PATH = Path.of("src/main/resources/stop-words.txt");
    private final String WORD_WITH_APOSTROPHE_REGEX = "[^a-zA-Z']+|(?<![a-zA-Z])'|'(?![a-zA-Z])";

    public String convertFileToString(MultipartFile file) {
        try {
            return new String(file.getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Map<String, Integer> sequentialGenerateWordMap(String document) {
        try {
            String[] stopWords = Files.readString(STOP_WORD_FILE_PATH).split("\\s+");
            Map<String, Integer> data = new HashMap<>();

            document = document.toLowerCase()
                    .replaceAll(WORD_WITH_APOSTROPHE_REGEX, " ")
                    .replaceAll("\\b(" + String.join("|", stopWords) + ")\\b", "")
                    .trim();

            Stream.of(document.split("\\s+")).forEach((word) -> {
                // update word map
                data.compute(word, (key, value) -> value == null ? 1 : value + 1);
            });
            return data;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String concurrentFilterDocs(String document) {
        try {
            Timer timer = new Timer();
            timer.start();

            // initial replace document
            document = document.toLowerCase().replaceAll(WORD_WITH_APOSTROPHE_REGEX, " ");

            // get the stop word from file concurrently
            CompletableFuture<String[]> stopWordsFuture = CompletableFuture.supplyAsync(() -> {
                try {
                    return Files.readString(STOP_WORD_FILE_PATH).split("\\s+");
                } catch (IOException e) {
                    return new String[0];
                }
            });
            String[] stopWords = stopWordsFuture.join();

            int numThreads = Runtime.getRuntime().availableProcessors();
            ExecutorService executor = Executors.newFixedThreadPool(numThreads);

            List<Future<String>> futures = new ArrayList<>();
            String[] words = document.split("\\s+");
            int wordsPerChunk = words.length / numThreads;
            int start = 0;
            for (int i = 0; i < numThreads; i++) {
                int end = (i == numThreads - 1) ? words.length :start + wordsPerChunk ;
                String chunk = String.join(" ", Arrays.copyOfRange(words, start, end));
                Future<String> future = executor.submit(() -> removeStopWords(chunk, stopWords));
                futures.add(future);
                start = end;
            }

            StringBuilder resultBuilder = new StringBuilder();
            for (Future<String> future : futures) {
                resultBuilder.append(future.get());
            }

            executor.shutdown();
            timer.stop();
            log.info("Process file time: {}", timer.getElapsedTimeMillis());
            return resultBuilder.toString();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * Remove stop words by chunk and return string
     */
    private String removeStopWords(String text, String[] stopWords) {
        StringBuilder resultBuilder = new StringBuilder();
        for (String word : text.split("\\s+")) {
            boolean isStopWord = false;
            for (String stopWord : stopWords) {
                if (word.equals(stopWord)) {
                    isStopWord = true;
                    break;
                }
            }
            if (!isStopWord) {
                resultBuilder.append(word).append(" ");
            }
        }
        return resultBuilder.toString();
    }
}
