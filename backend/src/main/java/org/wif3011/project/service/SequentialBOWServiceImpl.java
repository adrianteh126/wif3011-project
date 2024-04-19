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
public class SequentialBOWServiceImpl implements SequentialBOWService {

    private final ProcessFileServiceUtil processFileUtil;

    @Override
    public Map<String, Object> sequentialWordMap(MultipartFile file) {
        long totalElapsedTime = 0;
        Timer timer = new Timer();

        timer.start();
        String document = processFileUtil.convertFileToString(file);
        timer.stop();
        totalElapsedTime += timer.getElapsedTimeMillis();


        timer.start();
        Map<String, Integer> data = processFileUtil.sequentialGenerateWordMap(document);
        timer.stop();
        totalElapsedTime += timer.getElapsedTimeMillis();
        
        Map<String, Object> body = new HashMap<>();
        body.put("elapsed_time", totalElapsedTime);
        body.put("data", data);
        return body;
    }
}