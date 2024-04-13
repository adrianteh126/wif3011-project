package org.wif3011.project.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface SequentialBOWService {
    Map<String, Integer> sequentialWordMap(MultipartFile file);
}
