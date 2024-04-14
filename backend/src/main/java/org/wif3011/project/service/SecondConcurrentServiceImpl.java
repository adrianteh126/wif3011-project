package org.wif3011.project.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.wif3011.project.utility.ProcessFileServiceUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ForkJoinPool;

@Service
@RequiredArgsConstructor
public class SecondConcurrentServiceImpl implements SecondConcurrentService {

    private final ProcessFileServiceUtil processFileUtil;
    private Map<String, Integer> wordMap;

    @Override
    public Map<String, Integer> secondConcurrentWordMap(MultipartFile file) {
        // try with resource
        try{
            String document = processFileUtil.convertFileToString(file);
            document = processFileUtil.concurrentFilterDocs(document);
            wordMap = new HashMap<>();
            int numofThreads = Runtime.getRuntime().availableProcessors();
            String[] words = document.split("\\s+");
            int range = words.length / numofThreads;
            ForkJoinPool pool = new ForkJoinPool();
            wordMap = pool.submit(new WordCountTask(words, 0, range)).get();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return wordMap;
    }
}
