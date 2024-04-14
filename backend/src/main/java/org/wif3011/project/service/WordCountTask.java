package org.wif3011.project.service;

import java.util.*;
import java.util.concurrent.RecursiveTask;

public class WordCountTask extends RecursiveTask<Map<String, Integer>> {
    private final String[] words;
    private final int chunkSize;

    public WordCountTask(String[] words, int chunkSize) {
        this.words = words;
        this.chunkSize = chunkSize;
    }

    @Override
    protected Map<String, Integer> compute() {
        List<WordCountTask> tasks = new ArrayList<>();
        Map<String, Integer> wordCountMap = new HashMap<>();

        try {
            String [] temp = new String[chunkSize];
            System.arraycopy(words, 0, temp, 0, chunkSize);
            tasks.add(new WordCountTask(temp, chunkSize));
            for (String word : words) {
                word = word.toLowerCase().replaceAll("[^a-zA-Z]", "");

                // do filtering, checking -> check if the word is not empty and not include stopwords
                if (!word.isEmpty()) {
                    // defaulting to 0 if it's not yet in the map, and then increments this count by 1
                    // add into wordCountMap and increase the count
                    wordCountMap.put(word, wordCountMap.getOrDefault(word, 0) + 1);
                }
            }
            //  loops through all subtasks added to the tasks list and starts their asynchronous execution in the background
            for (WordCountTask task : tasks) {
                // submit task to task pool
                task.fork();
            }
            for (WordCountTask task : tasks) {
                wordCountMap.putAll(task.join());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return wordCountMap;
    }
}