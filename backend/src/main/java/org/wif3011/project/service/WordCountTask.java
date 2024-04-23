package org.wif3011.project.service;

import java.util.*;
import java.util.concurrent.RecursiveTask;

public class WordCountTask extends RecursiveTask<Map<String, Integer>> {
    private final String[] words;
    private final int start;
    private final int end;

    private static final int THRESHOLD = 1000; // Adjust based on your benchmarking

    public WordCountTask(String[] words, int start, int end) {
        this.words = words;
        this.start = start;
        this.end = end;
    }

    @Override
    protected Map<String, Integer> compute() {
        int length = end - start;
        if (length <= THRESHOLD) {
            Map<String, Integer> localWordCount = new HashMap<>();
            for (int i = start; i < end; i++) {
                String word = words[i];
                if (word != null && !word.isEmpty()) {
                    String lowerCaseWord = word.toLowerCase(); // Convert word to lowercase
                    localWordCount.merge(lowerCaseWord, 1, Integer::sum);
                }
            }
            return localWordCount;
        } else {
            int mid = start + length / 2;
            WordCountTask left = new WordCountTask(words, start, mid);
            WordCountTask right = new WordCountTask(words, mid, end);

            left.fork(); // Asynchronously execute the left task
            Map<String, Integer> rightResult = right.compute(); // Compute the right part synchronously
            Map<String, Integer> leftResult = left.join(); // Wait and retrieve the left result

            // Efficient map merging
            rightResult.forEach((key, value) -> leftResult.merge(key, value, Integer::sum));
            return leftResult;
        }
    }
}
