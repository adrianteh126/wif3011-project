package org.wif3011.project.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.wif3011.project.utility.ProcessFileServiceUtil;
import org.wif3011.project.utility.Timer;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SecondConcurrentServiceImpl implements SecondConcurrentService {

    private final ProcessFileServiceUtil processFileUtil;

    @Override
    public Map<String, Integer> secondConcurrentWordMap(MultipartFile file) {

        Map<String, Integer> wordMap = new HashMap<>();

        try{
            String document = processFileUtil.convertFileToString(file);
            document = processFileUtil.concurrentFilterDocs(document);

            Timer timer = new Timer();
            timer.start();

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
