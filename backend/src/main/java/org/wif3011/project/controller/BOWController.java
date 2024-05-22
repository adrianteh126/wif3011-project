package org.wif3011.project.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.wif3011.project.service.BagOfWordService;
import org.wif3011.project.service.SequentialBOWService;
import org.wif3011.project.utility.ApiConstant;
import org.wif3011.project.utility.ResponseManipulator;
import org.wif3011.project.utility.Timer;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class BOWController {
    private final SequentialBOWService sequentialBOWService;
    private final BagOfWordService bagOfWordService;

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
    public ResponseEntity<Object> concurrentWordCount1(@RequestBody MultipartFile file,
                                                       @RequestParam("numOfWords") int numOfWords,
                                                       @RequestParam("sortAscending") boolean sortAscending) {
        if (file.isEmpty()) return ResponseEntity.badRequest().body("{\"error\": \"File is empty.\"}");
        if (!file.getOriginalFilename().toLowerCase().endsWith(".txt"))
            return ResponseEntity.badRequest().body("{\"error\": \"Invalid file format.\"}");

        // total process time
        Timer timer = new Timer();
        timer.start();
        Map<String, Object> body = bagOfWordService.concurrentWordCount1(file);
        timer.stop();
        body.put("elapsed_time", timer.getElapsedTimeMillis());

        body.put("data", ResponseManipulator.sortAndLimit((Map<String, Integer>) body.get("data"), numOfWords, sortAscending));
        return ResponseEntity.ok(body);
    }

    @PostMapping(ApiConstant.FORK_JOIN_METHOD)
    public ResponseEntity<Object> concurrentWordCount2(@RequestBody MultipartFile file,
                                                       @RequestParam("numOfWords") int numOfWords,
                                                       @RequestParam("sortAscending") boolean sortAscending) {
        if (file.isEmpty()) return ResponseEntity.badRequest().body("{\"error\": \"File is empty.\"}");
        if (!file.getOriginalFilename().toLowerCase().endsWith(".txt"))
            return ResponseEntity.badRequest().body("{\"error\": \"Invalid file format.\"}");

        // total process time
        Timer timer = new Timer();
        timer.start();
        Map<String, Object> body = bagOfWordService.concurrentWordCount2(file);
        timer.stop();
        body.put("elapsed_time", timer.getElapsedTimeMillis());

        body.put("data", ResponseManipulator.sortAndLimit((Map<String, Integer>) body.get("data"), numOfWords, sortAscending));
        return ResponseEntity.ok(body);
    }

    @PostMapping(ApiConstant.COMPARISON_BOW)
    public ResponseEntity<Object> comparisonBOW(@RequestBody MultipartFile file){
        if (file.isEmpty()) return ResponseEntity.badRequest().body("{\"error\": \"File is empty.\"}");
        if (!file.getOriginalFilename().toLowerCase().endsWith(".txt"))
            return ResponseEntity.badRequest().body("{\"error\": \"Invalid file format.\"}");

        Map<String, Object> body = new HashMap<>();
        Map<String, Long> sequentialElapsedTime = new HashMap<>();
        Map<String, Long> javaStreamElapsedTime = new HashMap<>();
        Map<String, Long> forkJoinElapsedTime = new HashMap<>();

        Timer timer = new Timer();
        for(int i = 0; i < 3; i++){
            timer.start();
            sequentialBOWService.sequentialWordMapCompare(file);
            timer.stop();
            sequentialElapsedTime.put(String.format("elapsed_time_%1$s", i + 1), timer.getElapsedTimeMillis());
        }
        for(int i = 0; i < 3; i++){
            timer.start();
            bagOfWordService.concurrentWordCount1(file);
            timer.stop();
            javaStreamElapsedTime.put(String.format("elapsed_time_%1$s", i + 1), timer.getElapsedTimeMillis());
        }
        for(int i = 0; i < 3; i++){
            timer.start();
            bagOfWordService.concurrentWordCount2(file);
            timer.stop();
            forkJoinElapsedTime.put(String.format("elapsed_time_%1$s", i + 1), timer.getElapsedTimeMillis());
        }
        body.put("sequential", sequentialElapsedTime);
        body.put("javaStream", javaStreamElapsedTime);
        body.put("forkJoin", forkJoinElapsedTime);
        return ResponseEntity.ok(body);
    }
}
