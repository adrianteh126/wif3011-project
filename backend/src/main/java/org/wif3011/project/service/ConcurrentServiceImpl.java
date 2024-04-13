package org.wif3011.project.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class ConcurrentServiceImpl implements ConcurrentService{

    @Override
    public Map<String, Integer> javaStreamMethod(String document) {
        ConcurrentHashMap<String, Integer> bagOfWords = new ConcurrentHashMap<>();

        Stream.of(document.split("\\s+"))
                .parallel()
                .forEach(word -> bagOfWords.merge(word.toLowerCase(), 1, Integer::sum));

        return bagOfWords;
    }
}
