package org.wif3011.project.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.wif3011.project.utility.ProcessFileServiceUtil;
import org.wif3011.project.utility.ResponseManipulator;
import org.wif3011.project.utility.Timer;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class SequentialBOWServiceImpl implements SequentialBOWService {

    private final ProcessFileServiceUtil processFileUtil;

    @Override
    public Map<String, Object> sequentialWordMap(MultipartFile file, int numOfWords, boolean sortAscending) {
        long totalElapsedTime = 0;
        Map<String, Object> body = new HashMap<>();
        Timer timer = new Timer();

        timer.start();
        String document = processFileUtil.convertFileToString(file);
        document = processFileUtil.sequentialFilterDocs(document);
        timer.stop();
        totalElapsedTime += timer.getElapsedTimeMillis();
        body.put("file_processing_time", timer.getElapsedTimeMillis());

        timer.start();
        // add words into word map sequentially
        Map<String, Integer> data = new HashMap<>();
        Stream.of(document.split("\\s+")).forEach((word) -> {
            // update word map
            data.compute(word, (key, value) -> value == null ? 1 : value + 1);
        });
        timer.stop();
        totalElapsedTime += timer.getElapsedTimeMillis();
        body.put("algorithm_processing_time", timer.getElapsedTimeMillis());

        body.put("elapsed_time", totalElapsedTime);
        body.put("data", ResponseManipulator.sortAndLimit(data, numOfWords, sortAscending));
        return body;
    }

    @Override
    public Map<String, Integer> sequentialWordMapCompare(MultipartFile file){
        String document = processFileUtil.convertFileToString(file);
        Map<String, Integer> data = processFileUtil.sequentialGenerateWordMap(document);

        return data;
    }
}