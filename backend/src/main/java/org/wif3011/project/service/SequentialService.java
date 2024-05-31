package org.wif3011.project.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface SequentialService {
    Map<String, Integer> sequentialWordMap(String document);

    Map<String, Integer> sequentialWordMapCompare(MultipartFile file);
}
