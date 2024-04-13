package org.wif3011.project.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.wif3011.project.dto.MyRequest;
import org.wif3011.project.service.BagOfWordService;
import org.wif3011.project.service.MyService;
import org.wif3011.project.utility.ApiConstant;
import org.wif3011.project.utility.Timer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MyController {
    private final MyService myService;
    private final BagOfWordService bagOfWordService;


    @PostMapping(ApiConstant.UPLOAD)
    public ResponseEntity<Object> uploadFile(@RequestBody MultipartFile file) {
        try {
            if (file.isEmpty()) return ResponseEntity.badRequest().body("{\"error\": \"File is empty.\"}");
            if (!file.getOriginalFilename().toLowerCase().endsWith(".txt"))
                return ResponseEntity.badRequest().body("{\"error\": \"Invalid file format.\"}");

            Timer timer = new Timer();
            timer.start();
            Map<String, Integer> wordMap = myService.getWordMap(file);
            timer.stop();
            Map<String, Object> body = new HashMap<>();
            body.put("elapsed_time", timer.getElapsedTimeMillis());
            body.put("data", wordMap);
            return new ResponseEntity<>(body, HttpStatus.OK);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/hello")
    public String getHelloMessage(@RequestBody MyRequest request) {
        String name = request.getName();
        String message = myService.generateHelloMessage(name);
        return "{\"message\":\"" + message + "\"}";
    }

    @PostMapping(ApiConstant.JAVA_STREAM_METHOD)
    public ResponseEntity<Object> concurrentWordCount1(@RequestBody MultipartFile file) {
        return ResponseEntity.ok(bagOfWordService.concurrentWordCount1(file));
    }
}
