package org.wif3011.project.utility;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.*;

@Service
public class ProcessFileServiceUtil {

    private final String WORD_WITH_APOSTROPHE_REGEX  = "[^a-zA-Z']+|(?<![a-zA-Z])'|'(?![a-zA-Z])";

    public String convertFileToString (MultipartFile file) {
        try {
            return new String(file.getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String concurrentFilterDocs (String document) {

        document = document.replaceAll(WORD_WITH_APOSTROPHE_REGEX, " ");

        Path stopWordFileName = Path.of("src/main/resources/stop-words.txt");
        try {
            String[] stopWords = Files.readString(stopWordFileName).split("\\s+");
            int numThreads = Runtime.getRuntime().availableProcessors();
            ExecutorService executor = Executors.newFixedThreadPool(numThreads);
            CompletionService<String> completionService = new ExecutorCompletionService<>(executor);

            for (String stopWord : stopWords) {
                String finalDocument = document;
                completionService.submit(() -> finalDocument.replaceAll("\\b" + stopWord + "\\b", ""));
            }

            StringBuilder resultBuilder = new StringBuilder(document);
            for (int i = 0; i < stopWords.length; i++) {
                try {
                    Future<String> future = completionService.take();
                    String replaced = future.get();
                    resultBuilder = new StringBuilder(replaced);
                } catch (InterruptedException | ExecutionException e) {
                    return null;
                }
            }

            executor.shutdown();
            return resultBuilder.toString();
        } catch (IOException e) {
            return null;
        }
    }
}
