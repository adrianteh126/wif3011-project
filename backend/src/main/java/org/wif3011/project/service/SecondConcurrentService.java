package org.wif3011.project.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface SecondConcurrentService {
    Map<String, Integer> secondConcurrentWordMap (MultipartFile file);
}
