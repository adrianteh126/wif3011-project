package org.wif3011.project.service;

import java.util.*;
import java.util.concurrent.RecursiveTask;

public class WordCountTask extends RecursiveTask<Map<String, Integer>> {
    private final String[] words;
    private final int current;
    private final int chunkSize;

    public WordCountTask(String[] words, int current, int chunkSize) {
        this.words = words;
        this.current = current;
        this.chunkSize = chunkSize;
    }

    @Override
    protected Map<String, Integer> compute() {
        List<WordCountTask> tasks = new ArrayList<>();
        Map<String, Integer> wordCountMap = new HashMap<>();
        String [] temp = new String[chunkSize];
        try {
            if(current + chunkSize >= words.length){
                System.arraycopy(words, current, temp, 0, words.length - current);
            }
            else{
                System.arraycopy(words, current, temp, 0, chunkSize);
                tasks.add(new WordCountTask(words, current + chunkSize, chunkSize));
            }

            for (String word : temp) {
                if(word == null){
                    continue;
                }
                if (!word.isEmpty()) {
                    wordCountMap.put(word, wordCountMap.getOrDefault(word, 0) + 1);
                }
            }
            for (WordCountTask task : tasks) {
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