package org.wif3011.project.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface BagOfWordService {
    Map<String, Object> sequentialWordCount(MultipartFile file);

    Map<String, Object> concurrentWordCount1(MultipartFile file);

    Map<String, Object> concurrentWordCount2(MultipartFile file);
}
