package org.wif3011.project.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.wif3011.project.service.BagOfWordService;
import org.wif3011.project.service.SecondConcurrentService;
import org.wif3011.project.service.SequentialBOWService;
import org.wif3011.project.utility.ApiConstant;
import org.wif3011.project.utility.Timer;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class BOWController {
    private final SequentialBOWService sequentialBOWService;
    private final BagOfWordService bagOfWordService;
    private final SecondConcurrentService secondConcurrentService;

    @PostMapping(ApiConstant.SEQUENTIAL_BOW)
    public ResponseEntity<Object> getSequentialWordMap(
            @RequestParam("numOfWords") int numOfWords,
            @RequestParam("sortAscending") boolean sortAscending,
            @RequestBody MultipartFile file) {

        if (file.isEmpty()) return ResponseEntity.badRequest().body("{\"error\": \"File is empty.\"}");
        if (!file.getOriginalFilename().toLowerCase().endsWith(".txt"))
            return ResponseEntity.badRequest().body("{\"error\": \"Invalid file format.\"}");

        Map<String, Object> body = sequentialBOWService.sequentialWordMap(file, numOfWords, sortAscending);
        return ResponseEntity.ok(body);
    }

    @PostMapping(ApiConstant.JAVA_STREAM_METHOD)
    public ResponseEntity<Object> concurrentWordCount1(@RequestBody MultipartFile file) {
        Timer timer = new Timer();
        timer.start();
        Map<String, Integer> wordMap = bagOfWordService.concurrentWordCount1(file);
        timer.stop();

        Map<String, Object> body = new HashMap<>();
        body.put("elapsed_time", timer.getElapsedTimeMillis());
        body.put("data", wordMap);
        return ResponseEntity.ok(body);
    }

    @PostMapping(ApiConstant.FORK_JOIN_METHOD)
    public ResponseEntity<Object> concurrentWordCount2(@RequestBody MultipartFile file) {
        if (file.isEmpty()) return ResponseEntity.badRequest().body("{\"error\": \"File is empty.\"}");
        if (!file.getOriginalFilename().toLowerCase().endsWith(".txt"))
            return ResponseEntity.badRequest().body("{\"error\": \"Invalid file format.\"}");

        Timer timer = new Timer();
        timer.start();
        Map<String, Integer> wordMap = secondConcurrentService.secondConcurrentWordMap(file);
        timer.stop();

        Map<String, Object> body = new HashMap<>();
        body.put("elapsed_time", timer.getElapsedTimeMillis());
        body.put("data", wordMap);
        return ResponseEntity.ok(body);
    }
}
