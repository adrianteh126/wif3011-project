package org.wif3011.project.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.wif3011.project.service.MyService;
import org.wif3011.project.utility.Timer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class MyController {
    private final MyService myService;

    @Autowired
    public MyController(MyService myService) {
        this.myService = myService;
    }

    @PostMapping("/upload")
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

}
