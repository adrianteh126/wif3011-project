package org.wif3011.project.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.wif3011.project.utility.ProcessFileServiceUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class SequentialServiceImpl implements SequentialService {

    private final ProcessFileServiceUtil processFileUtil;

    @Override
    public Map<String, Integer> sequentialWordMap(String document) {
        Map<String, Integer> data = new HashMap<>();
        Stream.of(document.split("\\s+")).forEach((word) -> {
            data.compute(word, (key, value) -> value == null ? 1 : value + 1);
        });
        return data;
    }

    @Override
    public Map<String, Integer> sequentialWordMapCompare(MultipartFile file) {
        String document = processFileUtil.convertFileToString(file);
        Map<String, Integer> data = processFileUtil.sequentialGenerateWordMap(document);

        return data;
    }
}