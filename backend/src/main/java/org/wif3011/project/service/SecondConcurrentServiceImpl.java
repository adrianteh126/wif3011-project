package org.wif3011.project.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.wif3011.project.utility.Timer;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ForkJoinPool;

@Service
@RequiredArgsConstructor
public class SecondConcurrentServiceImpl implements SecondConcurrentService {

    private Map<String, Integer> wordMap;

    @Override
    public Map<String, Integer> forkJoinMethod(String document) {
        // try with resource
        try{
            Timer timer = new Timer();
            timer.start();
            wordMap = new HashMap<>();
            String[] words = document.split("\\s+");
            ForkJoinPool pool = ForkJoinPool.commonPool();  // Use the common pool
            WordCountTask task = new WordCountTask(words, 0, words.length);
            wordMap = pool.invoke(task);  // Use invoke to start and wait for the result

            timer.stop();
            System.out.println("time in fork service : " + timer.getElapsedTimeMillis());

        }
        catch (Exception e){
            e.printStackTrace();
        }
        return wordMap;
    }
}
