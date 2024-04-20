package org.wif3011.project.utility;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;
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

    /**
     * This method using Completion Service which is more efficient and stable in handling large file
     */
    public String concurrentFilterDocs(String document) {
        Timer timer = new Timer();
        timer.start();

        // initial replace document
        document = document.replaceAll(WORD_WITH_APOSTROPHE_REGEX, " ");

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
        int batchSize = (int) Math.ceil((double) stopWords.length / numThreads);

        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        CompletionService<String> completionService = new ExecutorCompletionService<>(executor);

        // based on the numThreads divide the stop word equally and submit to the completion service to run concurrently
        for (int i = 0; i < stopWords.length; i += batchSize) {
            int endIndex = Math.min(i + batchSize, stopWords.length);
            String[] batchStopWords = Arrays.copyOfRange(stopWords, i, endIndex);

            final String finalDocument = document;
            completionService.submit(() -> {
                String result = finalDocument;
                for (String stopWord : batchStopWords) {
                    result = result.replaceAll("\\b" + stopWord + "\\b", "");
                }
                return result;
            });
        }

        // loop to get the last result in completion service after execute
        StringBuilder resultBuilder = new StringBuilder(document);
        for (int i = 0; i < stopWords.length / batchSize; i++) {
            try {
                Future<String> future = completionService.take();
                String replaced = future.get();
                resultBuilder = new StringBuilder(replaced);
            } catch (InterruptedException | ExecutionException e) {
                return null;
            }
        }

        executor.shutdown();

        timer.stop();
        log.info("Process file time: {}", timer.getElapsedTimeMillis());
        return resultBuilder.toString();
    }

    /**
     * This method using atomic references which is more efficient in handling smaller file (stop word and text file)
     */
    private String atomicReferences(String document) {
        Timer timer = new Timer();
        timer.start();

        document = document.replaceAll(WORD_WITH_APOSTROPHE_REGEX, " ");

        try {
            String[] stopWords = Files.readString(STOP_WORD_FILE_PATH).split("\\s+");
            AtomicReference<String> result = new AtomicReference<>(document);

            Arrays.stream(stopWords)
                    .parallel()
                    .forEach(stopWord -> {
                        String currentResult = result.get();
                        String replaced = currentResult.replaceAll("\\b" + stopWord + "\\b", "");
                        result.set(replaced);
                    });

            timer.stop();
            log.info("Process file time: {}", timer.getElapsedTimeMillis());
            return result.get();

        } catch (IOException e) {
            return null;
        }
    }
}
