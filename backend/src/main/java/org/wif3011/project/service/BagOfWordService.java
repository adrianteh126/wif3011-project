package org.wif3011.project.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface BagOfWordService {
    Map<String, Integer> concurrentWordCount1 (MultipartFile file);
}
