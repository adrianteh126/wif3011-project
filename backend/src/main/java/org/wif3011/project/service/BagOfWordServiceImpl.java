package org.wif3011.project.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.wif3011.project.utility.ProcessFileServiceUtil;
import org.wif3011.project.utility.Timer;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class BagOfWordServiceImpl implements BagOfWordService {

    private final ProcessFileServiceUtil processFileUtil;
    private final SequentialService sequentialService;
    private final ConcurrentService concurrentService;

    @Override
    public Map<String, Object> sequentialWordCount(MultipartFile file) {
        Map<String, Object> body = new HashMap<>();
        Timer timer = new Timer();
        long totalElapsedTime = 0;

        // File Process Time
        timer.start();
        String document = processFileUtil.convertFileToString(file);
        document = processFileUtil.sequentialFilterDocs(document);
        timer.stop();
        totalElapsedTime += timer.getElapsedTimeMillis();
        body.put("file_processing_time", timer.getElapsedTimeMillis());

        // Algorithm Process Time
        timer.start();
        Map<String, Integer> data = sequentialService.sequentialWordMap(document);
        timer.stop();
        totalElapsedTime += timer.getElapsedTimeMillis();
        body.put("algorithm_processing_time", timer.getElapsedTimeMillis());

        // return
        body.put("elapsed_time", totalElapsedTime);
        body.put("data", data);
        return body;
    }

    @Override
    public Map<String, Object> concurrentWordCount1(MultipartFile file) {
        Map<String, Object> body = new HashMap<>();
        Timer timer = new Timer();
        long totalElapsedTime = 0;

        // File Process Time
        timer.start();
        String document = processFileUtil.convertFileToString(file);
        document = processFileUtil.concurrentFilterDocs(document);
        timer.stop();
        totalElapsedTime += timer.getElapsedTimeMillis();
        body.put("file_processing_time", timer.getElapsedTimeMillis());

        // Algorithm Process Time
        timer.start();
        Map<String, Integer> data = concurrentService.javaStreamMethod(document);
        timer.stop();
        totalElapsedTime += timer.getElapsedTimeMillis();
        body.put("algorithm_processing_time", timer.getElapsedTimeMillis());

        // return
        body.put("elapsed_time", totalElapsedTime);
        body.put("data", data);
        return body;
    }

    @Override
    public Map<String, Object> concurrentWordCount2(MultipartFile file) {
        Map<String, Object> body = new HashMap<>();
        Timer timer = new Timer();
        long totalElapsedTime = 0;

        // File Process Time
        timer.start();
        String document = processFileUtil.convertFileToString(file);
        document = processFileUtil.concurrentFilterDocs(document);
        timer.stop();
        totalElapsedTime += timer.getElapsedTimeMillis();
        body.put("file_processing_time", timer.getElapsedTimeMillis());

        // Algorithm Process Time
        timer.start();
        Map<String, Integer> data = concurrentService.forkJoinMethod(document);
        timer.stop();
        totalElapsedTime += timer.getElapsedTimeMillis();
        body.put("algorithm_processing_time", timer.getElapsedTimeMillis());

        // return
        body.put("elapsed_time", totalElapsedTime);
        body.put("data", data);
        return body;
    }
}
